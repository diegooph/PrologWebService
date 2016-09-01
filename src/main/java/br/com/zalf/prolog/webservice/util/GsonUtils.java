package br.com.zalf.prolog.webservice.util;

import br.com.zalf.prolog.webservice.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.zalf.prolog.models.AbstractResponse;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.ResponseWithCod;
import br.com.zalf.prolog.models.pneu.servico.Calibragem;
import br.com.zalf.prolog.models.pneu.servico.Inspecao;
import br.com.zalf.prolog.models.pneu.servico.Movimentacao;
import br.com.zalf.prolog.models.pneu.servico.Servico;

public class GsonUtils {

	private static final GsonBuilder sBuilder;

    static {
    	sBuilder = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.serializeSpecialFloatingPointValues()
				.enableComplexMapKeySerialization();

		if (BuildConfig.DEBUG)
			sBuilder.setPrettyPrinting();

        RuntimeTypeAdapterFactory<Servico> adapterServico = RuntimeTypeAdapterFactory
                .of(Servico.class)
                .registerSubtype(Calibragem.class)
                .registerSubtype(Movimentacao.class)
                .registerSubtype(Inspecao.class);

        RuntimeTypeAdapterFactory<AbstractResponse> adapterResponse = RuntimeTypeAdapterFactory
                .of(AbstractResponse.class)
                .registerSubtype(Response.class)
                .registerSubtype(ResponseWithCod.class);

        sBuilder.registerTypeAdapterFactory(adapterServico);
        sBuilder.registerTypeAdapterFactory(adapterResponse);
    }


	public static Gson getGson()  {
		return sBuilder.create();
	}
}
