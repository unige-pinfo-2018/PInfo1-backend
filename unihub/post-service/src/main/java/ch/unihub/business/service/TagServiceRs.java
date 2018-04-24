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
import java.util.List;
import java.util.Optional;


import ch.unihub.dom.Tag;

@Path("/tags")
public class TagServiceRs
{
    @Inject
    private PostService service;

    @GET
    @Path("/by/{columnId}/{value}")
    @Produces({ "application/json" })
    public Response getTag(@PathParam("value") String value,@PathParam("columnId") String columnId) {
        return tagResponse(service.getTag(value,columnId));
    }

    private Response tagResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Tag> tagOptional) {
        if (tagOptional.isPresent()) {
            final Tag tag = tagOptional.get();
            return Response.ok().entity(tag).build();
        } else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/addTag")
    @Produces({ "application/json" })
    public Response addTag(@NotNull Tag tag) throws URISyntaxException {
        service.addTag(tag);
        return Response
                .status(Response.Status.CREATED)
                .contentLocation(new URI("tags/by_id/" + tag.getId().toString()))
                .build();
    }
    //void addTags(Long postId, List<String> lisName);
    @PUT
    @Path("/addTags")
    @Produces({ "application/json" })
    public Response addTag(@QueryParam("postId") Long postId,@QueryParam("names") List<String> names) throws URISyntaxException {
        service.addTags(postId,names);
        return Response
                .status(Response.Status.CREATED)
                .build();
    }
}