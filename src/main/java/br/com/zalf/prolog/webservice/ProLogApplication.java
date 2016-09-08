package br.com.zalf.prolog.webservice;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProLogApplication extends Application {
	
	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();
		// Suporte ao File Upload.
		singletons.add(new MultiPartFeature());
		return singletons;
	}
	
	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<>();
		// Configura o pacote para fazer scan das classes com anotações REST.
		// FIXME: Se mudar o nome do pacote do projeto além de alterar aqui, alterar
		// no arquivo web.xml também
		properties.put("jersey.config.server.provider.packages", 
						"br.com.zalf.prolog.webservice");
		return properties;
	}
}
