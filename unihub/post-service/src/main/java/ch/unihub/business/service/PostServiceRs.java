package ch.unihub.business.service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/post")
public class PostServiceRs {
	@Inject
	private PostService service;

	@GET
	@Path("/nbPosts")
	@Produces({ "application/json" })
	public String getNbUsers() {
		return "{\"nbPosts\":\"" + service.getNbPosts() + "\"}";
		}
	
	@GET
	@Path("/helloWorld")
	@Produces({ "text/plain" })
	public String helloWorld() {
		return "Hello World";
		}
}
