package ch.unihub.business.service;

import ch.unihub.dom.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
/**
 * @author Arthur Deschamps
 */
@ManagedBean
@RequestScoped
@Path("/users")
public class AuthServiceRs {
    @Inject
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(AuthServiceRs.class);

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
            logger.error(ice.getMessage());
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
        logger.info("User authenticated");
        return Response.ok(currentUser.getSession()).build();
    }

    @POST
    @Path("/logout")
    @Produces("application/json")
    public Response logout() {
        SecurityUtils.getSubject().logout();
        return Response.ok().build();
    }

    @PUT
    @Path("/add")
    @Produces("application/json")
    @Consumes("application/json")
    public Response addUser(@NotNull final User user) throws URISyntaxException {
        final String password = user.getPassword();
        // Checks if the username is already taken
        if (userService.getUser(user.getUsername()).isPresent())
            return Response.status(Response.Status.CONFLICT).build();
        // If user doesn't exist, add it to the database
        userService.addUser(user, password);
        return Response
                .status(Response.Status.CREATED)
                .contentLocation(new URI("users/by_id/" + user.getId().toString()))
                .build();
    }
}
