package nasaltsev.appliedChallenge.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EnergyScope {

    private String id;
    private String name;
    private String label;
    private BigDecimal energy;
    private BigDecimal co2;
    private List<EnergyScope> subScopes;


}
