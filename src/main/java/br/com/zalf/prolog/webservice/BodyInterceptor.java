package br.com.zalf.prolog.webservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import br.com.zalf.prolog.webservice.util.L;

@BodyGetter
@Provider
public class BodyInterceptor implements ContainerRequestFilter {

	private static final String TAG = BodyInterceptor.class.getSimpleName();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (isJson(requestContext)) {
			requestContext.getEntityStream();
			String json = convertStreamToString(requestContext.getEntityStream());
			L.d(TAG, json);
		}
	}

	private boolean isJson(ContainerRequestContext request) {
		// define rules when to read body
		return request.getMediaType().toString().contains("application/json"); 
	}

	private String convertStreamToString(java.io.InputStream is) {
		 byte[] bytes = toBytes(is);
	        String texto = null;
			try {
				texto = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return texto;
	}
	
	private byte[] toBytes(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            L.e(TAG, "Erro ao criar byte array a partir de InputStream", e);
            return null;
        } finally {
            try {
                bos.close();
                in.close();
            } catch (IOException e) {
                L.e(TAG, "IOException ao criar byte array a partir de InputStream", e);
            }
        }
    }
}