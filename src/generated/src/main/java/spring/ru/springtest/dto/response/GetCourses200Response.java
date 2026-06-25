package spring.ru.springtest.dto.response;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spring.ru.springtest.dto.response.CourseResponse;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetCourses200Response
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-25T18:57:05.418890800+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class GetCourses200Response {

  @Valid
  private List<@Valid CourseResponse> content = new ArrayList<>();

  private Integer page;

  private Integer size;

  private Long totalElements;

  public GetCourses200Response content(List<@Valid CourseResponse> content) {
    this.content = content;
    return this;
  }

  public GetCourses200Response addContentItem(CourseResponse contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }
    this.content.add(contentItem);
    return this;
  }

  /**
   * Get content
   * @return content
  */
  @Valid 
  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public List<@Valid CourseResponse> getContent() {
    return content;
  }

  public void setContent(List<@Valid CourseResponse> content) {
    this.content = content;
  }

  public GetCourses200Response page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
  */
  
  @Schema(name = "page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public GetCourses200Response size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * @return size
  */
  
  @Schema(name = "size", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public GetCourses200Response totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Get totalElements
   * @return totalElements
  */
  
  @Schema(name = "totalElements", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalElements")
  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetCourses200Response getCourses200Response = (GetCourses200Response) o;
    return Objects.equals(this.content, getCourses200Response.content) &&
        Objects.equals(this.page, getCourses200Response.page) &&
        Objects.equals(this.size, getCourses200Response.size) &&
        Objects.equals(this.totalElements, getCourses200Response.totalElements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, page, size, totalElements);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetCourses200Response {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
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

