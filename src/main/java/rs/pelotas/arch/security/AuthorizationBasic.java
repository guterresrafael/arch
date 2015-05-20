package rs.pelotas.arch.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import org.jboss.resteasy.util.Base64;

/**
 *
 * @author Rafael Guterres
 */
public class AuthorizationBasic implements Serializable {

    private static final long serialVersionUID = 5670883358002212562L;

    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    public static final String AUTHORIZATION_SCHEME = "Basic";
    public static final String AUTHORIZATION_REGEX = AUTHORIZATION_SCHEME + " ";
    public static final String AUTHORIZATION_REPLACEMENT = "";

    public static UserPrincipal getUserPrincipal(ContainerRequestContext requestContext) {
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        final String encodedUsernameAndPassword = authorization.get(0).replaceFirst(AUTHORIZATION_REGEX, AUTHORIZATION_REPLACEMENT);
        String usernameAndPassword;
        try {
            usernameAndPassword = new String(Base64.decode(encodedUsernameAndPassword));
        } catch (IOException e) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();
        Set<String> roles = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        return new UserPrincipal(username, password, roles);
    }
}