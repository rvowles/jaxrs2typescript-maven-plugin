/*******************************************************************************
 * Copyright 2013 Raphael Jolivet
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bluetrainsoftware.maven.jaxrs2typescript.model;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RestMethod {

	private String name;
	private String path;
	private List<Param> params = new ArrayList<>();
	private HttpMethod httpMethod;
  private Param returnType;

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

  /**
   * use string interpolation for reliability.
   *
   * @param path
   */
	public void setPath(String path) {
    String newPath = Splitter.on("/").splitToList(path).stream().map(part -> {
      if (part.startsWith("{") && part.endsWith("}")) {
        return "${" + part.substring(1);
      } else {
        return part;
      }
    }).collect(Collectors.joining("/"));

//    if (path.startsWith("/") && !newPath.startsWith("/")) {
//      newPath = "/" + path;
//    }

    if (newPath.endsWith("/") && !path.endsWith("/")) {
      newPath = newPath.substring(0, newPath.length() - 2);
    }

    this.path = newPath;
	}

	public List<Param> getParams() {
    // mustache template needs this to add separators
    for(int count = 0, max = params.size() - 1; count < max; count ++) {
      params.get(count).setHasMore(true);
    }

		return params;
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

  public Param getReturnType() {
    return returnType;
  }

  public List<Param> getQueryParams() {
    return params.stream().filter(param -> param.getType() == ParamType.QUERY).collect(Collectors.toList());
  }

  public List<Param> getFormParams() {
    return params.stream().filter(param -> param.getType() == ParamType.FORM).collect(Collectors.toList());
  }

  public List<Param> getBodyParams() {
    return params.stream().filter(param -> param.getType() == ParamType.BODY).collect(Collectors.toList());
  }

  public void setReturnType(Param returnType) {
    this.returnType = returnType;
  }

  public boolean hasReturnType() {
    Class<?> originalClass = returnType.getDataType().getOriginalClass();
    return originalClass != Void.class && originalClass != void.class;
  }
}
