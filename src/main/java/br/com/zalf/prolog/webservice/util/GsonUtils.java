package br.com.zalf.prolog.webservice.util;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.OrigemDestinoConstants;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.destino.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.origem.Origem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.servico.Calibragem;
import br.com.zalf.prolog.webservice.frota.pneu.servico.Inspecao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.Servico;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;

public final class GsonUtils {

	private static final Gson sGson;

	private GsonUtils() {

	}

    static {
		GsonBuilder builder = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.serializeSpecialFloatingPointValues()
				.registerTypeAdapter(Duration.class, new DurationDeserializer())
				.registerTypeAdapter(Duration.class, new DurationSerializer())
				.enableComplexMapKeySerialization();

		if (BuildConfig.DEBUG) {
			builder.setPrettyPrinting();
		}

		RuntimeTypeAdapterFactory<Servico> adapterServico = RuntimeTypeAdapterFactory
				.of(Servico.class)
				.registerSubtype(Calibragem.class)
				.registerSubtype(Movimentacao.class)
				.registerSubtype(Inspecao.class);

		RuntimeTypeAdapterFactory<Origem> adapterOrigem = RuntimeTypeAdapterFactory
				.of(Origem.class, "tipo")
				.registerSubtype(OrigemEstoque.class, OrigemDestinoConstants.ESTOQUE)
				.registerSubtype(OrigemAnalise.class, OrigemDestinoConstants.ANALISE)
				.registerSubtype(OrigemVeiculo.class, OrigemDestinoConstants.VEICULO);

		RuntimeTypeAdapterFactory<Destino> adapterDestino = RuntimeTypeAdapterFactory
				.of(Destino.class, "tipo")
				.registerSubtype(DestinoDescarte.class, OrigemDestinoConstants.DESCARTE)
				.registerSubtype(DestinoAnalise.class, OrigemDestinoConstants.ANALISE)
				.registerSubtype(DestinoVeiculo.class, OrigemDestinoConstants.VEICULO)
				.registerSubtype(DestinoEstoque.class, OrigemDestinoConstants.ESTOQUE);

		RuntimeTypeAdapterFactory<Alternativa> adapterAlternativa = RuntimeTypeAdapterFactory
				.of(Alternativa.class)
				.registerSubtype(AlternativaEscolhaQuiz.class)
				.registerSubtype(AlternativaOrdenamentoQuiz.class)
				.registerSubtype(AlternativaChecklist.class)
				.registerSubtype(Alternativa.class);

		RuntimeTypeAdapterFactory<AbstractResponse> adapterResponse = RuntimeTypeAdapterFactory
                .of(AbstractResponse.class)
                .registerSubtype(Response.class)
                .registerSubtype(ResponseWithCod.class);

		builder.registerTypeAdapterFactory(adapterServico);
		builder.registerTypeAdapterFactory(adapterAlternativa);
		builder.registerTypeAdapterFactory(adapterResponse);
		builder.registerTypeAdapterFactory(adapterOrigem);
		builder.registerTypeAdapterFactory(adapterDestino);

		sGson = builder.create();
	}

	public static Gson getGson()  {
		return sGson;
	}
}