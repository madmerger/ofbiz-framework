package org.apache.ofbiz.migration.equivalence;

import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EquivalenceController {
    private final MigrationServiceDispatcher dispatcher;

    public EquivalenceController(MigrationServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping("/api/equivalence/{component}/services/{service}")
    public Map<String, Object> execute(@PathVariable("component") String component, @PathVariable("service") String service,
            @RequestBody Map<String, Object> input) {
        return dispatcher.execute(component, service, input);
    }
}
