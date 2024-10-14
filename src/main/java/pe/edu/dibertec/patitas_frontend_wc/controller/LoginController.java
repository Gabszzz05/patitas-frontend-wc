package pe.edu.dibertec.patitas_frontend_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.dibertec.patitas_frontend_wc.dto.LoginResponseDTO;
import pe.edu.dibertec.patitas_frontend_wc.viewModel.LoginModel;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/login")
public class LoginController {

    //NUEVO....
    @Autowired
    WebClient webClientAutenticacion;

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
}
