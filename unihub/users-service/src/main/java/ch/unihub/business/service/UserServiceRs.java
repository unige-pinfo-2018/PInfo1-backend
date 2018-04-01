package ch.unihub.business.service;

import ch.unihub.dom.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/users")
public class UserServiceRs {
	@Inject
	private UserService service;

	private Logger logger = LoggerFactory.getLogger(UserServiceRs.class);

	@GET
	@Path("/nbUsers")
	@Produces({ "application/json" })
	public String getNbUsers() {
		return "{\"nbUsers\":\"" + service.getNbUsers() + "\"}";
		}

	@GET
	@Path("/by_username/{username}")
	@Produces({ "application/json" })
	public Response getUser(@PathParam("username") String username) {
		User user;
		try {
			user = service.getUser(username);
		} catch (NoResultException e) {
			logger.error(e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		user.setPassword("*****");
		return Response.ok().entity(user).build();
	}

	@GET
	@Path("/by_id/{id}")
	@Produces({ "application/json" })
	public Response getUser(@PathParam("id") Long id) {
		User user;
		try {
			user = service.getUser(id);
		} catch (NoResultException e) {
			logger.error(e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		user.setPassword("*****");
		return Response.ok().entity(user).build();
	}

	@PUT
	@Path("/add")
	@Produces({ "application/json" })
	public Response addUser(@NotNull User user) throws URISyntaxException {
		service.addUser(user);
		return Response
				.status(Response.Status.CREATED)
				.contentLocation(new URI("users/by_id/" + user.getId().toString()))
				.build();
	}
}
