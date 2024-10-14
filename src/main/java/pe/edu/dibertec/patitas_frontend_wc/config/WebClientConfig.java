package pe.edu.dibertec.patitas_frontend_wc.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
public class WebClientConfig {

    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) //TimeOut de conexion
            .responseTimeout(Duration.ofSeconds(10)) //TimeOut para obtener el total de la rpta
            .doOnConnected(cnx -> cnx.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))); //Tiempo de espera maximo para recepcionar cada paquete

    @Bean
    public WebClient webClientAutenticacion(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8081/autenticacion")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
