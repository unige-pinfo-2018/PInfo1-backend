package ch.unihub.business.service;
import ch.unihub.dom.Post;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.*;

@Path("/posts")
public class PostServiceRs {
    private static final Logger logger = LogManager.getLogger(PostServiceRs.class);

    @Inject
	private PostService service;

	@GET
	@Path("/nbPosts")
	@Produces({ "application/json" })
	public String getNbPosts()
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

    // Le retour de la fonction disparu
    /* Returns 5 posts (or less if we reach the bottom of our DB) that are not comments with comments */
    @GET
    @Path("/contents")
    @Produces({"application/json"})
    public Response getSeveralPosts() {
        List result = service.getSeveralPosts();
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

	/* this function is not good and have a better function create for that : List<Post> getCommentsByID(long parentId)
	* so for now i have just copy cat the code of the good function and transform the Long in List<Long>
	*/
	// TODO destroy that
	@GET
	@Path("/getCommentsForPost/{idPost}")
	@Produces({ "application/json" })
	public Response getCommentsForPost(@PathParam("idPost") Long idPost) {
        List<Long> idPostList= new ArrayList<>();
        idPostList.add(idPost);
        List result = service.getCommentsByQuestionID(idPostList);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        /*
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
		return Response.ok(list).build();*/
	}
	
    @GET
    @Path("/userId_by_id/{id}")
    @Produces({ "application/json" })
    public Response getUserIdPost(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(service.getUserIdPost(id)).build();
    }

	@GET
	@Path("/parentId_by_id/{id}")
	@Produces({ "application/json" })
	public Response getParentIdPost(@PathParam("id") Long id) {
		return Response.status(Response.Status.OK).entity(service.getParentIdPost(id)).build();
	}

	@GET
	@Path("/replyToId_by_id/{id}")
	@Produces({ "application/json" })
	public Response getReplyToIdPost(@PathParam("id") Long id) {
        Long result = service.getReplyToIdPost(id);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		//return service.getReplyToIdPost(id);
	}




    // give for each posts in "idPosts" from a user a list with
    //      a list of nbUpvote for each postId
    //      a map with like and dislike for each post from an userId
    // http://localhost:18080/post-service/rest/posts/likedislike_of_posts_from_userid/1
    // {
    //      "idPosts": [1,2,3]
    // }
	@POST
	@Path("/nbUpvotes_by_ids/{userId}")
	@Produces({ "application/json" })
    @Consumes("application/json")
	public Response getNbUpvotes(@NotNull final String idPosts,@PathParam("userId") Long userId) {
        final JsonArray postIds = Json.createReader(new StringReader(idPosts)).readObject().getJsonArray("idPosts");
        List result=new ArrayList();
        result.add(
                postIds
                        .stream()
                        .map(id -> service.getNbUpvotes(Long.valueOf(id.toString())))
                        // Discards null objects
                        .filter(Objects::nonNull)
                        // Hides passwords
                        .toArray());
        result.add(
                postIds
                        .stream()
                        .map(id -> service.getLikeDislikeOfPostsFromUser(Long.valueOf(id.toString()),userId))
                        // Discards null objects
                        .filter(Objects::nonNull)
                        // Hides passwords
                        .toArray());
        return Response.ok(
                result
        ).build();
        //return Response.status(Response.Status.OK).entity(service.getNbUpvotes(idPosts)).build();
		//return service.getNbUpvotes(idPost);
	}


	@GET
	@Path("/date_by_id/{idPost}")
	@Produces({ "application/json" })
	public Response getDate(@PathParam("idPost") Long idPost) {
		return Response.status(Response.Status.OK).entity(service.getDate(idPost)).build();
		//return service.getDate(idPost);
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
	public Response getListIdTags(@PathParam("idPost") Long idPost) {
		return Response.status(Response.Status.OK).entity(service.getListIdTags(idPost)).build();
		//return service.getListIdTags(idPost);
	}

	@PUT
	@Path("/addPost")
    @Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Response addPost(@NotNull Post post) throws URISyntaxException {
		if (post.getUserId() == null || post.getContent() == null || post.getContent().length() > 50000)
			return Response.status(Response.Status.BAD_REQUEST).build();
		else
			return Response.status(Response.Status.CREATED).entity(service.addPost(post)).build();
		//return service.addPost(post);
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
	public Response searchPost(@QueryParam("q") String questionUser,@QueryParam("n") int nbPost,@QueryParam("t") List<String> listTags) {
        List  listSearch = service.searchPost(questionUser,nbPost,listTags);
        return listSearch.size() != 0 ?
                Response.status(Response.Status.OK).entity(listSearch).build() :
                Response.status(Response.Status.NOT_FOUND).build();

        //return Response.status(Response.Status.OK).entity(service.searchPost(questionUser,nbPost,listTags)).build();
		//return service.searchPost(questionUser,nbPost,listTags);
	}

	@GET
	@Path("/posts_by_ids")
	@Produces({ "application/json" })
	public Response getPostsByIds(@QueryParam("id") List<Long> listId) {
        List result = service.getPostsByIds(listId);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		//return service.getPostsByIds(listId);
	}

	@GET
	@Path("/posts_and_comments_by_tags")
	@Produces({"application/json"})
	public Response getPostsAndCommentsByTags(@QueryParam("q") String questionUser,@QueryParam("n") int nbPost,@QueryParam("t") List<String> listTags) {
        List result = service.getPostsAndCommentsByTags(questionUser,nbPost,listTags);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		//return service.getPostsAndCommentsByTags(questionUser,nbPost,listTags);
	}

	@GET
	@Path("/posts_and_comments_by_ids")
	@Produces({"application/json"})
	public Response getPostsAndCommentsByIds(@QueryParam("id") List<Long> listIds) {
        List result = service.getPostsAndCommentsByIds(listIds);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		//return service.getPostsAndCommentsByIds(listIds);
	}

	@GET
    @Path("/getPostsOfUser/{idUser}")
    @Produces({ "application/json" })
    public Response getPostsOfUser(@PathParam("idUser") Long idUser) {
        List<Post> result = service.getPostsOfUser(idUser);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        //return service.getPostsOfUser(idUser);
    }

    //List<Post> getCommentsByID(long parentId)
    @GET
    @Path("/getComments_by_questionID")
    @Produces({ "application/json" })
    public Response getCommentsByQuestionID(@QueryParam("id") List<Long> listIds) {
        List result = service.getCommentsByQuestionID(listIds);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }else
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        //return service.getCommentsByQuestionID(listIds);
    }

    @POST
    @Path("/login")
    @Produces("application/json")
    @Consumes("application/json")
    public Response login(@NotNull final String usernameAndPassword) {
        JsonObject usernameAndPasswordArray = Json.createReader(new StringReader(usernameAndPassword)).readObject();
        final String username = usernameAndPasswordArray.getString("username");
        final String password = usernameAndPasswordArray.getString("password");
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);

        Subject currentUser = SecurityUtils.getSubject();

        //Authenticate the subject by passing
        //the user name and password token
        //into the login method
        boolean successfulAuth = false;
        try {
            currentUser.login(token);
            successfulAuth = true;
        } catch  ( UnknownAccountException uae ) {
            logger.error("Unknown account");
        } catch  ( IncorrectCredentialsException ice ) {
            logger.error("Incorrect username/password combination");
        } catch  ( LockedAccountException lae ) {
            logger.error("Account is locked. Impossible to authenticate.");
        } catch  ( ExcessiveAttemptsException eae ) {
            logger.error("You've reached the maximum number of connection attempts.");
        } catch ( AuthenticationException ae ) {
            //unexpected error?
            logger.error("Authentication error: " + ae.getMessage());
        }

        // If connection was not successful, sends a Unauthorized response
        if (!successfulAuth) return Response.status(401).build();
        return Response.ok(currentUser.getSession()).build();
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
