package ch.unihub.business.service;

import ch.unihub.dom.AccountConfirmation;
import ch.unihub.dom.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Arthur Deschamps
 */

@Path("/users")
public class UserServiceRs {
	@Inject
	private UserService service;

	@Inject
	private EmailSender emailSender;

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

	@GET
	@Path("/by_email/{email}")
	@Produces({ "application/json" })
	public Response getUserByEmail(@PathParam("email") String email) {
		logger.info("trying to find a user by its email");
		return userResponse(service.getUserByEmail(email));
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

	@PUT
	@Path("/add")
	@Produces("application/json")
	@Consumes("application/json")
	public Response addUser(@NotNull final User user) throws URISyntaxException {
		// Checks if the username is already taken
		if (service.getUser(user.getUsername()).isPresent())
			return Response.status(Response.Status.CONFLICT).build();
		// Verifies for malformed fields
		if (user.getEmail() == null || user.getUsername() == null || user.getUsername().length() < 2 ||
				user.getUsername().length() > 35 || user.getPassword() == null || user.getPassword().length() < 2 ||
				user.getPassword().length() > 1000)
			return Response.status(Response.Status.BAD_REQUEST).build();
		// If user doesn't exist and all fields are correct, add it to the database
		user.setIsConfirmed(false);
		// Deletes all potential confirmations (since a confirmation must be unique).
		service.deleteAccountConfirmations(user.getEmail());
		service.createUser(user);
		// Creates an account confirmation
		service.createAccountConfirmation(user).ifPresent(confirmationId ->
			// Sends the confirmation link by email to the user
            emailSender.sendRegistrationMail(user.getEmail(), user.getUsername(), confirmationId)
        );
		return Response
				.status(Response.Status.CREATED)
				.contentLocation(new URI("users/by_id/" + user.getId().toString()))
				.build();
	}

	@GET
	@Path("/confirm")
	@Produces("application/json")
	public Response confirmUser(@QueryParam("email") String email, @QueryParam("id") String confirmationId) {
		List<AccountConfirmation> confirmations = service.findAccountConfirmations(email);
		if (confirmations.size() > 0 &&
				confirmations.stream().anyMatch(accountConfirmation ->
						accountConfirmation.getUserEmail().equals(email) &&
						accountConfirmation.getConfirmationId().equals(confirmationId))) {
			service.getUserByEmail(email).ifPresent(user -> {
				user.setIsConfirmed(true);
				service.updateUser(user);
			});
			// Deletes all potential account confirmations since the user was either confirmed or doesn't exist in
			// database.
			service.deleteAccountConfirmations(email);
			return Response.ok().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	private Response userResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<User> userOptional) {
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			user.setPassword("*****");
			return Response.ok().entity(user).build();
		} else return Response.status(Response.Status.NOT_FOUND).build();
	}
}
