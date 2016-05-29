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

public class Param {
	private String name;
  private RestClass dataType;
	private ParamType type;
	private boolean context = false;
  private boolean array;

  // this is for mustache
  private boolean hasMore;

  public Param() {
  }

  public Param(String name, RestClass dataType, boolean array) {
    this.name = name;
    this.dataType = dataType;
    this.array = array;
  }

  public Param(RestClass dataType, boolean array) {
    this(null, dataType, array);
  }

  public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParamType getType() {
		return type;
	}

	public void setType(ParamType type) {
		this.type = type;
	}

	/** If true, this param corresponds to a technical @Context param, and should be ignored in output generation */
	public boolean isContext() {
		return context;
	}

	public void setContext(boolean context) {
		this.context = context;
	}

  public RestClass getDataType() {
    return dataType;
  }

  public void setDataType(RestClass dataType) {
    this.dataType = dataType;
  }

  public boolean isArray() {
    return array;
  }

  public void setArray(boolean array) {
    this.array = array;
  }

  public String getClassName() {
    return dataType != null ? dataType.getClassName() : null;
  }

  public boolean isHasMore() {
    return hasMore;
  }

  public void setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
  }
}
