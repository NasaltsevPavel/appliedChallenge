package nasaltsev.appliedChallenge.service;

import nasaltsev.appliedChallenge.model.EnergyScope;
import nasaltsev.appliedChallenge.model.EnergySource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ExternalAPIService {

    private final WebClient.Builder webClientBuilder;

    public ExternalAPIService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public List<EnergySource> getEnergySources() {
        WebClient webClient = webClientBuilder.build();
        String energySourcesUrl = "https://applied-coding-challenge.s3.eu-central-1.amazonaws.com/backend/energy-sources.json";
        return webClient.get()
                .uri(energySourcesUrl)
                .retrieve()
                .bodyToFlux(EnergySource.class)
                .collectList()
                .block();
    }

    public List<EnergyScope> getScopes() {
        WebClient webClient = webClientBuilder.build();
        String scopeUrl = "https://applied-coding-challenge.s3.eu-central-1.amazonaws.com/backend/scopes.json";
        return webClient.get()
                .uri(scopeUrl)
                .retrieve()
                .bodyToFlux(EnergyScope.class)
                .collectList()
                .block();
    }



}
