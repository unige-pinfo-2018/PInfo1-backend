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
	public Response getPost(@PathParam("id") Long id) 
	{
		Post post;
		try {
			post = service.getPost(id);
		} catch (NoResultException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok().entity(post).build();
	}
	
	@PUT
	@Path("/addPost")
	@Produces({ "application/json" })
	public Response addPost(@NotNull Post newPost) throws URISyntaxException {
		service.addPost(newPost);
		return Response.status(Response.Status.CREATED).contentLocation(new URI("posts/by_id/" + newPost.getId().toString())).build();
	}
	
	
}
