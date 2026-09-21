package spring.ru.springtest.client;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import spring.ru.springtest.models.enums.FailureType;

import java.io.IOException;
import java.util.Set;

public class FailureClassifier {

    private static final Set<Integer> TRANSIENT_STATUSES = Set.of(408, 425, 429, 500, 502, 503, 504);

    FailureClassifier(){
    }

    public static FailureType classify(Throwable throwable) {
        Throwable current = throwable;

        while (current != null) {

            if (current instanceof CallNotPermittedException) {
                return FailureType.CIRCUIT_OPEN;
            }

            if (current instanceof ConstraintViolationException) {
                return FailureType.PERMANENT;
            }

            if (current instanceof FeignException feignException) {
                return classifyStatus(feignException.status());
            }

            if (current instanceof IOException) {
                return FailureType.TRANSIENT;
            }

            current = current.getCause();
        }
        return FailureType.UNKNOWN;
    }

    private static FailureType classifyStatus(int status) {
        if (status == -1 || TRANSIENT_STATUSES.contains(status)) {
            return FailureType.TRANSIENT;
        }
        if (status >= 400 && status < 500) {
            return FailureType.PERMANENT;
        }
        return FailureType.UNKNOWN;
    }
}
