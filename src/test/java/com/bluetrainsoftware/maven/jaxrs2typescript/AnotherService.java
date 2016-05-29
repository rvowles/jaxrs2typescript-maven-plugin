package com.bluetrainsoftware.maven.jaxrs2typescript;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

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

  @PUT
  Response doSomething(@QueryParam("wilbur") Set<String> names);
}
