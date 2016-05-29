package com.bluetrainsoftware.maven.jaxrs2typescript.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class RestClass {
  private final Class<?> originalClass;
  private String simpleType;
  private String className;
  private RestClass parent;
  private List<Param> params = new ArrayList<Param>();

  public RestClass(Class<?> originalClass) {
    this(originalClass, null, null);
  }

  public RestClass(Class<?> originalClass, String simpleType) {
    this(originalClass, simpleType, null);
  }

  public RestClass(Class<?> originalClass, String simpleType, Type genericType) {
    this.simpleType = simpleType;

    if (simpleType == null) {
      if (originalClass.isArray()) {
        this.originalClass = originalClass.getComponentType();
      } else if (Collection.class.isAssignableFrom(originalClass)) {
        if (genericType == null) {
          throw new RuntimeException(String.format("Found class `%s` with no generic type but came from collection.", originalClass.getName()));
        }
        // want the generic type
        this.originalClass = (Class)((ParameterizedType)genericType).getActualTypeArguments()[0];
      } else {
        this.originalClass = originalClass;
      }

      this.className = this.originalClass.getSimpleName();
    } else {
      this.className = simpleType;
      this.originalClass = originalClass;
    }
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public RestClass getParent() {
    return parent;
  }

  public void setParent(RestClass parent) {
    this.parent = parent;
  }

  public List<Param> getParams() {
    return params;
  }

  public void addParam(Param param) {
    this.params.add(param);
  }

  public Class<?> getOriginalClass() {
    return originalClass;
  }

  public String getSimpleType() {
    return simpleType;
  }

  public void setSimpleType(String simpleType) {
    this.simpleType = simpleType;
  }
}
