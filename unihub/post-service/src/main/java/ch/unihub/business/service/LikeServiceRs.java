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


import ch.unihub.dom.Like;

@Path("/likes")
public class LikeServiceRs
{
    @Inject
    private PostService service;

    @GET
    @Path("/by_id/{id}")
    @Produces({ "application/json" })
    public Response getLike(@PathParam("id") Long id) {
        return likeResponse(service.getLike(id));
    }

    private Response likeResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Like> likeOptional) {
        if (likeOptional.isPresent()) {
            final Like like = likeOptional.get();
            return Response.ok().entity(like).build();
        } else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/addLike")
    @Produces({ "application/json" })
    public Response addLike(@NotNull Like like) throws URISyntaxException {
        service.addLike(like);
        return Response
                .status(Response.Status.CREATED)
                .contentLocation(new URI("likes/by_id/" + like.getId().toString()))
                .build();
    }
}