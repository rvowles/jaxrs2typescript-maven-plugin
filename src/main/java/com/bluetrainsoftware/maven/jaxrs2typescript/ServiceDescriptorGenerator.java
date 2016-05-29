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
package com.bluetrainsoftware.maven.jaxrs2typescript;

import com.bluetrainsoftware.maven.jaxrs2typescript.model.HttpMethod;
import com.bluetrainsoftware.maven.jaxrs2typescript.model.Models;
import com.bluetrainsoftware.maven.jaxrs2typescript.model.Param;
import com.bluetrainsoftware.maven.jaxrs2typescript.model.RestClass;
import com.bluetrainsoftware.maven.jaxrs2typescript.model.RestMethod;
import com.bluetrainsoftware.maven.jaxrs2typescript.model.RestService;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bluetrainsoftware.maven.jaxrs2typescript.model.ParamType.BODY;
import static com.bluetrainsoftware.maven.jaxrs2typescript.model.ParamType.FORM;
import static com.bluetrainsoftware.maven.jaxrs2typescript.model.ParamType.PATH;
import static com.bluetrainsoftware.maven.jaxrs2typescript.model.ParamType.QUERY;


/**
 * Generates a {@link RestService} description out of a service class /
 * interface
 */
public class ServiceDescriptorGenerator {

  private static final String MODULE_NAME_PLACEHOLDER = "%MODULE_NAME%";
  private static final String JSON_PLACEHOLDER = "%JSON%";

  static private final String ROOT_URL_VAR = "rootUrl";
  static private final String ADAPTER_VAR = "adapter";

  private final Map<Class<?>, RestClass> modelClasses = new HashMap<>();

  public ServiceDescriptorGenerator(Collection<? extends Class<?>> classes, Writer out, String basePath) {
    addDummyMappingForJAXRSClasses();

    Collection<RestService> restServices = generateRestServices(classes);

    Map<String, Object> mustacheModel = new HashMap<>();

    mustacheModel.put("models", getModels());
    mustacheModel.put("apis", restServices);
    mustacheModel.put("basePath", basePath);

    Template template = Mustache.compiler().compile(new InputStreamReader(getClass().getResourceAsStream("api.mustache")));

    template.execute(mustacheModel, out);

    try {
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  Models getModels() {
    return new Models(modelClasses.values().stream().filter(r -> r.getSimpleType() == null).collect(Collectors.toList()));
  }

  private RestClass addModel(RestClass clazz) {
    modelClasses.put(clazz.getOriginalClass(), clazz);

    return clazz;
  }

  /** Those classes will be transformed as "any" */
  private void addDummyMappingForJAXRSClasses() {
    addModel(new RestClass(Response.class, "any"));
    addModel(new RestClass(UriInfo.class, "any"));
    addModel(new RestClass(Request.class, "any"));
    addModel(new RestClass(Void.class, "void"));
    addModel(new RestClass(void.class, "void"));
    addModel(new RestClass(Object.class, "any"));
    addModel(new RestClass(Boolean.class, "boolean"));
    addModel(new RestClass(boolean.class, "boolean"));
    addModel(new RestClass(String.class, "string"));
    addModel(new RestClass(Long.class, "number"));
    addModel(new RestClass(long.class, "number"));
    addModel(new RestClass(Double.class, "number"));
    addModel(new RestClass(double.class, "number"));
    addModel(new RestClass(Float.class, "number"));
    addModel(new RestClass(float.class, "number"));
    addModel(new RestClass(Integer.class, "number"));
    addModel(new RestClass(int.class, "number"));
    addModel(new RestClass(Map.class, "any"));
    addModel(new RestClass(Date.class, "Date"));
  }

  /**
   * Main method to generate a REST Service desciptor out of JAX-RS service
   * class
   */
  private Collection<RestService> generateRestServices(Collection<? extends Class<?>> classes) {

    List<RestService> services = new ArrayList<RestService>();

    for (Class<?> clazz : classes) {

      RestService service = new RestService();
      service.setName(clazz.getSimpleName());

      Path pathAnnotation = clazz.getAnnotation(Path.class);

      if (pathAnnotation == null) {
        throw new RuntimeException("No @Path on class " + clazz.getName());
      }

      service.setPath(pathAnnotation.value());

      for (Method method : clazz.getDeclaredMethods()) {
        if (Modifier.isPublic(method.getModifiers()) && !method.getName().contains("$")) {
          RestMethod restMethod = generateMethod(method);

          if (restMethod != null) {
            service.addMethod(restMethod);
          }
        }
      }

      services.add(service);
    }

    return services;
  }


  private RestMethod generateMethod(Method method) {

    RestMethod restMethod = new RestMethod();
    Path pathAnnotation = method.getAnnotation(Path.class);

    restMethod.setPath(pathAnnotation == null ? "" : pathAnnotation.value());

    restMethod.setName(method.getName());

    if (method.getAnnotation(GET.class) != null) {
      restMethod.setHttpMethod(HttpMethod.GET);
    }
    if (method.getAnnotation(POST.class) != null) {
      restMethod.setHttpMethod(HttpMethod.POST);
    }
    if (method.getAnnotation(PUT.class) != null) {
      restMethod.setHttpMethod(HttpMethod.PUT);
    }
    if (method.getAnnotation(DELETE.class) != null) {
      restMethod.setHttpMethod(HttpMethod.DELETE);
    }

    if (restMethod.getHttpMethod() == null) {
      return null;
    }

    RestClass returnClass = cacheClassType(method.getReturnType(), method.getGenericReturnType());
    Param returnType = new Param(null, returnClass, Collection.class.isAssignableFrom(method.getReturnType()) || method.getReturnType().isArray());
    restMethod.setReturnType(returnType);

    restMethod.setParams(generateParams(method));

    return restMethod;
  }

  private RestClass cacheClassType(Class<?> type, Type genericType) {
    // have to figure out what this actually is (it could be generic or an array)
    final RestClass clazz = new RestClass(type, null, genericType);

    // to allow us to deal with recursive issues, we change this to always add the class
    if (modelClasses.get(clazz.getOriginalClass()) == null) {
      modelClasses.put(clazz.getOriginalClass(), clazz);

      Class<?> restClass = clazz.getOriginalClass();

      if (restClass.getName().startsWith("java.lang")) {
        throw new RuntimeException(String.format("Attempted to use class `%s` that has no default mapper.", restClass.getName()));
      }

      Arrays.stream(restClass.getDeclaredFields()).forEach(field -> {
        // protect against Groovy
        if (!field.getName().equals("property") && !field.getName().contains("$") &&
              !(field.getName().equals("metaClass") && field.getType().getName().equals("groovy.lang.MetaClass"))) {
          if (field.getName().equalsIgnoreCase("roles")) {
            System.out.println("here");
          }
          Param param = new Param(field.getName(), cacheClassType(field.getType(), field.getGenericType()),
            field.getType().isArray() || Collection.class.isAssignableFrom(field.getType()));

          clazz.addParam(param);
        }
      });

      if (restClass.getSuperclass() != null && restClass.getSuperclass() != Object.class) {
        clazz.setParent(cacheClassType(restClass.getSuperclass(), restClass.getGenericSuperclass()));
      }
    }

    return clazz;
  }

  private List<Param> generateParams(Method method) {
    List<Param> params = Arrays.stream(method.getParameters()).map(p -> {
      RestClass type = cacheClassType(p.getType(), p.getParameterizedType());
      boolean array = Collection.class.isAssignableFrom(p.getType()) || p.getType().isArray();
      Param param = new Param(null, type, array);

      param.setName("body");
      param.setType(BODY);

      for (Annotation ann : p.getAnnotations()) {
        fillParam(ann, param);
      }

      return param;
    }).collect(Collectors.toList());

    return params;
  }

  private void fillParam(Annotation annot, Param param) {
    if (annot instanceof PathParam) {
      param.setType(PATH);
      param.setName(((PathParam) annot).value());
    } else if (annot instanceof QueryParam) {
      param.setType(QUERY);
      param.setName(((QueryParam) annot).value());
    } else if (annot instanceof FormParam) {
      param.setType(FORM);
      param.setName(((FormParam) annot).value());
    } else if (annot instanceof Context) {
      param.setContext(true);
    }
  }


}
