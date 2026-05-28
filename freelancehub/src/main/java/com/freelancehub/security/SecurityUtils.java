package com.freelancehub.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }
        return (UserPrincipal) auth.getPrincipal();
    }

    public static UserPrincipal getCurrentUserOrThrow() {
        UserPrincipal principal = getCurrentUser();
        if (principal == null) {
            throw new IllegalStateException("No hay usuario autenticado en el contexto de seguridad");
        }
        return principal;
    }

    public static Long getCurrentUserId() {
        UserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getId() : null;
    }

    public static Long getCurrentUserIdOrThrow() {
        return getCurrentUserOrThrow().getId();
    }
}
