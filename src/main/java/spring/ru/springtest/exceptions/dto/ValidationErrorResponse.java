package spring.ru.springtest.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private Map<String, List<String>> errors;

    private String requestId;
}
