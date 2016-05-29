package com.bluetrainsoftware.maven.jaxrs2typescript;

import javax.naming.NoPermissionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * I just want to make it clear I wouldn't design it like this, I would more probably use
 * /identity/{id}/validate/{password}
 *
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
@Path("/identity")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface IdentityService {

  @POST
  @Path("{id}/validate/{passwd}")
  User validateUser(@PathParam("id") String id, @PathParam("passwd") String password)
    throws NotFoundException, NoPermissionException;
}
