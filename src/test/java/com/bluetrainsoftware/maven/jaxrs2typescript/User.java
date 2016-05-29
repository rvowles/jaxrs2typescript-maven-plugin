package com.bluetrainsoftware.maven.jaxrs2typescript;

import java.util.Set;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class User {
  private String firstName;
  private String lastName;
  private String id;
  private Set<String> roles;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }
}
