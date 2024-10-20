package pe.edu.dibertec.patitas_frontend_wc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//Habilitamos el Feign Client y ya se podra usar
@EnableFeignClients
public class PatitasFrontendWcApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatitasFrontendWcApplication.class, args);
	}

}
