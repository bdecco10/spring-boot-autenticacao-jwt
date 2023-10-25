package expertostech.autenticacao.jwt.ibm.teste.service;

import expertostech.autenticacao.jwt.ibm.teste.client.RegisterMedicalClient;
import expertostech.autenticacao.jwt.ibm.teste.model.MedicalRecord;
import expertostech.autenticacao.jwt.ibm.teste.model.MedicalRecordResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterMedicalService {

    /**
     * Usando o for desta forma, vai executar a qtd certa de requisicao para o
     * servico externo, com isso evitamos menos problema de rede(Intermitencia/Etc..)
     *
     * @param lowerlimit
     * @param upperlimit
     * @return
     */
    public static int healthCheckup(int lowerlimit, int upperlimit) {
        AtomicInteger totalCount = new AtomicInteger();
        try {
            AtomicInteger totalPages = new AtomicInteger(-1); // Inicializado com valor negativo para entrada no loop
            String apiUrl = "https://jsonmock.hackerrank.com/api/medical_records?page=";
            for (int currentPage = 1; currentPage <= totalPages.get() || totalPages.get() == -1; currentPage++) {
                MedicalRecordResponse response = RegisterMedicalClient.sendGetRequest(apiUrl + currentPage);
                Optional.ofNullable(response)
                        .ifPresent(resp -> {
                            if (totalPages.get() == -1) {
                                totalPages.set(resp.total_pages);
                            }
                            totalCount.addAndGet(getCountInRange(resp.data, lowerlimit, upperlimit));
                        });
                if (response == null) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Total Count: " + totalCount);
        return totalCount.get();
    }

    public static int getCountInRange(List<MedicalRecord> records, int lowerlimit, int upperlimit) {
        return (int) records.stream()
                .filter(medicalRecord -> medicalRecord.vitals != null)
                .mapToInt(medicalRecord -> medicalRecord.vitals.bloodPressureDiastole)
                .filter(diastole -> diastole >= lowerlimit && diastole <= upperlimit)
                .count();
    }
}
