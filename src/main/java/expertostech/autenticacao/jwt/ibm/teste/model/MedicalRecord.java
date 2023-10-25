package expertostech.autenticacao.jwt.ibm.teste.model;

import lombok.Data;

@Data
public class MedicalRecord {
    public int id;
    public long timestamp;
    public Diagnosis diagnosis;
    public Vitals vitals;
    public Doctor doctor;
    public int userId;
    public String userName;
    public String userDob;
    public Meta meta;

}
