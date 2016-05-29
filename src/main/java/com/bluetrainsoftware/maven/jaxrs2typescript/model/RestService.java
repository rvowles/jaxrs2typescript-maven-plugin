/*******************************************************************************
 * Copyright 2013 Raphael Jolivet
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bluetrainsoftware.maven.jaxrs2typescript.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Root descrpition of a service */
public class RestService {

  private String name;
  private String path;
  private final Map<String, RestMethod> methods = new HashMap<String, RestMethod>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;

    if (!path.startsWith("/")) {
      this.path = "/" + path;
    }

    if (!path.endsWith("/")) {
      this.path += "/";
    }
  }

  public Collection<RestMethod> getMethods() {
    return methods.values();
  }

  public void addMethod(RestMethod method) {
    methods.put(method.getName(), method);
  }


}
