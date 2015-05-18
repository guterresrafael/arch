package rs.pelotas.arch.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

/**
 *
 * @author Rafael Guterres
 */
@Provider
public class SecurityInterceptor implements ContainerRequestFilter {

    private static final String RESOURCE_METHOD_INVOKER_CLASS = "org.jboss.resteasy.core.ResourceMethodInvoker";
    private static final ServerResponse HTTP_401_UNAUTHORIZED = new ServerResponse(null, 401, new Headers<>());
    private static final ServerResponse HTTP_403_FORBIDDEN = new ServerResponse(null, 403, new Headers<>());

    @Inject
    AuthenticationAndAuthorizationSecurity authSecurity;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty(RESOURCE_METHOD_INVOKER_CLASS);
        Method method = methodInvoker.getMethod();

        //@PermitAll
        if (!method.isAnnotationPresent(PermitAll.class)) {
        
            //@DenyAll
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(HTTP_403_FORBIDDEN);
                return;
            }
            
            //Authorization
            AuthorizationBasic authorization = new AuthorizationBasic(requestContext);
            if (authorization.getUsername() == null || authorization.getPassword() == null ||
                !isAuthenticatedUser(authorization)) {
                requestContext.abortWith(HTTP_401_UNAUTHORIZED);
                return;
            }
            
            //@RolesAllowed
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> roles = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
                if (!isAuthorizedUser(authorization, roles)) {
                    requestContext.abortWith(HTTP_403_FORBIDDEN);
                }
            }
            
            //Add Authorization to Context
            requestContext.setProperty(AuthorizationBasic.AUTHORIZATION_PROPERTY, authorization);
        }
    }
    
    private boolean isAuthenticatedUser(AuthorizationBasic authorization) {
        return authSecurity.isAuthenticatedUser(authorization);
    }
    
    private boolean isAuthorizedUser(AuthorizationBasic authorization, Set<String> roles) {
        return authSecurity.isAuthorizedUser(authorization, roles);
    }
}