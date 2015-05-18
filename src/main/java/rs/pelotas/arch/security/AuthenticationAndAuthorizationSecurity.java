package rs.pelotas.arch.security;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Rafael Guterres
 */
public interface AuthenticationAndAuthorizationSecurity extends Serializable {

    public boolean isAuthenticatedUser(AuthorizationBasic authorization);

    public boolean isAuthorizedUser(AuthorizationBasic authorization, Set<String> roles);
}
