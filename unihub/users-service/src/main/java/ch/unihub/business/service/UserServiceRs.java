package ch.unihub.business.service;

import ch.unihub.dom.AccountConfirmation;
import ch.unihub.dom.ResetPasswordRequest;
import ch.unihub.dom.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
	@Consumes("application/json")
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

	@POST
	@Path("/by_ids")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getUsersByIds(@NotNull final String ids) {
		final JsonArray userIds = Json.createReader(new StringReader(ids)).readObject().getJsonArray("ids");
		return Response.ok(
				userIds
						.stream()
						.map(id -> service.getUser(Long.valueOf(id.toString())).orElse(null))
						// Discards null objects
						.filter(Objects::nonNull)
						// Hides passwords
						.peek(user -> user.setPassword(null))
						.toArray()
		).build();
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
		} catch (NotFoundException | EJBException e) {
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
		} catch (NotFoundException | EJBException e) {
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
		if (updatedUserOptional.isPresent()) {
			final User updatedUser = updatedUserOptional.get();
			updatedUser.setPassword(null);
			return Response.ok(updatedUser).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
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

	/**
	 * Works as long as a cookie is passed and hasn't expired.
	 * Do not use for sensitive operations (such as changing the password, modifying the email, etc).
	 *
	 * @return If the user's provided the correct credentials during the current session or during
	 * a previous session and is remembered (by a cookie).
	 */
	@GET
	@Path("/isLoggedIn")
	@Produces("application/json")
	public Response isLoggedIn() {
		final String username = (String) SecurityUtils.getSubject().getPrincipal();
		final User currentUser = service.getUser(username).orElse(null);
		return Response.ok(new Object[] {
				username != null,
				currentUser
		}).build();
	}

	/**
	 * Works only once per "login" call.
	 * Only use for operations that need strict authentication.
	 * 
	 * @return If the user's provided the correct credentials during the current session.
	 */
	@GET
	@Path("/isAuthenticated")
	@Produces("application/json")
	public Response isAuthenticated() {
		return Response.ok(SecurityUtils.getSubject().isAuthenticated()).build();
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

	@GET
	@Path("/request_password_reset")
	@Produces("application/json")
	public Response requestPasswordReset(@QueryParam("email") String email) {
		if (service.getUserByEmail(email).isPresent()) {
			final String requestId = service.createPasswordResetRequest(email);
			emailSender.sendPasswordResettingEmail(email, requestId);
			return Response.ok().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Path("/reset_password")
	@Consumes("application/json")
	@Produces("application/json")
	public Response resetPassword(@NotNull final String emailAndRequestIdAndPassword) {
		JsonObject json = Json.createReader(new StringReader(emailAndRequestIdAndPassword)).readObject();
		final String email = json.getString("email");
		final String requestId = json.getString("id");
		final String newPassword = json.getString("password");
		if (email == null || requestId == null || newPassword == null)
			return Response.status(Response.Status.BAD_REQUEST).build();
		List<ResetPasswordRequest> requests = service.findResetPasswordRequests(email);
		if (requests.size() > 0 && requests.stream().anyMatch(request -> request.getRequestId().equals(requestId))) {
			service.getUserByEmail(email).ifPresent(user -> service.updatePassword(user, newPassword));
			service.deletePasswordRequests(email);
			return Response.ok().build();
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	private Response userResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<User> userOptional) {
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			user.setPassword(null);
			return Response.ok().entity(user).build();
		} else return Response.status(Response.Status.NOT_FOUND).build();
	}
}
