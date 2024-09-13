package nasaltsev.appliedChallenge.controller;

import jakarta.validation.Valid;
import nasaltsev.appliedChallenge.model.ConsumptionRequest;
import nasaltsev.appliedChallenge.model.ConsumptionResponse;
import nasaltsev.appliedChallenge.service.EmissionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/v1/emissions/")
public class EmissionsController {

    private final EmissionsService emissionsService;

    public EmissionsController(EmissionsService emissionsService) {
        this.emissionsService = emissionsService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<List<ConsumptionResponse>> calculateEmissions(
            @RequestBody @Valid List<ConsumptionRequest> requests) {
        List<ConsumptionResponse> response = emissionsService.calculateCarbonFootprint(requests);
        return ResponseEntity.ok(response);
    }
}
