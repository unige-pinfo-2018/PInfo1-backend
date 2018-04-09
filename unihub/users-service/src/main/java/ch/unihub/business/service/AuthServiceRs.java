package ch.unihub.business.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ManagedBean
@RequestScoped
@Path("/users")
public class AuthServiceRs {
    @Inject
    private UserService service;

    private Logger logger = LoggerFactory.getLogger(AuthServiceRs.class);

    @POST
    @Path("/login")
    @Produces({ "application/json" })
    public Response login(@NotNull final String username, @NotNull final String password) {
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
            logger.info(uae.getMessage());
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
        logger.info("User authenticated");
        return Response.ok(currentUser.getSession()).build();
    }

    @POST
    @Path("/logout")
    @Produces({ "application/json" })
    public Response logout() {
        SecurityUtils.getSubject().logout();
        return Response.ok().build();
    }
}
