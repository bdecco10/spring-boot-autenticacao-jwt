package expertostech.autenticacao.jwt.ibm.teste.model;

import lombok.Data;

import java.util.List;

@Data
public class MedicalRecordResponse {
    public int total;
    public int total_pages;
    public List<MedicalRecord> data;

}
