package br.com.zalf.prolog.webservice.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.zalf.prolog.models.pneu.servico.Calibragem;
import br.com.zalf.prolog.models.pneu.servico.Inspecao;
import br.com.zalf.prolog.models.pneu.servico.Movimentacao;
import br.com.zalf.prolog.models.pneu.servico.Servico;

public class GsonUtils {

	public static Gson getGson(){

		RuntimeTypeAdapterFactory<Servico> adapter = RuntimeTypeAdapterFactory
				.of(Servico.class)
				.registerSubtype(Calibragem.class)
				.registerSubtype(Movimentacao.class)
				.registerSubtype(Inspecao.class);

		Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
					.setPrettyPrinting()
					.serializeSpecialFloatingPointValues()
					.enableComplexMapKeySerialization()
					.registerTypeAdapterFactory(adapter)
					.create();

		return gson;
	}

}
