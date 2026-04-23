//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api.filters;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(ApiLoggingFilter.class.getName());
    // This filter logs incoming HTTP requests and outgoing HTTP responses, including the method, URL, and response status code for better visibility into API usage and debugging.
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        String method = requestContext.getMethod();
        String url = uriInfo == null ? "" : uriInfo.getRequestUri().toString();
        LOGGER.info(() -> "REQUEST " + method + " " + url);
    }
    // The filter method for outgoing responses logs the HTTP status code, method, and URL of the request that generated the response, providing insight into the API's behavior and performance.
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        String method = requestContext.getMethod();
        String url = uriInfo == null ? "" : uriInfo.getRequestUri().toString();
        int status = responseContext.getStatus();
        LOGGER.info(() -> "RESPONSE " + status + " " + method + " " + url);
    }
}
