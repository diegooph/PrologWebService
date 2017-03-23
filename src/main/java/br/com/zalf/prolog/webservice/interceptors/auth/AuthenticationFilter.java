package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.util.L;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String TAG = AuthenticationFilter.class.getSimpleName();

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        L.d(TAG, "authorizationHeader: " + authorizationHeader);
        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        
        String token = authorizationHeader.substring("Bearer".length()).trim();

        Method resourceMethod = resourceInfo.getResourceMethod();
        Secured methodAnnot = resourceMethod.getAnnotation(Secured.class);
        if (methodAnnot != null) {
           validateToken(token, methodAnnot.permissions(), methodAnnot.needsToHaveAll());
        }

        Class<?> resourceClass = resourceInfo.getResourceClass();
        Secured classAnnot = resourceClass.getAnnotation(Secured.class);
        if (classAnnot != null) {
            validateToken(token, classAnnot.permissions(), classAnnot.needsToHaveAll());
        }
    }

    private void validateToken(String token, int[] permissions, boolean needsToHaveAll) throws NotAuthorizedException {
        AutenticacaoService service = new AutenticacaoService();

        // Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    	L.d(TAG, "Token: " + token);
    	if (permissions.length == 0) {
    	    if (!service.verifyIfTokenExists(token))
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        } else {
            if (!service.userHasPermission(token, permissions, needsToHaveAll))
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        }
    }
}