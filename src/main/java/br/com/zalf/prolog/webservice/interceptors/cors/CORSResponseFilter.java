package br.com.zalf.prolog.webservice.interceptors.cors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
 
@Provider
public class CORSResponseFilter implements ContainerResponseFilter {
 
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
 
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
 
		headers.add("Access-Control-Allow-Origin", "*");
		//allows CORS requests only coming from podcastpedia.org
		//headers.add("Access-Control-Allow-Origin", "http://podcastpedia.org");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");			
		//headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");
	}
}