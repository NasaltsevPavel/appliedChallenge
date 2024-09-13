package nasaltsev.appliedChallenge.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConsumptionRequest {

    @NotBlank
    private String description;

    @NotBlank
    private String energySourceId;

    @NotNull
    @Digits(integer = 5 , fraction = 5)
    private BigDecimal consumption;

    @Digits(integer = 1 , fraction = 5)
    private BigDecimal emissionFactor;

    public ConsumptionRequest(String description, String energySourceId, BigDecimal consumption, BigDecimal emissionFactor) {
        this.description = description;
        this.energySourceId = energySourceId;
        this.consumption = consumption;
        this.emissionFactor = emissionFactor;
    }
}
