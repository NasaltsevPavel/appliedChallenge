package nasaltsev.appliedChallenge.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConsumptionResponse {

    private String name;
    private String label;
    private BigDecimal energy;
    private BigDecimal co2;
    private List<ConsumptionResponse> children;

    public ConsumptionResponse(String name, String label, BigDecimal energy, BigDecimal co2) {
        this.name = name;
        this.label = label;
        this.energy = energy;
        this.co2 = co2;
        this.children = new ArrayList<>();
    }
}
