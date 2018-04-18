package ch.unihub.business.service;

import ch.unihub.dom.Post;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Path("/posts")
public class PostServiceRs {
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
		//return "{\"nbPosts\":\"" + service.getNextPostId() + "\"}";
	}
	
	@GET
	@Path("/by_id/{id}")
	@Produces({ "application/json" })
    public Response getPost(@PathParam("id") Long id) {
        return postResponse(service.getPost(id));
    }

	@GET
	@Path("/content_by_ids/")
	@Produces({ "application/json" })
	public Response getContent(
			@QueryParam("from") int from,
			@QueryParam("to") int to) {
		List<Response> list = new ArrayList<>();
		for(int i = from; i <= to; i++)
			if (service.getPost(Long.parseLong(Integer.toString(i))).get().getParentId() != null && to<service.getNbPosts()){
				to+=1;
			} else {
				list.add(postResponse(service.getPost(Long.parseLong(Integer.toString(i)))));
			}
		return Response.ok(list).build();
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
	@Path("/getCommentsForPosts/")
	@Produces({ "application/json" })
	public Response getCommentsForPosts(
			@QueryParam("from") int from,
			@QueryParam("to") int to) {
		HashMap<Integer, List<Post>> commentsForAllPosts = new HashMap<>();
		int nbPosts = service.getNbPosts();
		for(int i = from; i <= to; i++){
			if (service.getPost(Long.parseLong(Integer.toString(i))).get().getParentId() != null && to<service.getNbPosts()){
				to+=1;
			} else {
				List<Post> list = new ArrayList<>();
				for (int j = 1; j <= nbPosts; j++) {
					Long pID = service.getParentIdPost((Long.parseLong(Integer.toString(j))));
					if (pID != null) {
						Optional<Post> p = service.getPost((Long.parseLong(Integer.toString(j))));
						if (Long.parseLong(Integer.toString(i)) == pID) {
							p.ifPresent(list::add);
						}
					}
				}
				commentsForAllPosts.put(i, list);
			}
		}
		return Response.ok(commentsForAllPosts).build();
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

    /*return list with id and a score (bigger if the post have similitudes with the post)*/
	@GET
	@Path("/searchPost")
	@Produces({ "application/json" })
	public List searchPost(@QueryParam("q") String questionUser,@QueryParam("n") int nbPost,@QueryParam("t") List<String> listTags) {
		return service.searchPost(questionUser,nbPost,listTags);
	}
}
