package br.com.zalf.prolog.webservice.util;

import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.network.ResponseWithCod;
import br.com.zalf.prolog.commons.questoes.Alternativa;
import br.com.zalf.prolog.frota.pneu.servico.Calibragem;
import br.com.zalf.prolog.frota.pneu.servico.Inspecao;
import br.com.zalf.prolog.frota.pneu.servico.Movimentacao;
import br.com.zalf.prolog.frota.pneu.servico.Servico;
import br.com.zalf.prolog.gente.quiz.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.gente.quiz.AlternativaOrdenamentoQuiz;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;

public class GsonUtils {

	private static final GsonBuilder sBuilder = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			.setPrettyPrinting()
			.serializeSpecialFloatingPointValues()
			.registerTypeAdapter(Duration.class, new DurationSerializer())
			.enableComplexMapKeySerialization();

    static {
		RuntimeTypeAdapterFactory<Servico> adapterServico = RuntimeTypeAdapterFactory
				.of(Servico.class)
				.registerSubtype(Calibragem.class)
				.registerSubtype(Movimentacao.class)
				.registerSubtype(Inspecao.class);

		RuntimeTypeAdapterFactory<Alternativa> adapterAlternativa = RuntimeTypeAdapterFactory
				.of(Alternativa.class)
				.registerSubtype(AlternativaEscolhaQuiz.class)
				.registerSubtype(AlternativaOrdenamentoQuiz.class)
				.registerSubtype(Alternativa.class);

		RuntimeTypeAdapterFactory<AbstractResponse> adapterResponse = RuntimeTypeAdapterFactory
                .of(AbstractResponse.class)
                .registerSubtype(Response.class)
                .registerSubtype(ResponseWithCod.class);

        sBuilder.registerTypeAdapterFactory(adapterServico);
		sBuilder.registerTypeAdapterFactory(adapterAlternativa);
        sBuilder.registerTypeAdapterFactory(adapterResponse);
    }


	public static Gson getGson()  {
		return sBuilder.create();
	}
}
