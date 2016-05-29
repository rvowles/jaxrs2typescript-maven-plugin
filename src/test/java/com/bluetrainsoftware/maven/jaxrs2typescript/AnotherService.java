package com.bluetrainsoftware.maven.jaxrs2typescript;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
@Path("/identity2")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AnotherService {
  @GET
  public int getPeanuts();

  @POST
  String setPeanuts(@FormParam("peanuts") int peanuts);
}
