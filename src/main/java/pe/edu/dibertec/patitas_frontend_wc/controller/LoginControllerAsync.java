package pe.edu.dibertec.patitas_frontend_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.dibertec.patitas_frontend_wc.client.AutenticacionClient;
import pe.edu.dibertec.patitas_frontend_wc.dto.LogOutResponseDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginResponseDTO;
import pe.edu.dibertec.patitas_frontend_wc.viewModel.LoginModel;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
//@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerAsync {

    //NUEVO....
    @Autowired
    WebClient webClientAutenticacion;

    //Metodo para autenticar
    @PostMapping("/autenticar-async")
    public Mono<LoginResponseDTO> autenticar(@RequestBody LoginRequestDTO loginRequestDTO) {
        //Validar campos de entrada
        if(loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().trim().length() == 0 ||
                loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().trim().length() == 0 ||
                loginRequestDTO.password() == null || loginRequestDTO.password().trim().length() == 0) {

            return Mono.just(new LoginResponseDTO("01", "ERROR: Debe completar correctamente sus credenciales", "", "", "", ""));
        }

        try {
            //Consumir servicio de atenticacion del backend
            return webClientAutenticacion.post()
                    .uri("/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {
                        if(response.code().equals("00")){
                            return Mono.just(new LoginResponseDTO("00", "", response.user(), "", response.tipoDocumento(), response.numeroDocumento()));
                        }else{
                            return Mono.just(new LoginResponseDTO("02", "ERROR: Autenticacion Fallida", "", "", "", ""));
                        }
                    });
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("99", "ERROR: Ocurrio un problema en el servidor", "", "", "", ""));
        }
    }

    //Cerrar Sesion
    @PostMapping("/logout-async")
    public Mono<LogOutResponseDTO> logout() {

        try{
            //Consumir el servicio de cierre de sesion del backed
            return webClientAutenticacion.post()
                    .uri("/logout")
                    .retrieve()
                    .bodyToMono(LogOutResponseDTO.class)
                    .flatMap(response -> {
                        if(response.code().equals("00")){
                            return Mono.just(new LogOutResponseDTO("00", "Cierre de Sesion correcto"));
                        }else {
                            return Mono.just(new LogOutResponseDTO("02", "ERROR: Cierre de Sesion Fallida"));
                        }
                    });
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Mono.just(new LogOutResponseDTO("99", "ERROR: Ocurrio un problema en el servidor"));
        }
    }


}
