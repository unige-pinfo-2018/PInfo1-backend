package ch.unihub.business.service;

/*
@Author: Giovanna Theo
 */

/*This class allows cross-domain requests. If it's not present, any attempt
  on calling a rest service from domain1 to domain2 will fail
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    private static final List<String> allowedOrigins = new ArrayList<>(Arrays.asList(
            "http://127.0.0.1:8080",
            "127.0.0.1:8080",
            "http://localhost:8080",
            "localhost:8080"
    ));
    private static final String defaultOrigin = "http://127.0.0.1:8080";

    private static final Logger logger = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        final String requestOrigin = requestContext.getHeaders().getFirst("Origin");
        logger.info("CORS filter triggered; origins = " + requestOrigin + ", request = "
                + requestContext.getUriInfo().getPath());
        final String acceptedOrigin = allowedOrigins.contains(requestOrigin) ? requestOrigin : defaultOrigin;
        cres.getHeaders().add("Access-Control-Allow-Origin", acceptedOrigin);
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}

