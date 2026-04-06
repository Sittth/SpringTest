package spring.ru.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import spring.ru.springtest.dto.ProfileRequestCreate;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserRequestCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-05T18:26:19.788055+03:00[Europe/Moscow]", comments = "Generator version: 7.6.0")
public class UserRequestCreate {

  private String username;

  private ProfileRequestCreate profile;

  public UserRequestCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserRequestCreate(String username) {
    this.username = username;
  }

  public UserRequestCreate username(String username) {
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

  public UserRequestCreate profile(ProfileRequestCreate profile) {
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
  public ProfileRequestCreate getProfile() {
    return profile;
  }

  public void setProfile(ProfileRequestCreate profile) {
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
    UserRequestCreate userRequestCreate = (UserRequestCreate) o;
    return Objects.equals(this.username, userRequestCreate.username) &&
        Objects.equals(this.profile, userRequestCreate.profile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, profile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserRequestCreate {\n");
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

