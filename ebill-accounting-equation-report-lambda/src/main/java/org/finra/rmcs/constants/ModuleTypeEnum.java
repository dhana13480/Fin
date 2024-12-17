package org.finra.rmcs.constants;

public enum ModuleTypeEnum {
  NONE(0),
  ENABLER(4);
  private Integer id;

  ModuleTypeEnum(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public static ModuleTypeEnum getEnumById(Integer id) {
    for (ModuleTypeEnum e : values()) {
      if ((null != id) && (null != e.id) && (e.id.equals(id))) {
        return e;
      }
    }
    return ModuleTypeEnum.NONE;
  }
}
