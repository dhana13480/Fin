package org.finra.rmcs.constants;

public enum FileTypeEnum {
  PDF("PDF"),
  GZ("GZ"),
  ZIP("ZIP");

  private String name;
  FileTypeEnum(String name) {
    this.name = name;
  }
}
