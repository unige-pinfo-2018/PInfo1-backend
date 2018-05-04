package ch.unihub.business.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.UUID;

@Path("/authenticator")
public class NotificationsAuthenticatorRs {

    private static Logger logger = LoggerFactory.getLogger(NotificationsAuthenticatorRs.class);

    @GET
    @Path("/getSessionId")
    @Produces("application/json")
    public Response getWebsocketSessionId() {
        final String sessionId = UUID.randomUUID().toString();
        final String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        // Removes potentially already existing ids
        NotificationsService.usernamesWithSessionIds.remove(username);
        // Registers the session id
        NotificationsService.usernamesWithSessionIds.put(
                username,
                sessionId
        );
        return Response.ok(sessionId).build();
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
}
