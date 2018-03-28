package com.dnastack.dos.registry.downstream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Allows a requester to list and filter Data Objects. Only Data Objects matching all of the requested parameters will be returned.
 */
@ApiModel(description = "Allows a requester to list and filter Data Objects. Only Data Objects matching all of the requested parameters will be returned.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T23:33:01.899-04:00")

public class ListDataObjectsRequestDto {
  @JsonProperty("alias")
  private String alias = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("checksum")
  private ChecksumRequestDto checksum = null;

  @JsonProperty("page_size")
  private Integer pageSize = null;

  @JsonProperty("page_token")
  private String pageToken = null;

  public ListDataObjectsRequestDto alias(String alias) {
    this.alias = alias;
    return this;
  }

   /**
   * OPTIONAL If provided will only return Data Objects with the given alias.
   * @return alias
  **/
  @ApiModelProperty(value = "OPTIONAL If provided will only return Data Objects with the given alias.")


  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public ListDataObjectsRequestDto url(String url) {
    this.url = url;
    return this;
  }

   /**
   * OPTIONAL If provided will return only Data Objects with a that URL matches this string.
   * @return url
  **/
  @ApiModelProperty(value = "OPTIONAL If provided will return only Data Objects with a that URL matches this string.")


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ListDataObjectsRequestDto checksum(ChecksumRequestDto checksum) {
    this.checksum = checksum;
    return this;
  }

   /**
   * OPTIONAL If provided will only return data object messages with the provided checksum. If the checksum type is provided
   * @return checksum
  **/
  @ApiModelProperty(value = "OPTIONAL If provided will only return data object messages with the provided checksum. If the checksum type is provided")

  @Valid

  public ChecksumRequestDto getChecksum() {
    return checksum;
  }

  public void setChecksum(ChecksumRequestDto checksum) {
    this.checksum = checksum;
  }

  public ListDataObjectsRequestDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

   /**
   * OPTIONAL Specifies the maximum number of results to return in a single page. If unspecified, a system default will be used.
   * @return pageSize
  **/
  @ApiModelProperty(value = "OPTIONAL Specifies the maximum number of results to return in a single page. If unspecified, a system default will be used.")


  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public ListDataObjectsRequestDto pageToken(String pageToken) {
    this.pageToken = pageToken;
    return this;
  }

   /**
   * OPTIONAL The continuation token, which is used to page through large result sets. To get the next page of results, set this parameter to the value of `next_page_token` from the previous response.
   * @return pageToken
  **/
  @ApiModelProperty(value = "OPTIONAL The continuation token, which is used to page through large result sets. To get the next page of results, set this parameter to the value of `next_page_token` from the previous response.")


  public String getPageToken() {
    return pageToken;
  }

  public void setPageToken(String pageToken) {
    this.pageToken = pageToken;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListDataObjectsRequestDto listDataObjectsRequest = (ListDataObjectsRequestDto) o;
    return Objects.equals(this.alias, listDataObjectsRequest.alias) &&
        Objects.equals(this.url, listDataObjectsRequest.url) &&
        Objects.equals(this.checksum, listDataObjectsRequest.checksum) &&
        Objects.equals(this.pageSize, listDataObjectsRequest.pageSize) &&
        Objects.equals(this.pageToken, listDataObjectsRequest.pageToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, url, checksum, pageSize, pageToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListDataObjectsRequestDto {\n");

    sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    checksum: ").append(toIndentedString(checksum)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    pageToken: ").append(toIndentedString(pageToken)).append("\n");
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

