package com.bluetrainsoftware.maven.jaxrs2typescript;

/**
 * No idea who had the original copyright on this, it appears in multiple places and appears to have been replaced
 * over and over again.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A custom ComponentConfigurator which adds the project's runtime classpath
 * elements to the
 *
 * @author Brian Jackson
 * @since Aug 1, 2008 3:04:17 PM
 */
@Component(role=org.codehaus.plexus.component.configurator.ComponentConfigurator.class, hint = "include-project-dependencies")
public class IncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator {
//  @Requirement(role= org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup.class, hint="default")
//  private ConverterLookup converterLookup;

  @Override
  public void configureComponent(Object component, PlexusConfiguration configuration,
                                 ExpressionEvaluator expressionEvaluator,
                                 ClassRealm containerRealm,
                                 ConfigurationListener listener) throws ComponentConfigurationException {
    addProjectDependenciesToClassRealm(expressionEvaluator, containerRealm);

    converterLookup.registerConverter(new ClassRealmConverter(containerRealm));

    ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();

    converter.processConfiguration(converterLookup, component, containerRealm, configuration,
      expressionEvaluator, listener);

  }

  private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm)
    throws ComponentConfigurationException {
    List<String> runtimeClasspathElements;
    try {
      // noinspection unchecked
      runtimeClasspathElements = (List<String>) expressionEvaluator
        .evaluate("${project.runtimeClasspathElements}");
    } catch (ExpressionEvaluationException e) {
      throw new ComponentConfigurationException(
        "There was a problem evaluating: ${project.runtimeClasspathElements}", e);
    }

    // Add the project dependencies to the ClassRealm
    final URL[] urls = buildURLs(runtimeClasspathElements);
    for (URL url : urls) {
      containerRealm.addURL(url);
    }

    Set<Artifact> artifacts = null;

    try {
      artifacts = (Set<Artifact>)( ((MavenProject)expressionEvaluator.evaluate("${project}")).getArtifacts());
    } catch (ExpressionEvaluationException e) {
      e.printStackTrace();
    }

    Optional.of(artifacts).ifPresent(s -> s.stream().map(art -> {
      try {
        return art.getFile().toURI().toURL();
      } catch (MalformedURLException e) {
        return null;
      }
    })
      .filter(r -> r != null)
      .forEach(containerRealm::addURL));
  }

  private URL[] buildURLs(List<String> runtimeClasspathElements) throws ComponentConfigurationException {
    // Add the projects classes and dependencies
    List<URL> urls = new ArrayList<URL>(runtimeClasspathElements.size());
    for (String element : runtimeClasspathElements) {
      try {
        final URL url = new File(element).toURI().toURL();
        urls.add(url);
      } catch (MalformedURLException e) {
        throw new ComponentConfigurationException("Unable to access project dependency: " + element, e);
      }
    }

    // Add the plugin's dependencies (so Trove stuff works if Trove isn't on
    return urls.toArray(new URL[urls.size()]);
  }

}