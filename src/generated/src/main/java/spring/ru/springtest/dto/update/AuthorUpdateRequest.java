package spring.ru.springtest.dto.update;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spring.ru.springtest.dto.update.BookUpdateRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AuthorUpdateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-30T16:13:17.904382200+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class AuthorUpdateRequest {

  private String name;

  @Valid
  private List<@Valid BookUpdateRequest> books = new ArrayList<>();

  public AuthorUpdateRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @Size(min = 2, max = 50) 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AuthorUpdateRequest books(List<@Valid BookUpdateRequest> books) {
    this.books = books;
    return this;
  }

  public AuthorUpdateRequest addBooksItem(BookUpdateRequest booksItem) {
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
  @Valid @Size(max = 200) 
  @Schema(name = "books", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("books")
  public List<@Valid BookUpdateRequest> getBooks() {
    return books;
  }

  public void setBooks(List<@Valid BookUpdateRequest> books) {
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
    AuthorUpdateRequest authorUpdateRequest = (AuthorUpdateRequest) o;
    return Objects.equals(this.name, authorUpdateRequest.name) &&
        Objects.equals(this.books, authorUpdateRequest.books);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, books);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthorUpdateRequest {\n");
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

