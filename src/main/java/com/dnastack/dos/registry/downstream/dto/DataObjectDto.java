package com.dnastack.dos.registry.downstream.dto;

import com.dnastack.dos.registry.controller.ChecksumDto;
import com.dnastack.dos.registry.controller.URLDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ga4ghDataObjectDto
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T23:07:32.368-04:00")

public class DataObjectDto {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("size")
  private String size = null;

  @JsonProperty("created")
  private DateTime created = null;

  @JsonProperty("updated")
  private DateTime updated = null;

  @JsonProperty("version")
  private String version = null;

  @JsonProperty("mime_type")
  private String mimeType = null;

  @JsonProperty("checksums")
  private List<ChecksumDto> checksums = new ArrayList<ChecksumDto>();

  @JsonProperty("urls")
  private List<URLDto> urls = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("aliases")
  private List<String> aliases = null;

  public DataObjectDto id(String id) {
    this.id = id;
    return this;
  }

   /**
   * REQUIRED An identifier unique to this Data Object.
   * @return id
  **/
  @ApiModelProperty(required = true, value = "REQUIRED An identifier unique to this Data Object.")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DataObjectDto name(String name) {
    this.name = name;
    return this;
  }

   /**
   * OPTIONAL A string that can be optionally used to name a Data Object.
   * @return name
  **/
  @ApiModelProperty(value = "OPTIONAL A string that can be optionally used to name a Data Object.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataObjectDto size(String size) {
    this.size = size;
    return this;
  }

   /**
   * REQUIRED The computed size in bytes.
   * @return size
  **/
  @ApiModelProperty(required = true, value = "REQUIRED The computed size in bytes.")
  @NotNull


  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public DataObjectDto created(DateTime created) {
    this.created = created;
    return this;
  }

   /**
   * REQUIRED Timestamp of object creation in RFC3339.
   * @return created
  **/
  @ApiModelProperty(required = true, value = "REQUIRED Timestamp of object creation in RFC3339.")
  @NotNull

  @Valid

  public DateTime getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created;
  }

  public DataObjectDto updated(DateTime updated) {
    this.updated = updated;
    return this;
  }

   /**
   * OPTIONAL Timestamp of update in RFC3339, identical to create timestamp in systems that do not support updates.
   * @return updated
  **/
  @ApiModelProperty(value = "OPTIONAL Timestamp of update in RFC3339, identical to create timestamp in systems that do not support updates.")

  @Valid

  public DateTime getUpdated() {
    return updated;
  }

  public void setUpdated(DateTime updated) {
    this.updated = updated;
  }

  public DataObjectDto version(String version) {
    this.version = version;
    return this;
  }

   /**
   * OPTIONAL A string representing a version.
   * @return version
  **/
  @ApiModelProperty(value = "OPTIONAL A string representing a version.")


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public DataObjectDto mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

   /**
   * OPTIONAL A string providing the mime-type of the Data Object. For example, \"application/json\".
   * @return mimeType
  **/
  @ApiModelProperty(value = "OPTIONAL A string providing the mime-type of the Data Object. For example, \"application/json\".")


  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public DataObjectDto checksums(List<ChecksumDto> checksums) {
    this.checksums = checksums;
    return this;
  }

  public DataObjectDto addChecksumsItem(ChecksumDto checksumsItem) {
    this.checksums.add(checksumsItem);
    return this;
  }

   /**
   * REQUIRED The checksum of the Data Object. At least one checksum must be provided.
   * @return checksums
  **/
  @ApiModelProperty(required = true, value = "REQUIRED The checksum of the Data Object. At least one checksum must be provided.")
  @NotNull

  @Valid

  public List<ChecksumDto> getChecksums() {
    return checksums;
  }

  public void setChecksums(List<ChecksumDto> checksums) {
    this.checksums = checksums;
  }

  public DataObjectDto urls(List<URLDto> urls) {
    this.urls = urls;
    return this;
  }

  public DataObjectDto addUrlsItem(URLDto urlsItem) {
    if (this.urls == null) {
      this.urls = new ArrayList<URLDto>();
    }
    this.urls.add(urlsItem);
    return this;
  }

   /**
   * OPTIONAL The list of URLs that can be used to access the Data Object.
   * @return urls
  **/
  @ApiModelProperty(value = "OPTIONAL The list of URLs that can be used to access the Data Object.")

  @Valid

  public List<URLDto> getUrls() {
    return urls;
  }

  public void setUrls(List<URLDto> urls) {
    this.urls = urls;
  }

  public DataObjectDto description(String description) {
    this.description = description;
    return this;
  }

   /**
   * OPTIONAL A human readable description of the contents of the Data Object.
   * @return description
  **/
  @ApiModelProperty(value = "OPTIONAL A human readable description of the contents of the Data Object.")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DataObjectDto aliases(List<String> aliases) {
    this.aliases = aliases;
    return this;
  }

  public DataObjectDto addAliasesItem(String aliasesItem) {
    if (this.aliases == null) {
      this.aliases = new ArrayList<String>();
    }
    this.aliases.add(aliasesItem);
    return this;
  }

   /**
   * Get aliases
   * @return aliases
  **/
  @ApiModelProperty(value = "")


  public List<String> getAliases() {
    return aliases;
  }

  public void setAliases(List<String> aliases) {
    this.aliases = aliases;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataObjectDto ga4ghDataObject = (DataObjectDto) o;
    return Objects.equals(this.id, ga4ghDataObject.id) &&
        Objects.equals(this.name, ga4ghDataObject.name) &&
        Objects.equals(this.size, ga4ghDataObject.size) &&
        Objects.equals(this.created, ga4ghDataObject.created) &&
        Objects.equals(this.updated, ga4ghDataObject.updated) &&
        Objects.equals(this.version, ga4ghDataObject.version) &&
        Objects.equals(this.mimeType, ga4ghDataObject.mimeType) &&
        Objects.equals(this.checksums, ga4ghDataObject.checksums) &&
        Objects.equals(this.urls, ga4ghDataObject.urls) &&
        Objects.equals(this.description, ga4ghDataObject.description) &&
        Objects.equals(this.aliases, ga4ghDataObject.aliases);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, size, created, updated, version, mimeType, checksums, urls, description, aliases);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Ga4ghDataObjectDto {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    updated: ").append(toIndentedString(updated)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("    checksums: ").append(toIndentedString(checksums)).append("\n");
    sb.append("    urls: ").append(toIndentedString(urls)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    aliases: ").append(toIndentedString(aliases)).append("\n");
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

