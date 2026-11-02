import groovy.json.JsonOutput
import java.net.InetAddress
import java.sql.Timestamp
import java.time.Instant
import org.apache.ofbiz.entity.transaction.TransactionUtil

response.contentType = 'application/json'
String token = System.getenv('EQUIV_PASSWORD')
if (!InetAddress.getByName(request.remoteAddr).loopbackAddress || !token || request.getHeader('X-Equivalence-Token') != token) {
    response.status = 403
    response.writer.write(JsonOutput.toJson([responseMessage: 'error', errorMessage: 'Local harness authentication required']))
    return 'error'
}
try {
    def body = ['service', 'input', 'runAs', 'entityLists'].collectEntries { [(it): request.getAttribute(it)] }
    def input = new LinkedHashMap(body.input ?: [:])
    def model = dispatcher.dispatchContext.getModelService(body.service)
    input.userLogin = delegator.findOne('UserLogin', [userLoginId: body.runAs], false)
    input.locale = Locale.US
    input.timeZone = TimeZone.getTimeZone('UTC')
    model.getInParamNames().each { name ->
        if (input[name] != null && model.getParam(name).type == 'java.sql.Timestamp' && input[name] instanceof String) {
            input[name] = Timestamp.from(Instant.parse(input[name]))
        }
    }
    (body.entityLists ?: [:]).each { field, entity ->
        input[field] = (input[field] ?: []).withIndex().collect { values, index ->
            def value = delegator.makeValue(entity instanceof List ? entity[index] : entity)
            values.each { name, item ->
                if (item == null) value.set(name, null)
                else value.setString(name, item.toString())
            }
            value
        }
    }
    def result = dispatcher.runSync(body.service, input)
    response.writer.write(JsonOutput.toJson(result))
} catch (Exception exception) {
    if (TransactionUtil.isTransactionInPlace()) {
        TransactionUtil.rollback()
    }
    response.writer.write(JsonOutput.toJson([responseMessage: 'error', errorMessage: exception.message]))
}
return 'success'
