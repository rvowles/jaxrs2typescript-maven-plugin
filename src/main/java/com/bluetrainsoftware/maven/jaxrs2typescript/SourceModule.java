package com.bluetrainsoftware.maven.jaxrs2typescript;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class SourceModule {
  @Parameter(required = false)
  private String packageName;

  @Parameter
  private String className;

  @Parameter
  private List<String> classNames;

  @Parameter
  private String basePath;

  @Parameter(required = true)
  private String typescriptModule;

  public SourceModule() {
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getTypescriptModule() {
    return typescriptModule;
  }

  public void setTypescriptModule(String typescriptModule) {
    this.typescriptModule = typescriptModule;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<String> getClassNames() {
    return classNames;
  }

  public void setClassNames(List<String> classNames) {
    this.classNames = classNames;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
}
