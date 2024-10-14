package pe.edu.dibertec.patitas_frontend_wc.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    //Creamos el Bean para RestTemplate
    @Bean
    public RestTemplate restTemplateAutenticacion(RestTemplateBuilder builder) {
        //Usar el Builder tiene muchos beneficios como declarar un tiempo de respuestas maximo (Para probar este ejemplo dormimos el back para q funcione)
        return builder
                .rootUri("http://localhost:8081/autenticacion")
                .setReadTimeout(Duration.ofSeconds(30)) //Tiempo de espera maximo para recibir la respuesta
                .build();
    }
}
