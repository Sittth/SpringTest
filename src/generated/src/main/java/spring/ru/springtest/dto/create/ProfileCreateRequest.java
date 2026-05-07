package spring.ru.springtest.dto.create;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProfileCreateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-07T16:15:05.471821500+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class ProfileCreateRequest {

  private String bio;

  public ProfileCreateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProfileCreateRequest(String bio) {
    this.bio = bio;
  }

  public ProfileCreateRequest bio(String bio) {
    this.bio = bio;
    return this;
  }

  /**
   * Get bio
   * @return bio
  */
  @NotNull @Size(min = 1, max = 500) 
  @Schema(name = "bio", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bio")
  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProfileCreateRequest profileCreateRequest = (ProfileCreateRequest) o;
    return Objects.equals(this.bio, profileCreateRequest.bio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bio);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileCreateRequest {\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
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

