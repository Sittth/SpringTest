package spring.ru.springtest.exceptions;

public class AuthorNotFoundException extends AppException {
    public AuthorNotFoundException(String message) {
        super(message);
    }

    public AuthorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
