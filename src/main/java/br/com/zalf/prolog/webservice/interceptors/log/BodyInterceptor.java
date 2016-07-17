package br.com.zalf.prolog.webservice.interceptors.log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.LogDatabase;

@LogBody
@Provider
public class BodyInterceptor implements ContainerRequestFilter {

	private static final String TAG = BodyInterceptor.class.getSimpleName();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (isJson(requestContext)) {
			String json = IOUtils.toString(requestContext.getEntityStream(), StandardCharsets.UTF_8);
				
			L.d(TAG, json);
			LogDatabase.insertLog(json);
			
            InputStream in = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
            requestContext.setEntityStream(in);
		}
	}

	private boolean isJson(ContainerRequestContext request) {
		// define rules when to read body
		return request.getMediaType().toString().contains("application/json"); 
	}
}
