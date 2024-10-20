package pe.edu.dibertec.patitas_frontend_wc.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options requestOptions() {
        //Declaramos un timeout para conectarnos y otro para lectura
        return new Request.Options(5000, 10000);
        //1° Es el tiempo máximo que el cliente espera para establecer la conexión.
        //2° Es el tiempo máximo que el cliente espera para recibir una respuesta después de establecer la conexión.
    }

    //Read timed out executing POST http://localhost:8081/autenticacion/login
    /*
     Indica que el cliente (Feign Client) intentó leer la respuesta
     del servidor, pero no recibió una respuesta dentro del tiempo
     de espera configurado
    */
}
