package expertostech.autenticacao.jwt.logger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import expertostech.autenticacao.jwt.logger.dto.LogEntry;
import expertostech.autenticacao.jwt.logger.dto.ErrorException;
import expertostech.autenticacao.jwt.logger.dto.Request;
import expertostech.autenticacao.jwt.logger.dto.Response;
import lombok.AllArgsConstructor;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final ObjectMapper objectMapper;

    public void logError(Object data ,Exception ex) {
        String message = convertObjectToLogMessage(data);
        LogEntry logEntry = new LogEntry().builder().
                timestamp(LocalDateTime.now()).
                request(Request.builder().build()).
                response(Response.builder().payload(message).build()).
                error(ErrorException.builder().
                        details(ex.getMessage()).
                        type(ex.getClass().toString()).
                        build()).build();
        Marker marker = Markers.appendFields(logEntry);
        logger.error(marker, "Log Entry: {}", logEntry);
    }

    public void logInfo(Object data) {
        String message = convertObjectToLogMessage(data);
        LogEntry logEntry = new LogEntry().builder().
                timestamp(LocalDateTime.now()).
                request(Request.builder().
                        headers(new HashMap<>()).
                        payload(message).
                        build()).
                response(Response.builder().build()).
                error(null).build();
        Marker marker = Markers.appendFields(logEntry);
        logger.info(marker, "Log Entry: {}", logEntry);
    }

    public void logDebug(LogEntry logEntry) {
        Marker marker = Markers.appendFields(logEntry);
        logger.debug(marker, "Log Entry: {}", logEntry);
    }

    public String convertObjectToLogMessage(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            // Em caso de erro na serialização, registre uma mensagem de erro ou trate de outra forma apropriada.
            e.printStackTrace();
            return "Erro na conversão para log: " + e.getMessage();
        }

    }
}