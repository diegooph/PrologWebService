package br.com.zalf.prolog.webservice.commons.gson;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoConstants;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.Origem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloBanda;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloPneu;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.ModeloVeiculo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.ResponseIntervalo;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
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
				.setExclusionStrategies(new AnnotationExclusionStrategy())
				.enableComplexMapKeySerialization();

		if (BuildConfig.DEBUG) {
			builder.setPrettyPrinting();
		}

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


		RuntimeTypeAdapterFactory<Modelo> adapterModelo = RuntimeTypeAdapterFactory
				.of(Modelo.class, "tipo")
				.registerSubtype(ModeloPneu.class, ModeloPneu.TIPO_MODELO_PNEU)
				.registerSubtype(ModeloBanda.class, ModeloBanda.TIPO_MODELO_BANDA)
				.registerSubtype(ModeloVeiculo.class, ModeloVeiculo.TIPO_MODELO_VEICULO);

		RuntimeTypeAdapterFactory<Alternativa> adapterAlternativa = RuntimeTypeAdapterFactory
				.of(Alternativa.class)
				.registerSubtype(AlternativaEscolhaQuiz.class)
				.registerSubtype(AlternativaOrdenamentoQuiz.class)
				.registerSubtype(AlternativaChecklist.class)
				/* Como Alternativa não é abstrato e nós iremos instancia-la, a mesma foi adicionada como subtipo de si
				* própria. */
				.registerSubtype(Alternativa.class);

		RuntimeTypeAdapterFactory<AbstractResponse> adapterResponse = RuntimeTypeAdapterFactory
                .of(AbstractResponse.class)
                .registerSubtype(Response.class)
                .registerSubtype(ResponseWithCod.class)
				.registerSubtype(ResponseIntervalo.class);

		builder.registerTypeAdapterFactory(Servico.provideTypeAdapterFactory());
		builder.registerTypeAdapterFactory(adapterAlternativa);
		builder.registerTypeAdapterFactory(adapterResponse);
		builder.registerTypeAdapterFactory(adapterOrigem);
		builder.registerTypeAdapterFactory(adapterDestino);
		builder.registerTypeAdapterFactory(adapterModelo);
		builder.registerTypeAdapterFactory(QuantidadeServicos.provideTypeAdapterFactory());

		sGson = builder.create();
	}

	public static Gson getGson()  {
		return sGson;
	}
}