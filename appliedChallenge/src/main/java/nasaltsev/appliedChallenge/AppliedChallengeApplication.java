package nasaltsev.appliedChallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppliedChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppliedChallengeApplication.class, args);
	}

}
