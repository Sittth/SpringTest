package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import spring.ru.springtest.dto.ProfileRequestUpdate;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserRequestUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-13T23:52:54.972128400+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class UserRequestUpdate {

  private String username;

  private ProfileRequestUpdate profile;

  public UserRequestUpdate username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @Pattern(regexp = "^[a-zA-Z0-9_]+$") @Size(min = 2, max = 50) 
  @Schema(name = "username", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserRequestUpdate profile(ProfileRequestUpdate profile) {
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
  public ProfileRequestUpdate getProfile() {
    return profile;
  }

  public void setProfile(ProfileRequestUpdate profile) {
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
    UserRequestUpdate userRequestUpdate = (UserRequestUpdate) o;
    return Objects.equals(this.username, userRequestUpdate.username) &&
        Objects.equals(this.profile, userRequestUpdate.profile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, profile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserRequestUpdate {\n");
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

