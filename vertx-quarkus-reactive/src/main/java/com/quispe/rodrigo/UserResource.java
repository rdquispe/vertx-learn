package com.quispe.rodrigo;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UserResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @GET
    public Uni<List<Users>> get() {
        LOG.info("Get all users...");
        return Users.listAll(Sort.by("id"));
    }
}