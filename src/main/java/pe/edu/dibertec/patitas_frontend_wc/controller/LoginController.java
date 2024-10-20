package pe.edu.dibertec.patitas_frontend_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.dibertec.patitas_frontend_wc.client.AutenticacionClient;
import pe.edu.dibertec.patitas_frontend_wc.dto.LogOutResponseDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginResponseDTO;
import pe.edu.dibertec.patitas_frontend_wc.viewModel.LoginModel;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    //NUEVO....
    @Autowired
    WebClient webClientAutenticacion;

    //Feign Client
    @Autowired
    AutenticacionClient autenticacionClient;

    //Metodo para mostrar la pantalla de inicio
    @GetMapping("/inicio")
    public String incioPage(Model model){
        //Instanciamos el viewModel
        LoginModel loginModel = new LoginModel("00", "", "");
        //                     VARIABLE               VALOR
        model.addAttribute("loginModel", loginModel);
        //Retornamos la vista
        return "inicioPage";
    }

    //Metodo para autenticar
    @PostMapping("/autenticar")
    public String autenticar(@RequestParam("tipoDocumento") String tipoDocumento,
                             @RequestParam("numeroDocumento") String numeroDocumento,
                             @RequestParam("password") String password,
                             Model model){
        //Validar campos de entrada
        if(tipoDocumento == null || tipoDocumento.isEmpty() || numeroDocumento == null || numeroDocumento.isEmpty() || password == null || password.isEmpty()){
            //Enviamos un mensaje de Error
            LoginModel loginModel = new LoginModel("01", "Error: Debe completar correctamente sus credenciales", "");
            model.addAttribute("loginModel", loginModel);
            //Retornamos a inicio
            return "inicioPage";
        }

        //Invocacion API Primer Forma:
        /*LoginRequestDTO loginRequestDTO = new LoginRequestDTO(tipoDocumento, numeroDocumento, password);
        //Realizar la solicitud al back
        try {
            ResponseEntity<LoginResponseDTO> response = restTemplate.exchange(backendURL, HttpMethod.POST, new HttpEntity<>(loginRequestDTO), LoginResponseDTO.class);
            LoginResponseDTO loginResponseDTO = response.getBody();

            //Verificar la respuesta
            if(loginResponseDTO != null && "00".equals(loginResponseDTO.code())){
                LoginModel loginModel = new LoginModel("00", "", loginResponseDTO.user());
                model.addAttribute("loginModel", loginModel);
                return "principal";
            }else{
                LoginModel loginModel = new LoginModel("01", loginResponseDTO != null ?loginResponseDTO.msj() : "Error", "");
                model.addAttribute("loginModel", loginModel);
                return "inicioPage";
            }

        }catch (Exception e){
            LoginModel loginModel = new LoginModel("99", "ERROR: No se pudo conectar con el servidor", "");
            model.addAttribute("loginModel", loginModel);
            return "inicioPage";
        }*/

        //Invocacion API Segunda Forma:
        try {
            //Invocacion de un servicio que recepciona un objeto de tipo Mono
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(tipoDocumento, numeroDocumento, password);
            Mono<LoginResponseDTO> monoLoginResponseDTO = webClientAutenticacion.post()
                    .uri("http://localhost:8081/autenticacion/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class);
            //Recuperamos el resultado de la peticion
            LoginResponseDTO loginResponseDTO = monoLoginResponseDTO.block();


            if (loginResponseDTO.code().equals("00")) {
                //Instanciamos el viewModel
                LoginModel loginModel = new LoginModel("00", "", loginResponseDTO.user());
                //                     VARIABLE               VALOR
                model.addAttribute("loginModel", loginModel);
                //
                return "principal";
            } else {
                LoginModel loginModel = new LoginModel("02", "ERROR: Autenticacion Fallida", "");
                model.addAttribute("loginModel", loginModel);
                return "inicioPage";
            }
        }catch (Exception e){
            LoginModel loginModel = new LoginModel("99", "ERROR: No se pudo conectar con el servidor", "");
            model.addAttribute("loginModel", loginModel);
            System.out.println(e.getMessage());
            return "inicioPage";
        }
    }

    //Login
    //Utilizamos FeignClient
    @PostMapping("/autenticar-feign")
    public ResponseEntity<LoginModel> autenticarFeign(@RequestBody LoginRequestDTO loginRequestDTO) {

        System.out.println("Consumiendo con Feign Client el LOGIN");

        // Validar campos de entrada
        if (loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().isEmpty() ||
                loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().isEmpty() ||
                loginRequestDTO.password() == null || loginRequestDTO.password().isEmpty()) {

            // Enviamos un mensaje de Error
            LoginModel loginModel = new LoginModel("01", "Error: Debe completar correctamente sus credenciales", "");
            return ResponseEntity.badRequest().body(loginModel);
        }

        try {
            // Consumir Servicio con Feign Client
            ResponseEntity<LoginResponseDTO> responseEntity = autenticacionClient.login(loginRequestDTO);

            // Validar resultado del servicio
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LoginResponseDTO loginResponseDTO = responseEntity.getBody();

                if ("00".equals(loginResponseDTO.code())) {
                    LoginModel loginModel = new LoginModel("00", "", loginResponseDTO.user());
                    return ResponseEntity.ok(loginModel);
                } else {
                    LoginModel loginModel = new LoginModel("02", "ERROR: Autenticacion Fallida", "");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginModel);
                }
            } else {
                LoginModel loginModel = new LoginModel("02", "ERROR: Autenticacion Fallida", "");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginModel);
            }
        } catch (Exception e) {
            LoginModel loginModel = new LoginModel("99", "ERROR: No se pudo conectar con el servidor", "");
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginModel);
        }
    }

    //LogOut
    @PostMapping("/logout-feign")
    public ResponseEntity<LogOutResponseDTO> logoutFeign() {

        System.out.println("Consumiendo con Feign Client el LOGOUT");

        try {
            // Consumir el servicio de logout con Feign Client
            ResponseEntity<LogOutResponseDTO> responseEntity = autenticacionClient.logout();

            // Validar el resultado del servicio
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LogOutResponseDTO logOutResponseDTO = responseEntity.getBody();

                if ("00".equals(logOutResponseDTO.code())) {
                    return ResponseEntity.ok(new LogOutResponseDTO("00", "Cierre de Sesion correcto"));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new LogOutResponseDTO("02", "ERROR: Cierre de Sesion Fallida"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LogOutResponseDTO("02", "ERROR: Cierre de Sesion Fallida"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogOutResponseDTO("99", "ERROR: Ocurrió un problema en el servidor"));
        }
    }



}
