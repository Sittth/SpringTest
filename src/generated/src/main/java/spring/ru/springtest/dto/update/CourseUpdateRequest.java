package spring.ru.springtest.dto.update;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CourseUpdateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-16T19:56:50.520546900+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class CourseUpdateRequest {

  private String title;

  @Valid
  private List<UUID> studentIds = new ArrayList<>();

  public CourseUpdateRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  @Size(min = 3, max = 100) 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CourseUpdateRequest studentIds(List<UUID> studentIds) {
    this.studentIds = studentIds;
    return this;
  }

  public CourseUpdateRequest addStudentIdsItem(UUID studentIdsItem) {
    if (this.studentIds == null) {
      this.studentIds = new ArrayList<>();
    }
    this.studentIds.add(studentIdsItem);
    return this;
  }

  /**
   * Get studentIds
   * @return studentIds
  */
  @Valid @Size(max = 200) 
  @Schema(name = "studentIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("studentIds")
  public List<UUID> getStudentIds() {
    return studentIds;
  }

  public void setStudentIds(List<UUID> studentIds) {
    this.studentIds = studentIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CourseUpdateRequest courseUpdateRequest = (CourseUpdateRequest) o;
    return Objects.equals(this.title, courseUpdateRequest.title) &&
        Objects.equals(this.studentIds, courseUpdateRequest.studentIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, studentIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CourseUpdateRequest {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    studentIds: ").append(toIndentedString(studentIds)).append("\n");
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

