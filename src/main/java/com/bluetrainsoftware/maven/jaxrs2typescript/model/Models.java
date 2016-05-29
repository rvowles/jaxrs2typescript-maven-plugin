package com.bluetrainsoftware.maven.jaxrs2typescript.model;

import java.util.List;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class Models {
  private final List<RestClass> model;

  public Models(List<RestClass> model) {
    this.model = model;
  }

  public List<RestClass> getModel() {
    return model;
  }
}
