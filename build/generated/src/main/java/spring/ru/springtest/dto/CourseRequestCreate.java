package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spring.ru.springtest.dto.StudentRequestCreate;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CourseRequestCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-05T18:26:19.788055+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class CourseRequestCreate {

  private String title;

  @Valid
  private List<@Valid StudentRequestCreate> students = new ArrayList<>();

  public CourseRequestCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CourseRequestCreate(String title) {
    this.title = title;
  }

  public CourseRequestCreate title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  @NotNull @Size(min = 3, max = 100) 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CourseRequestCreate students(List<@Valid StudentRequestCreate> students) {
    this.students = students;
    return this;
  }

  public CourseRequestCreate addStudentsItem(StudentRequestCreate studentsItem) {
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
  @Valid @Size(max = 200) 
  @Schema(name = "students", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("students")
  public List<@Valid StudentRequestCreate> getStudents() {
    return students;
  }

  public void setStudents(List<@Valid StudentRequestCreate> students) {
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
    CourseRequestCreate courseRequestCreate = (CourseRequestCreate) o;
    return Objects.equals(this.title, courseRequestCreate.title) &&
        Objects.equals(this.students, courseRequestCreate.students);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, students);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CourseRequestCreate {\n");
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

