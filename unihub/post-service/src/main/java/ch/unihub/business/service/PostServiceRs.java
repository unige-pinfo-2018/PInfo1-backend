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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
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


    @GET
    @Path("/userId_by_id/{id}")
    @Produces({ "application/json" })
    public Long getUserIdPost(@PathParam("id") Long id) {
        return service.getUserIdPost(id);
    }

	@GET
	@Path("/parentId_by_id/{id}")
	@Produces({ "application/json" })
	public Long getParentIdPost(@PathParam("id") Long id) {
		return service.getParentIdPost(id);
	}

	@GET
	@Path("/replyToId_by_id/{id}")
	@Produces({ "application/json" })
	public Long getReplyToIdPost(@PathParam("id") Long id) {
		return service.getReplyToIdPost(id);
	}

	@GET
	@Path("/nbUpvotes_by_id/{idPost}")
	@Produces({ "application/json" })
	public Long getNbUpvotes(@PathParam("idPost") Long idPost) {
		return service.getNbUpvotes(idPost);
	}

	@GET
	@Path("/date_by_id/{idPost}")
	@Produces({ "application/json" })
	public Date getDate(@PathParam("idPost") Long idPost) {
		return service.getDate(idPost);
	}

	@GET
	@Path("/content_by_id/{idPost}")
	@Produces({ "application/json" })
	public String getContent(@PathParam("idPost") Long idPost) {
		return "{\"content\":\"" + service.getContent(idPost) + "\"}" ;
	}

	@GET
	@Path("/listIdTags_by_id/{idPost}")
	@Produces({ "application/json" })
	public List<Long> getListIdTags(@PathParam("idPost") Long idPost) {
		return service.getListIdTags(idPost);
	}

	@PUT
	@Path("/addPost")
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
