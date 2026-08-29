package spring.ru.springtest.dto.create;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spring.ru.springtest.dto.create.BookCreateRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AuthorCreateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:33:24.057069+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class AuthorCreateRequest {

  private String name;

  @Valid
  private List<@Valid BookCreateRequest> books = new ArrayList<>();

  public AuthorCreateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AuthorCreateRequest(String name, List<@Valid BookCreateRequest> books) {
    this.name = name;
    this.books = books;
  }

  public AuthorCreateRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull @Size(min = 2, max = 50) 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AuthorCreateRequest books(List<@Valid BookCreateRequest> books) {
    this.books = books;
    return this;
  }

  public AuthorCreateRequest addBooksItem(BookCreateRequest booksItem) {
    if (this.books == null) {
      this.books = new ArrayList<>();
    }
    this.books.add(booksItem);
    return this;
  }

  /**
   * Get books
   * @return books
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "books", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("books")
  public List<@Valid BookCreateRequest> getBooks() {
    return books;
  }

  public void setBooks(List<@Valid BookCreateRequest> books) {
    this.books = books;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthorCreateRequest authorCreateRequest = (AuthorCreateRequest) o;
    return Objects.equals(this.name, authorCreateRequest.name) &&
        Objects.equals(this.books, authorCreateRequest.books);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, books);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthorCreateRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    books: ").append(toIndentedString(books)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

