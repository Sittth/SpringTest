package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import spring.ru.springtest.dto.Book;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Author
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-15T21:13:03.022664+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class Author {

  private UUID id;

  private String name;

  @Valid
  private List<@Valid Book> books = new ArrayList<>();

  public Author id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Author name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Author books(List<@Valid Book> books) {
    this.books = books;
    return this;
  }

  public Author addBooksItem(Book booksItem) {
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
  @Valid 
  @Schema(name = "books", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("books")
  public List<@Valid Book> getBooks() {
    return books;
  }

  public void setBooks(List<@Valid Book> books) {
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
    Author author = (Author) o;
    return Objects.equals(this.id, author.id) &&
        Objects.equals(this.name, author.name) &&
        Objects.equals(this.books, author.books);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, books);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Author {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

