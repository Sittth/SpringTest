package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import spring.ru.springtest.dto.StudentResponse;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CourseResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-12T16:26:58.355165800+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class CourseResponse {

  private UUID id;

  private String title;

  @Valid
  private List<@Valid StudentResponse> students = new ArrayList<>();

  public CourseResponse id(UUID id) {
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

  public CourseResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CourseResponse students(List<@Valid StudentResponse> students) {
    this.students = students;
    return this;
  }

  public CourseResponse addStudentsItem(StudentResponse studentsItem) {
    if (this.students == null) {
      this.students = new ArrayList<>();
    }
    this.students.add(studentsItem);
    return this;
  }

  /**
   * Get students
   * @return students
  */
  @Valid 
  @Schema(name = "students", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("students")
  public List<@Valid StudentResponse> getStudents() {
    return students;
  }

  public void setStudents(List<@Valid StudentResponse> students) {
    this.students = students;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CourseResponse courseResponse = (CourseResponse) o;
    return Objects.equals(this.id, courseResponse.id) &&
        Objects.equals(this.title, courseResponse.title) &&
        Objects.equals(this.students, courseResponse.students);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, students);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CourseResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    students: ").append(toIndentedString(students)).append("\n");
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

