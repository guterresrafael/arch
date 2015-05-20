package rs.pelotas.arch.security;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Rafael Guterres
 */
public interface SecurityService extends Serializable {

    public boolean isAuthenticatedUser(UserPrincipal userPrincipal);

    public boolean isAuthorizedUser(UserPrincipal userPrincipal, Set<String> roles);
}
