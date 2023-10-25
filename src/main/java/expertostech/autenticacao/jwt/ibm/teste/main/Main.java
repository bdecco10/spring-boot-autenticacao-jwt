package expertostech.autenticacao.jwt.ibm.teste.main;

import expertostech.autenticacao.jwt.ibm.teste.service.RegisterMedicalService;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        RegisterMedicalService.healthCheckup(134, 148);
    }

}
