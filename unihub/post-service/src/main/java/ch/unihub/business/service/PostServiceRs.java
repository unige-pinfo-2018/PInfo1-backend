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


import ch.unihub.dom.Post;


@Path("/posts")
public class PostServiceRs {
	@Inject
	private PostService service;

	@GET
	@Path("/nbPosts")
	@Produces({ "application/json" })
	public String getNbUsers() 
	{
		return "{\"nbPosts\":\"" + service.getNbPosts() + "\"}";
	}
	
	@GET
	@Path("/helloWorld")
	@Produces({ "text/plain" })
	public String helloWorld() 
	{
		return "Hello World";
		//return "{\"nbPosts\":\"" + service.getNextPostId() + "\"}";
	}
	
	@GET
	@Path("/by_id/{id}")
	@Produces({ "application/json" })
    public Response getPost(@PathParam("id") Long id) {
        return postResponse(service.getPost(id));
    }
    /*
	public Response getPost(@PathParam("id") Long id) 
	{
		Post post;
		try {
			post = service.getPost(id);
		} catch (NoResultException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok().entity(post).build();
	}*/

	@PUT
	@Path("/add")
	@Produces({ "application/json" })
	public Response addPost(@NotNull Post post) throws URISyntaxException {
		service.addPost(post);
		return Response
				.status(Response.Status.CREATED)
				.contentLocation(new URI("posts/by_id/" + post.getId().toString()))
				.build();
	}

    private Response postResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Post> postOptional) {
        if (postOptional.isPresent()) {
            final Post post = postOptional.get();
            return Response.ok().entity(post).build();
        } else return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/update_post")
    @Produces({ "application/json" })
    public Response updatePost(@NotNull final Post post) {
        final Optional<Post> updatedPostOptional = service.updatePost(post);
        return updatedPostOptional.isPresent() ?
                Response.ok(updatedPostOptional.get()).build() :
                Response.status(Response.Status.NOT_FOUND).build();
    }

}
