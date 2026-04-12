package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spring.ru.springtest.dto.BookRequestCreate;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AuthorRequestCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-12T16:26:58.355165800+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class AuthorRequestCreate {

  private String name;

  @Valid
  private List<@Valid BookRequestCreate> books = new ArrayList<>();

  public AuthorRequestCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AuthorRequestCreate(String name) {
    this.name = name;
  }

  public AuthorRequestCreate name(String name) {
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

  public AuthorRequestCreate books(List<@Valid BookRequestCreate> books) {
    this.books = books;
    return this;
  }

  public AuthorRequestCreate addBooksItem(BookRequestCreate booksItem) {
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
  public List<@Valid BookRequestCreate> getBooks() {
    return books;
  }

  public void setBooks(List<@Valid BookRequestCreate> books) {
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
    AuthorRequestCreate authorRequestCreate = (AuthorRequestCreate) o;
    return Objects.equals(this.name, authorRequestCreate.name) &&
        Objects.equals(this.books, authorRequestCreate.books);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, books);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthorRequestCreate {\n");
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

