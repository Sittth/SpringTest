package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spring.ru.springtest.dto.BookRequestUpdate;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AuthorRequestUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-14T16:20:24.178330700+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class AuthorRequestUpdate {

  private String name;

  @Valid
  private List<@Valid BookRequestUpdate> books = new ArrayList<>();

  public AuthorRequestUpdate name(String name) {
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

  public AuthorRequestUpdate books(List<@Valid BookRequestUpdate> books) {
    this.books = books;
    return this;
  }

  public AuthorRequestUpdate addBooksItem(BookRequestUpdate booksItem) {
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
  public List<@Valid BookRequestUpdate> getBooks() {
    return books;
  }

  public void setBooks(List<@Valid BookRequestUpdate> books) {
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
    AuthorRequestUpdate authorRequestUpdate = (AuthorRequestUpdate) o;
    return Objects.equals(this.name, authorRequestUpdate.name) &&
        Objects.equals(this.books, authorRequestUpdate.books);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, books);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthorRequestUpdate {\n");
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

