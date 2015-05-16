package rs.pelotas.arch.security;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import org.jboss.resteasy.util.Base64;

/**
 *
 * @author Rafael Guterres
 */
public class AuthorizationBasic {

    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    public static final String AUTHORIZATION_SCHEME = "Basic";
    public static final String AUTHORIZATION_REGEX = AUTHORIZATION_SCHEME + " ";
    public static final String AUTHORIZATION_REPLACEMENT = "";

    private String username;
    private String password;

    public AuthorizationBasic(ContainerRequestContext requestContext) {
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
        if (authorization == null || authorization.isEmpty()) {
            return;
        }
        final String encodedUsernameAndPassword = authorization.get(0).replaceFirst(AUTHORIZATION_REGEX, AUTHORIZATION_REPLACEMENT);
        String usernameAndPassword;
        try {
            usernameAndPassword = new String(Base64.decode(encodedUsernameAndPassword));
        } catch (IOException e) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        this.username = tokenizer.nextToken();
        this.password = tokenizer.nextToken();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}