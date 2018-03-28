package com.dnastack.dos.registry.downstream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * ChecksumRequestDto
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T23:33:01.899-04:00")

public class ChecksumRequestDto {
  @JsonProperty("checksum")
  private String checksum = null;

  @JsonProperty("type")
  private String type = null;

  public ChecksumRequestDto checksum(String checksum) {
    this.checksum = checksum;
    return this;
  }

   /**
   * REQUIRED The hexlified checksum that one would like to match on.
   * @return checksum
  **/
  @ApiModelProperty(value = "REQUIRED The hexlified checksum that one would like to match on.")


  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public ChecksumRequestDto type(String type) {
    this.type = type;
    return this;
  }

   /**
   * OPTIONAL If provided will restrict responses to those that match the provided type.  possible values: md5                # most blob stores provide a checksum using this multipart-md5      # multipart uploads provide a specialized tag in S3 sha256 sha512
   * @return type
  **/
  @ApiModelProperty(value = "OPTIONAL If provided will restrict responses to those that match the provided type.  possible values: md5                # most blob stores provide a checksum using this multipart-md5      # multipart uploads provide a specialized tag in S3 sha256 sha512")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChecksumRequestDto checksumRequest = (ChecksumRequestDto) o;
    return Objects.equals(this.checksum, checksumRequest.checksum) &&
        Objects.equals(this.type, checksumRequest.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checksum, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChecksumRequestDto {\n");

    sb.append("    checksum: ").append(toIndentedString(checksum)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

