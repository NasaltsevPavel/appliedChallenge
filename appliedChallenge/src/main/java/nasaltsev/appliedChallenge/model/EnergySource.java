package nasaltsev.appliedChallenge.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EnergySource {

    private String energySourceId;
    private String scopeId;
    private String name;
    private BigDecimal conversionFactor;
    private BigDecimal emissionFactor;
}
