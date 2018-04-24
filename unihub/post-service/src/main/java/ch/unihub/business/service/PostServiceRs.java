package ch.unihub.business.service;
import ch.unihub.dom.Post;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Path("/posts")
public class PostServiceRs {
    private static final Logger logger = LogManager.getLogger(PostServiceRs.class);

    @Inject
	private PostService service;

	@GET
	@Path("/nbPosts")
	@Produces({ "application/json" })
	public String getNbUsers()
	{
		int n = service.getNbPosts();
		int nbPostNotComment = 0;
		for (int i=1; i<=n; i++){
			if (service.getPost(Long.parseLong(Integer.toString(i))).get().getParentId() == null){
				nbPostNotComment+=1;
			}
		}
		return "{\"nbPosts\":\"" + nbPostNotComment + "\"}";
	}
	
	@GET
	@Path("/helloWorld")
	@Produces({ "text/plain" })
	public String helloWorld() 
	{
		return "Hello World";
	}
	
	@GET
	@Path("/by_id/{id}")
	@Produces({ "application/json" })
    public Response getPost(@PathParam("id") Long id) {
        return postResponse(service.getPost(id));
    }

	/* Returns 5 posts (or less if we reach the bottom of our DB) that are not comments */
	@GET
	@Path("/contents")
	@Produces({"application/json"})
	public List getSeveralPosts(@QueryParam("nbPost") int nbPost) {
		return service.getSeveralPosts(nbPost);
	}

	@GET
	@Path("/getCommentsForPost/{idPost}")
	@Produces({ "application/json" })
	public Response getCommentsForPost(@PathParam("idPost") Long idPost) {
		List<Response> list = new ArrayList<>();
		int nbPosts = service.getNbPosts();
		for (int i = 1; i <= nbPosts; i++) {
			Long pID = service.getParentIdPost((Long.parseLong(Integer.toString(i))));
			if (pID != null) {
				Optional<Post> p = service.getPost((Long.parseLong(Integer.toString(i))));
				if (idPost.longValue() == pID.longValue()) {
					list.add(postResponse(p));
				}
			}
		}
		return Response.ok(list).build();
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
    @Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Long addPost(@NotNull Post post) throws URISyntaxException {
		return service.addPost(post);
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

    /*return list with id and a score (bigger if the post have similitudes with the post)*/
	@GET
	@Path("/searchPost")
	@Produces({ "application/json" })
	public List searchPost(@QueryParam("q") String questionUser,@QueryParam("n") int nbPost,@QueryParam("t") List<String> listTags) {
		return service.searchPost(questionUser,nbPost,listTags);
	}

	@GET
	@Path("/posts_by_ids")
	@Produces({ "application/json" })
	public List getPostsByIds(@QueryParam("id") List<Long> listId) {
		return service.getPostsByIds(listId);
	}

	@GET
	@Path("/posts_and_comments_by_tags")
	@Produces({"application/json"})
	public List getPostsAndCommentsByTags(@QueryParam("q") String questionUser,@QueryParam("n") int nbPost,@QueryParam("t") List<String> listTags) {
		return service.getPostsAndCommentsByTags(questionUser,nbPost,listTags);
	}

	@GET
	@Path("/posts_and_comments_by_ids")
	@Produces({"application/json"})
	public List getPostsAndCommentsByIds(@QueryParam("id") List<Long> listIds) {
		return service.getPostsAndCommentsByIds(listIds);
	}

	@GET
    @Path("/getPostsOfUser/{idUser}")
    @Produces({ "application/json" })
    public List<Post> getPostsOfUser(@PathParam("idUser") Long idUser) {
        return service.getPostsOfUser(idUser);
    }

    //List<Post> getCommentsByID(long parentId)
    @GET
    @Path("/getComments_by_questionID")
    @Produces({ "application/json" })
    public List getCommentsByQuestionID(@QueryParam("id") List<Long> listIds) {
        return service.getCommentsByQuestionID(listIds);
    }


    /*
	@PUT
	@Path("/addPostAndTag")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Response addPost2(@QueryParam("userId") Long userId, @QueryParam("content") String content,@QueryParam("name") List<String> name,@QueryParam("parentId") Long parentId) throws URISyntaxException {
	    Long id;
        id = service.addPostAndTag(Long userId, String content,String name,Long parentId);
        return Response
				.status(Response.Status.CREATED)
				.contentLocation(new URI("posts/by_id/" + id.toString()))
				.build();
	}*/
}
