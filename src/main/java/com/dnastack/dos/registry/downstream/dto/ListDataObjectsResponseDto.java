package com.dnastack.dos.registry.downstream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A list of Data Objects matching the requested parameters, and a paging token, that can be used to retrieve more results.
 */
@ApiModel(description = "A list of Data Objects matching the requested parameters, and a paging token, that can be used to retrieve more results.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T23:03:59.998-04:00")

public class ListDataObjectsResponseDto {
  @JsonProperty("data_objects")
  private List<DataObjectDto> dataObjects = null;

  @JsonProperty("next_page_token")
  private String nextPageToken = null;

  public ListDataObjectsResponseDto dataObjects(List<DataObjectDto> dataObjects) {
    this.dataObjects = dataObjects;
    return this;
  }

  public ListDataObjectsResponseDto addDataObjectsItem(DataObjectDto dataObjectsItem) {
    if (this.dataObjects == null) {
      this.dataObjects = new ArrayList<DataObjectDto>();
    }
    this.dataObjects.add(dataObjectsItem);
    return this;
  }

   /**
   * The list of Data Objects.
   * @return dataObjects
  **/
  @ApiModelProperty(value = "The list of Data Objects.")

  @Valid

  public List<DataObjectDto> getDataObjects() {
    return dataObjects;
  }

  public void setDataObjects(List<DataObjectDto> dataObjects) {
    this.dataObjects = dataObjects;
  }

  public ListDataObjectsResponseDto nextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
    return this;
  }

   /**
   * The continuation token, which is used to page through large result sets. Provide this value in a subsequent request to return the next page of results. This field will be empty if there aren't any additional results.
   * @return nextPageToken
  **/
  @ApiModelProperty(value = "The continuation token, which is used to page through large result sets. Provide this value in a subsequent request to return the next page of results. This field will be empty if there aren't any additional results.")


  public String getNextPageToken() {
    return nextPageToken;
  }

  public void setNextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListDataObjectsResponseDto dataObjectsResponse = (ListDataObjectsResponseDto) o;
    return Objects.equals(this.dataObjects, dataObjectsResponse.dataObjects) &&
        Objects.equals(this.nextPageToken, dataObjectsResponse.nextPageToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataObjects, nextPageToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataObjectsResponseDto {\n");

    sb.append("    dataObjects: ").append(toIndentedString(dataObjects)).append("\n");
    sb.append("    nextPageToken: ").append(toIndentedString(nextPageToken)).append("\n");
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

