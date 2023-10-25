package expertostech.autenticacao.jwt.kafka.util;

import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;

import java.util.function.BooleanSupplier;

public class KafkaDTOValidator {
    public static void validate(KafkaDTO kafkaDTO) {
        // Validação da estrutura da mensagem
        validateCondition(() -> kafkaDTO != null, "Mensagem inválida: a mensagem não pode ser nula.");
        validateCondition(() -> kafkaDTO.getCampo1() != null, "Mensagem inválida: o campo campo1 não pode ser nulo.");
        validateCondition(() -> kafkaDTO.getCampo2() != 0, "Mensagem inválida: o campo campo2 não pode ser zero.");

        // Validação dos tipos dos campos
        validateCondition(() -> kafkaDTO.getCampo1().getClass().equals(String.class), "Mensagem inválida: o campo campo1 deve ser uma String.");
        validateCondition(() -> kafkaDTO.getCampo2().getClass().equals(Integer.class), "Mensagem inválida: o campo campo2 deve ser um Integer.");
    }

    private static void validateCondition(BooleanSupplier condition, String errorMessage) {
        if (!condition.getAsBoolean()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
