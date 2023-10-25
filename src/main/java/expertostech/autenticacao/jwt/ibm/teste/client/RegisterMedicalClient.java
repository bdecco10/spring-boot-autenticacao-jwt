package expertostech.autenticacao.jwt.ibm.teste.client;

import expertostech.autenticacao.jwt.ibm.teste.model.MedicalRecordResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class RegisterMedicalClient {

    public static MedicalRecordResponse sendGetRequest(String apiUrl) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, MedicalRecordResponse.class);
    }
}
