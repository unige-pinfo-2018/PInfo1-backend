package ch.unihub.business.service;

import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Digits;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.*;
import java.util.Optional;


import ch.unihub.dom.Dislike;

@Path("/dislikes")
public class DislikeServiceRs
{
    @Inject
    private PostService service;

    @GET
    @Path("/by/{columnId}/{value}")
    @Produces({ "application/json" })
    public Response getDislike(@PathParam("value") Long value,@PathParam("columnId") String columnId) {
        return dislikeResponse(service.getDislike(value,columnId));
    }


    private Response dislikeResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Dislike> dislikeOptional) {
        if (dislikeOptional.isPresent()) {
            final Dislike dislike = dislikeOptional.get();
            return Response.ok().entity(dislike).build();
        } else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/addDislike")
    @Produces({ "application/json" })
    public Response addDislike(@NotNull Dislike dislike) throws URISyntaxException {
        service.addDislike(dislike);
        return Response
                .status(Response.Status.CREATED)
                .build();
    }
}