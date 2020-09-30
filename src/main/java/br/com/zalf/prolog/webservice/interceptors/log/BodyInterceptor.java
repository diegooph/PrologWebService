package br.com.zalf.prolog.webservice.interceptors.log;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.LogDatabase;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@LogBody
@Provider
public class BodyInterceptor implements ContainerRequestFilter {
	private static final String TAG = BodyInterceptor.class.getSimpleName();

	@Override
	public void filter(final ContainerRequestContext request) throws IOException {
		if (isJson(request)) {
			final String json = IOUtils.toString(request.getEntityStream(), StandardCharsets.UTF_8);

			Log.d(TAG, json);
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() -> LogDatabase.insertLog(
					json,
					request.getMethod() + " - " + request.getUriInfo().getPath()));
			executor.shutdown();

			final InputStream in = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
			request.setEntityStream(in);
		}
	}

	private boolean isJson(final ContainerRequestContext request) {
		// define rules when to read body
		return request.getMediaType().toString().contains("application/json");
	}
}
