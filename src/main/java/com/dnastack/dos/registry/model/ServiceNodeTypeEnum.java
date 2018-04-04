package com.dnastack.dos.registry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicates the type of the service node (e.g. DOS, BEACON etc.). By default, it assumes to be DOS type if not specified. 
 */
public enum ServiceNodeTypeEnum {

  DOS("DOS"),

  BEACON("BEACON");

  private String value;

  ServiceNodeTypeEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ServiceNodeTypeEnum fromValue(String text) {
    for (ServiceNodeTypeEnum b : ServiceNodeTypeEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

