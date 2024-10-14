package pe.edu.dibertec.patitas_frontend_wc.dto;

//@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponseDTO(String code, String msj, String user, String userGmail, String tipoDocumento, String numeroDocumento) {
}
