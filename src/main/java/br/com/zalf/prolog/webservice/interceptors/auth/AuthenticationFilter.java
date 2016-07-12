package br.com.zalf.prolog.webservice.interceptors.auth;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

    	System.out.println("Chamado");
        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        System.out.println("authorizationHeader: " + authorizationHeader);
        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            // Validate the token
            validateToken(token);
        } catch (Exception e) {
        	e.printStackTrace();
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(String token) throws Exception {
        // Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    	System.out.println("Token: " + token);
    	if (!new AutenticacaoService().verifyIfTokenExists(token))
    		throw new NotAuthorizedException("Token inválido");
    }
}