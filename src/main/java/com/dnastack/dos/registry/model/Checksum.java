package com.dnastack.dos.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
public class Checksum {
  private String checksum = null;
  private String type = null;

  public Checksum checksum(String checksum) {
    this.checksum = checksum;
    return this;
  }
}

