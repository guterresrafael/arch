package rs.pelotas.arch.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.util.Base64;

/**
 *
 * @author Rafael Guterres
 */
@Provider
public abstract class SecurityInterceptor implements ContainerRequestFilter {

    private static final String RESOURCE_METHOD_INVOKER_CLASS = "org.jboss.resteasy.core.ResourceMethodInvoker";
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final ServerResponse HTTP_401_UNAUTHORIZED = new ServerResponse(null, 401, new Headers<>());
    private static final ServerResponse HTTP_403_FORBIDDEN = new ServerResponse(null, 403, new Headers<>());
    private static final ServerResponse HTTP_500_INTERNAL_SERVER_ERROR = new ServerResponse(null, 500, new Headers<>());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty(RESOURCE_METHOD_INVOKER_CLASS);
        Method method = methodInvoker.getMethod();

        //Access allowed for all
        if (!method.isAnnotationPresent(PermitAll.class)) {
        
            //Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(HTTP_403_FORBIDDEN);
                return;
            }

            //Get request headers and fetch authorization header.
            //If no authorization information present; block access
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(HTTP_401_UNAUTHORIZED);
                return;
            }

            //Get encoded username and password
            //Decode username and password
            //Split username and password tokens
            final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
            String usernameAndPassword;
            try {
                usernameAndPassword = new String(Base64.decode(encodedUserPassword));
            } catch (IOException e) {
                requestContext.abortWith(HTTP_500_INTERNAL_SERVER_ERROR);
                return;
            }
            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            //Verify user password
            if (!isValidPassword(username, password)) {
                requestContext.abortWith(HTTP_401_UNAUTHORIZED);
                return;
            }
            
            //Verify user access
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> roles = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
                
                //Is user allowed?
                if (!isAllowedUser(username, roles)) {
                    requestContext.abortWith(HTTP_403_FORBIDDEN);
                }
            }
        }
    }

    public abstract boolean isValidPassword(final String username, final String password);
    
    public abstract boolean isAllowedUser(final String username, final Set<String> roles);
}