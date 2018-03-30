package ch.unihub.business.service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/users")
public class UserServiceRs {
	@Inject
	private UserService service;

	@GET
	@Path("/nbUsers")
	@Produces({ "application/json" })
	public String getNbUsers() {
		return "{\"nbUsers\":\"" + service.getNbUsers() + "\"}";
		}
	
	@GET
	@Path("/helloWorld")
	@Produces({ "text/plain" })
	public String helloWorld() {
		return "Hello World";
		}
}
