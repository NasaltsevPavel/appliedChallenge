package nasaltsev.appliedChallenge.service;

import nasaltsev.appliedChallenge.exceptions.EnergySourceNotFoundException;
import nasaltsev.appliedChallenge.exceptions.SubScopeNotFoundException;
import nasaltsev.appliedChallenge.model.ConsumptionRequest;
import nasaltsev.appliedChallenge.model.ConsumptionResponse;
import nasaltsev.appliedChallenge.model.EnergyScope;
import nasaltsev.appliedChallenge.model.EnergySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EmissionsService {

    private final ExternalAPIService externalAPIService;

    public EmissionsService(ExternalAPIService externalAPIService) {
        this.externalAPIService = externalAPIService;
    }

    public List<ConsumptionResponse> calculateCarbonFootprint(List<ConsumptionRequest> consumptionRequest) {
        List<EnergyScope> scopes = externalAPIService.getScopes();
        List<ConsumptionResponse> consumptionResponseList = mapScopeToConsumptionResponse(scopes);

        Map<String, Integer> subScopeCounters = new HashMap<>();

        for (ConsumptionRequest request : consumptionRequest) {
            ConsumptionResponse responseData = calculateEnergyAndCO2(
                    request.getEnergySourceId(),
                    request.getConsumption(),
                    request.getEmissionFactor()
            );

            EnergyScope matchedScope = scopes.stream()
                    .flatMap(scope -> scope.getSubScopes().stream())
                    .filter(subScope -> subScope.getId().equals(responseData.getName()))
                    .findFirst()
                    .orElseThrow(() -> new SubScopeNotFoundException("SubScope with ID " + responseData.getName() + " not found"));

            subScopeCounters.putIfAbsent(matchedScope.getName(), 0);
            int n = subScopeCounters.get(matchedScope.getName()) + 1;
            subScopeCounters.put(matchedScope.getName(), n);

            responseData.setName(matchedScope.getName() + "." + n);
            responseData.setLabel(responseData.getLabel() + " (" + request.getDescription() + ")");

            addRequestToResponseList(consumptionResponseList, responseData, matchedScope.getName());
        }

        return calculateTotals(consumptionResponseList);
    }


    private ConsumptionResponse calculateEnergyAndCO2(String energySourceId, BigDecimal consumption, BigDecimal customEmissionFactor) {
        List<EnergySource> energySources = externalAPIService.getEnergySources();

        EnergySource verifiedEnergySource = energySources.stream()
                .filter(source -> source.getEnergySourceId().equals(energySourceId))
                .findFirst()
                .orElseThrow(() -> new EnergySourceNotFoundException("Energy source with ID: " + energySourceId + " not found"));

        BigDecimal conversionFactor = verifiedEnergySource.getConversionFactor();
        BigDecimal emissionFactor = customEmissionFactor != null ? customEmissionFactor : verifiedEnergySource.getEmissionFactor();

        BigDecimal energy = consumption.multiply(conversionFactor).setScale(2, RoundingMode.HALF_UP);

        BigDecimal co2 = energy.multiply(emissionFactor).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

        return new ConsumptionResponse(verifiedEnergySource.getScopeId(), verifiedEnergySource.getName(), energy, co2);
    }

    private List<ConsumptionResponse> mapScopeToConsumptionResponse(List<EnergyScope> energyScopes) {
        return energyScopes.stream().map(scope -> {
            ConsumptionResponse consumptionResponse =
                    new ConsumptionResponse(scope.getName(), scope.getLabel(), BigDecimal.ZERO, BigDecimal.ZERO);

            List<ConsumptionResponse> children = scope.getSubScopes().stream()
                    .map(subScope -> new ConsumptionResponse(subScope.getName(), subScope.getLabel(), BigDecimal.ZERO, BigDecimal.ZERO))
                    .toList();

            consumptionResponse.setChildren(children);
            return consumptionResponse;
        }).toList();
    }

    private void addRequestToResponseList(List<ConsumptionResponse> responseList, ConsumptionResponse consumptionResponse, String name) {
        responseList.forEach(scope -> scope.getChildren().stream()
                .filter(subScope -> subScope.getName().equals(name))
                .findFirst()
                .ifPresent(subScope -> subScope.getChildren().add(consumptionResponse)));
    }

    private List<ConsumptionResponse> calculateTotals(List<ConsumptionResponse> consumptionResponseList) {
        for (ConsumptionResponse scope : consumptionResponseList) {
            BigDecimal totalEnergy = scope.getChildren().stream()
                    .map(this::calculateChildTotalEnergy)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCO2 = scope.getChildren().stream()
                    .map(this::calculateChildTotalCO2)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            scope.setEnergy(totalEnergy);
            scope.setCo2(totalCO2);
        }
        return consumptionResponseList;
    }

    private BigDecimal calculateChildTotalEnergy(ConsumptionResponse child) {
        BigDecimal childTotalEnergy = child.getEnergy() != null ? child.getEnergy() : BigDecimal.ZERO;

        for (ConsumptionResponse grandChild : child.getChildren()) {
            childTotalEnergy = childTotalEnergy.add(calculateChildTotalEnergy(grandChild));
        }

        child.setEnergy(childTotalEnergy);
        return childTotalEnergy;
    }

    private BigDecimal calculateChildTotalCO2(ConsumptionResponse child) {
        BigDecimal childTotalCO2 = child.getCo2() != null ? child.getCo2() : BigDecimal.ZERO;

        for (ConsumptionResponse grandChild : child.getChildren()) {
            childTotalCO2 = childTotalCO2.add(calculateChildTotalCO2(grandChild));
        }

        child.setCo2(childTotalCO2);
        return childTotalCO2;
    }
}
