package com.sanketika.course_backend.services;

// import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    /**
     * Get user's email from Keycloak token
     */
    public String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }

        return null;
    }

    /**
     * Get user's role from Keycloak token (realm roles)
     */
    public String getCurrentUserRole() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            var roles = jwt.getClaimAsMap("realm_access");
            if (roles != null && roles.containsKey("roles")) {
                var list = (java.util.List<String>) roles.get("roles");

                if (!list.isEmpty()) {
                    return list.get(0);  // first role
                }
            }
        }

        return null;
    }
}
