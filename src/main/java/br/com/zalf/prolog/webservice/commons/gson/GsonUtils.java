package br.com.zalf.prolog.webservice.commons.gson;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ResponseImagemChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.ChecklistOfflineSupport;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.motivo.Motivo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.motivo.MotivoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.Origem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu._model.ModeloBanda;
import br.com.zalf.prolog.webservice.frota.pneu._model.ModeloPneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.QuantidadeServicos;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.ModeloVeiculo;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.MarcacaoInconsistencia;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.ResponseIntervalo;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .setLenient()
                .enableComplexMapKeySerialization();

        if (BuildConfig.DEBUG) {
            builder.setPrettyPrinting();
        }

        RuntimeTypeAdapterFactory<Origem> adapterOrigem = RuntimeTypeAdapterFactory
                .of(Origem.class, "tipo")
                .registerSubtype(OrigemEstoque.class, OrigemDestinoEnum.ESTOQUE.asString())
                .registerSubtype(OrigemAnalise.class, OrigemDestinoEnum.ANALISE.asString())
                .registerSubtype(OrigemVeiculo.class, OrigemDestinoEnum.VEICULO.asString());

        RuntimeTypeAdapterFactory<Destino> adapterDestino = RuntimeTypeAdapterFactory
                .of(Destino.class, "tipo")
                .registerSubtype(DestinoDescarte.class, OrigemDestinoEnum.DESCARTE.asString())
                .registerSubtype(DestinoAnalise.class, OrigemDestinoEnum.ANALISE.asString())
                .registerSubtype(DestinoVeiculo.class, OrigemDestinoEnum.VEICULO.asString())
                .registerSubtype(DestinoEstoque.class, OrigemDestinoEnum.ESTOQUE.asString());

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
                .registerSubtype(ResponseIntervalo.class)
                .registerSubtype(ResponseImagemChecklist.class);

        RuntimeTypeAdapterFactory<Motivo> adapterMotivo = RuntimeTypeAdapterFactory
                .of(Motivo.class, "tipo")
                .registerSubtype(MotivoDescarte.class, MotivoDescarte.TIPO_MOTIVO_DESCARTE);

        builder.registerTypeAdapterFactory(Servico.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(adapterAlternativa);
        builder.registerTypeAdapterFactory(adapterResponse);
        builder.registerTypeAdapterFactory(adapterOrigem);
        builder.registerTypeAdapterFactory(adapterDestino);
        builder.registerTypeAdapterFactory(adapterModelo);
        builder.registerTypeAdapterFactory(adapterMotivo);
        builder.registerTypeAdapterFactory(Pneu.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(QuantidadeServicos.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(PneuServicoRealizado.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(RaizenProdutividade.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(NovaAfericao.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(Afericao.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(RaizenProdutividadeItem.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(MarcacaoAjuste.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(MarcacaoInconsistencia.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(OrdemServicoListagem.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(OrdemServicoVisualizacao.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(ItemOrdemServicoVisualizacao.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(PerguntaModeloChecklist.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(AlternativaModeloChecklist.provideTypeAdapterFactory());
        builder.registerTypeAdapterFactory(ChecklistOfflineSupport.provideTypeAdapterFactory());

        builder.registerTypeAdapterFactory(ApiPneuAlteracaoStatus.provideTypeAdapterFactory());

        sGson = builder.create();
    }

    public static Gson getGson() {
        return sGson;
    }
}