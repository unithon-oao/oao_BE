package oao_BE.oao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class OaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(OaoApplication.class, args);
	}

}
