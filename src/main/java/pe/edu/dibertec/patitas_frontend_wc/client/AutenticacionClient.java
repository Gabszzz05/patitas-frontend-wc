package pe.edu.dibertec.patitas_frontend_wc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.dibertec.patitas_frontend_wc.config.FeignConfig;
import pe.edu.dibertec.patitas_frontend_wc.dto.LogOutResponseDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginResponseDTO;

//Se declara la interfaz
//name = para que toda la aplicacion lo reconozca
//url = url del servicio a usar

//Asociamos la clase config al Feign
@FeignClient(name = "autenticacion", url = "http://localhost:8081/autenticacion", configuration = FeignConfig.class) //Permitira pasar los servicios que queramos consumir
public interface AutenticacionClient {

    //Firma para el Login
    @PostMapping("/login")//Tipo de envio
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO);

    //Firma para el LogOut
    @PostMapping("/logout")
    ResponseEntity<LogOutResponseDTO> logout();
}

/*
Ejemplo de Configuración mediante Properties
Para application.properties:
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=10000

Para application.yml:
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
*/
