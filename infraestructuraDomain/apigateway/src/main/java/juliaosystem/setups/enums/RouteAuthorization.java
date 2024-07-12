package juliaosystem.setups.enums;

public enum RouteAuthorization {
    SUPER_ADMINDMINISTRADOR("ADMINISTRADOR", "*", new String[]{"*"}, "Acceso permitido"),
    USUARIO("USUARIO", "123", new String[]{"/usuarios/*"}, "Acceso permitido para usuarios");

    private final String role;
    private final String idBussines;
    private final String[] routePatterns;
    private final String successMessage;

    RouteAuthorization(String role, String idBussines, String[] routePatterns, String successMessage) {
        this.role = role;
        this.idBussines = idBussines;
        this.routePatterns = routePatterns;
        this.successMessage = successMessage;
    }

    public String getRole() {
        return role;
    }

    public String getIdBussines() {
        return idBussines;
    }

    public String[] getRoutePatterns() {
        return routePatterns;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public static RouteAuthorization getAuthorizationForRoute(String route) {
        for (RouteAuthorization authorization : values()) {
            for (String pattern : authorization.routePatterns) {
                if (route.matches(pattern)) {
                    return authorization;
                }
            }
        }
        return null;
    }
}

