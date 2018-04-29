package ch.unihub.business.service;

import org.apache.shiro.SecurityUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/authenticator")
public class NotificationsAuthenticatorRs {

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
}
