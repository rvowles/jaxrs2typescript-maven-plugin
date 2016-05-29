package com.bluetrainsoftware.maven.jaxrs2typescript;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.ws.rs.Path;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generate typescript file out of RESt service definition
 */
@Mojo(name = "generate",
  defaultPhase = LifecyclePhase.PROCESS_CLASSES,
  configurator = "include-project-dependencies",
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class MainMojo extends AbstractMojo {

  @Parameter(required = true)
  List<SourceModule> sourceModules;

  /**
   * Path to output typescript folder
   * The name will be moduleName.d.ts
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/ts")
  private File tsOutFolder;

  /**
   * Path to output Js file
   * The name will be moduleName.js
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/js")
  private File jsOutFolder;

  @Parameter(defaultValue = "${project}", readonly = true)
  MavenProject project;

  private String artifactToUrl(Artifact art) {
    return art.getFile().getAbsolutePath();
  }

  protected void exportModule(SourceModule module, Set<Class<?>> classesToExport) {
    // Descriptor for service

    try {
      Writer writer = createFileAndGetWriter(tsOutFolder, module.getTypescriptModule() + ".ts");

      ServiceDescriptorGenerator descGen = new ServiceDescriptorGenerator(classesToExport, writer, module.getBasePath() == null ? "" : module.getBasePath());

      writer.flush();
      writer.close();

    } catch (Exception ex) {
      getLog().error(String.format("Failed to generate module %s", module.getPackageName()), ex);
    }
  }

  private void addClassByName(String name, Set<Class<?>> classesToExport) {
    try {
      classesToExport.add(Class.forName(name));
    } catch (ClassNotFoundException e) {
      getLog().error("Cannot load class " + name, e);
    }
  }

  @Override
  public void execute() throws MojoExecutionException {
    try {
      sourceModules.forEach(module -> {
        Set<Class<?>> classesToExport = new HashSet<>();

        if (module.getClassName() != null) {
          addClassByName(module.getClassName(), classesToExport);
        }

        if (module.getClassNames() != null && module.getClassNames().size() > 0) {
          module.getClassNames().stream().forEach(name -> addClassByName(name, classesToExport));
        }

        if (module.getPackageName() != null) {
          new FastClasspathScanner(module.getPackageName())
            .matchClassesWithAnnotation(Path.class, classesToExport::add).scan();
        }

        if (classesToExport.size() > 0) {
          exportModule(module, classesToExport);
        } else {
          getLog().error(String.format("found no classes or interfaces in package/path `%s`", module.getTypescriptModule()));
        }
      });


    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private Writer createFileAndGetWriter(File folder, String fileName) throws IOException {
    File file = new File(folder, fileName);
    getLog().info("Create file : " + file.getCanonicalPath());
    file.createNewFile();
    FileOutputStream stream = new FileOutputStream(file);
    OutputStreamWriter writer = new OutputStreamWriter(stream);
    return writer;
  }

  ;
}
