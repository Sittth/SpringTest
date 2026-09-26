package spring.ru.springtest.exceptions;

import java.util.UUID;

public class BookMetadataRegistrationException extends AppException {
  public BookMetadataRegistrationException(UUID bookId, Throwable cause) {
    super("Failed to register metadata for book " + bookId, cause);
  }
}
