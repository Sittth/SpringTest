package spring.ru.springtest.dto.create;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import spring.ru.springtest.dto.create.ProfileCreateRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserCreateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-16T19:56:49.698145800+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class UserCreateRequest {

  private String username;

  private ProfileCreateRequest profile;

  public UserCreateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserCreateRequest(String username) {
    this.username = username;
  }

  public UserCreateRequest username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @NotNull @Pattern(regexp = "^[a-zA-Z0-9_]+$") @Size(min = 2, max = 50) 
  @Schema(name = "username", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserCreateRequest profile(ProfileCreateRequest profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
  */
  @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profile")
  public ProfileCreateRequest getProfile() {
    return profile;
  }

  public void setProfile(ProfileCreateRequest profile) {
    this.profile = profile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCreateRequest userCreateRequest = (UserCreateRequest) o;
    return Objects.equals(this.username, userCreateRequest.username) &&
        Objects.equals(this.profile, userCreateRequest.profile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, profile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCreateRequest {\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
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

