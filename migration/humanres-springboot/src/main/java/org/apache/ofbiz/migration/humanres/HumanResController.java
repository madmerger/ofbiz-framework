package org.apache.ofbiz.migration.humanres;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/humanres")
public class HumanResController {
    private final HumanResServiceDispatcher serviceDispatcher;
    private final EntityQueryService entityQueryService;

    public HumanResController(
            HumanResServiceDispatcher serviceDispatcher, EntityQueryService entityQueryService) {
        this.serviceDispatcher = serviceDispatcher;
        this.entityQueryService = entityQueryService;
    }

    @PostMapping("/services/{serviceName}")
    public Map<String, Object> execute(
            @PathVariable String serviceName, @RequestBody Map<String, Object> input) {
        return success(serviceDispatcher.execute(serviceName, input));
    }

    @PostMapping("/services/{serviceName}/multi")
    public Map<String, Object> executeMulti(
            @PathVariable String serviceName, @RequestBody List<Map<String, Object>> rows) {
        List<Map<String, Object>> results = serviceDispatcher.executeMulti(serviceName, rows);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("responseMessage", "success");
        response.put("results", results);
        return response;
    }

    @GetMapping("/entities/{entityName}")
    public Map<String, Object> list(
            @PathVariable String entityName,
            @RequestParam Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw HumanResException.badRequest("page must be non-negative and size must be between 1 and 100");
        }
        Map<String, Object> response = entityQueryService.list(entityName, filters, page, size);
        response.put("responseMessage", "success");
        return response;
    }

    @GetMapping("/entities/{entityName}/one")
    public Map<String, Object> one(
            @PathVariable String entityName, @RequestParam Map<String, String> primaryKeys) {
        Object entity = entityQueryService.one(entityName, primaryKeys);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("responseMessage", "success");
        response.put("result", entity);
        return response;
    }

    @GetMapping("/hr-tree")
    public Map<String, Object> hrTree(@RequestParam String partyId) {
        List<Map<String, Object>> tree = serviceDispatcher.getHrTree(partyId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("responseMessage", "success");
        response.put("hrTree", tree);
        return response;
    }

    @PostMapping("/internal-orgs")
    public Map<String, Object> createInternalOrg(@RequestBody Map<String, Object> input) {
        return success(serviceDispatcher.execute("createInternalOrg", input));
    }

    @DeleteMapping("/internal-orgs")
    public Map<String, Object> removeInternalOrg(@RequestParam Map<String, String> input) {
        return success(serviceDispatcher.execute("removeInternalOrg", new LinkedHashMap<>(input)));
    }

    @PostMapping("/public-holidays")
    public Map<String, Object> createPublicHoliday(@RequestBody Map<String, Object> input) {
        return success(serviceDispatcher.execute("createPublicHoliday", input));
    }

    private static Map<String, Object> success(Map<String, Object> values) {
        Map<String, Object> response = new LinkedHashMap<>(values);
        response.put("responseMessage", "success");
        return response;
    }
}
