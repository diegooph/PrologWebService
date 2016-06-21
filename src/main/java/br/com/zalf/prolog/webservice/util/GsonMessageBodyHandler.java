package br.com.zalf.prolog.webservice.util;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.zalf.prolog.models.pneu.servico.Calibragem;
import br.com.zalf.prolog.models.pneu.servico.Inspecao;
import br.com.zalf.prolog.models.pneu.servico.Movimentacao;
import br.com.zalf.prolog.models.pneu.servico.Servico;

@Provider
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class GsonMessageBodyHandler implements MessageBodyWriter<Object>,
		MessageBodyReader<Object> {
	private static final String UTF_8 = "UTF-8";
	private Gson gson;

	RuntimeTypeAdapterFactory<Servico> adapter = RuntimeTypeAdapterFactory
            .of(Servico.class)
            .registerSubtype(Calibragem.class, Servico.TIPO_CALIBRAGEM)
            .registerSubtype(Movimentacao.class, Servico.TIPO_MOVIMENTACAO)
            .registerSubtype(Inspecao.class, Servico.TIPO_INSPECAO);
	
	private Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder()
	                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
	                .setPrettyPrinting()
	                .serializeSpecialFloatingPointValues()
	                .enableComplexMapKeySerialization()
	                .registerTypeAdapterFactory(adapter)
	                .create();
		}
		return gson;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException {
		InputStreamReader streamReader = new InputStreamReader(entityStream,
				UTF_8);
		try {
			Type jsonType;
			if (type.equals(genericType)) {
				jsonType = type;
			} else {
				jsonType = genericType;
			}
			return getGson().fromJson(streamReader, jsonType);
		} finally {
			streamReader.close();
		}
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(Object object, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Object object, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
		try {
			Type jsonType;
			if (type.equals(genericType)) {
				jsonType = type;
			} else {
				jsonType = genericType;
			}
			getGson().toJson(object, jsonType, writer);
		} finally {
			writer.close();
		}
	}
}
