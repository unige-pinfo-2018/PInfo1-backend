package ch.unihub.business.service;

import ch.unihub.dom.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * @author Arthur Deschamps
 */
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
		return userResponse(service.getUser(username));
	}

	@GET
	@Path("/by_id/{id}")
	@Produces({ "application/json" })
	public Response getUser(@PathParam("id") Long id) {
		return userResponse(service.getUser(id));
	}

	@DELETE
	@Path("/delete_by_id/{id}")
	@Produces({ "application/json" })
	public Response deleteUser(@PathParam("id") Long id) {
		try {
			service.deleteUser(id);
		} catch (NotFoundException e) {
			logger.trace(e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("/delete_by_username/{username}")
	@Produces({ "application/json" })
	public Response deleteUser(@PathParam("username") String username) {
		try {
			service.deleteUser(username);
		} catch (NotFoundException e) {
			logger.trace(e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@POST
	@Path("/update_user")
	@Produces({ "application/json" })
	public Response updateUser(@NotNull final User user) {
		final Optional<User> updatedUserOptional = service.updateUser(user);
		return updatedUserOptional.isPresent() ?
				Response.ok(updatedUserOptional.get()).build() :
				Response.status(Response.Status.NOT_FOUND).build();
	}

	private Response userResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<User> userOptional) {
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			return Response.ok().entity(user).build();
		} else return Response.status(Response.Status.NOT_FOUND).build();
	}
}
