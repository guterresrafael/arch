package rs.pelotas.arch.security;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Rafael Guterres
 */
public interface AuthenticationAndAuthorizationSecurity extends Serializable {
    
    public boolean isAuthenticatedUser(String login, String password);
    
    public boolean isAuthorizedUser(String login, Set<String> roles);
}
