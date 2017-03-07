package br.com.zalf.prolog.webservice.util;

import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.network.ResponseWithCod;
import br.com.zalf.prolog.commons.questoes.Alternativa;
import br.com.zalf.prolog.frota.checklist.AlternativaChecklist;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoConstants;
import br.com.zalf.prolog.frota.pneu.movimentacao.destino.*;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.Origem;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.OrigemEstoque;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.OrigemRecapagem;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.OrigemVeiculo;
import br.com.zalf.prolog.frota.pneu.servico.Calibragem;
import br.com.zalf.prolog.frota.pneu.servico.Inspecao;
import br.com.zalf.prolog.frota.pneu.servico.Movimentacao;
import br.com.zalf.prolog.frota.pneu.servico.Servico;
import br.com.zalf.prolog.gente.quiz.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.gente.quiz.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;

public class GsonUtils {

	private static final GsonBuilder sBuilder;

    static {

		sBuilder = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.serializeSpecialFloatingPointValues()
				.registerTypeAdapter(Duration.class, new DurationSerializer())
				.enableComplexMapKeySerialization();

		if (BuildConfig.DEBUG) {
			sBuilder.setPrettyPrinting();
		}

		RuntimeTypeAdapterFactory<Servico> adapterServico = RuntimeTypeAdapterFactory
				.of(Servico.class)
				.registerSubtype(Calibragem.class)
				.registerSubtype(Movimentacao.class)
				.registerSubtype(Inspecao.class);

		RuntimeTypeAdapterFactory<Origem> adapterOrigem = RuntimeTypeAdapterFactory
				.of(Origem.class, "tipo")
				.registerSubtype(OrigemEstoque.class, OrigemDestinoConstants.ESTOQUE)
				.registerSubtype(OrigemRecapagem.class, OrigemDestinoConstants.RECAPAGEM)
				.registerSubtype(OrigemVeiculo.class, OrigemDestinoConstants.VEICULO);

		RuntimeTypeAdapterFactory<Destino> adapterDestino = RuntimeTypeAdapterFactory
				.of(Destino.class, "tipo")
				.registerSubtype(DestinoDescarte.class, OrigemDestinoConstants.DESCARTE)
				.registerSubtype(DestinoRecapagem.class, OrigemDestinoConstants.RECAPAGEM)
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

        sBuilder.registerTypeAdapterFactory(adapterServico);
		sBuilder.registerTypeAdapterFactory(adapterAlternativa);
        sBuilder.registerTypeAdapterFactory(adapterResponse);
        sBuilder.registerTypeAdapterFactory(adapterOrigem);
//		sBuilder.registerTypeAdapterFactory(adapterDestino);
	}


	public static Gson getGson()  {
		return sBuilder.create();
	}
}
