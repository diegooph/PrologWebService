package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.IntegracaoOsFilter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ModelosChecklistBloqueados;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeDeParaHolder;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeRestricaoHolder;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

/**
 * DAO que conterá todos os métodos necessários para que as integrações funcionem.
 * Essa DAO <b>NÃO DEVE</b> possuir métodos que servem para uma ou outra integração com empresas específicas.
 * A ideia é que ela possua métodos que o ProLog utiliza para fazer as integrações (no geral) funcionarem do seu lado.
 */
public interface IntegracaoDao {

    @Nullable
    SistemaKey getSistemaKey(@NotNull final String userToken,
                             @NotNull final RecursoIntegrado recursoIntegrado) throws Exception;

    @NotNull
    String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable;

    @NotNull
    Long getCodEmpresaByTokenIntegracao(@NotNull final Connection conn,
                                        @NotNull final String tokenIntegracao) throws Throwable;

    @NotNull
    Long getCodEmpresaByCodUnidadeProLog(@NotNull final Connection conn,
                                         @NotNull final Long codUnidadeProLog) throws Throwable;

    @NotNull
    String getUrl(@NotNull final Long codEmpresa,
                  @NotNull final SistemaKey sistemaKey,
                  @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    String getUrl(@NotNull final Connection conn,
                  @NotNull final Long codEmpresa,
                  @NotNull final SistemaKey sistemaKey,
                  @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    String getCodAuxiliarByCodUnidadeProlog(@NotNull final Connection conn,
                                            @NotNull final Long codUnidadeProlog) throws Throwable;

    @NotNull
    UnidadeDeParaHolder getCodAuxiliarByCodUnidadeProlog(@NotNull final Connection conn,
                                                         @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    UnidadeRestricaoHolder getUnidadeRestricaoHolder(@NotNull final Connection conn,
                                                     @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    ApiAutenticacaoHolder getApiAutenticacaoHolder(@NotNull final Long codEmpresa,
                                                   @NotNull final SistemaKey sistemaKey,
                                                   @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    ApiAutenticacaoHolder getApiAutenticacaoHolder(@NotNull final Connection conn,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final SistemaKey sistemaKey,
                                                   @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    List<Long> getCodUnidadesIntegracaoBloqueada(@NotNull final String userToken,
                                                 @NotNull final SistemaKey sistemaKey,
                                                 @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable;

    @NotNull
    List<Long> getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
            @NotNull final String tokenIntegracao,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable;

    boolean getConfigAberturaServicoPneuIntegracao(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    ModelosChecklistBloqueados getModelosChecklistBloqueados(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    Long insertOsPendente(@NotNull final Long codUnidade, @NotNull final Long codOs) throws Throwable;

    @NotNull
    List<OsIntegracao> getOrdensServicosIntegracaoByCod(
            @NotNull final List<Long> codsOrdensServicos,
            @NotNull final IntegracaoOsFilter integracaoOsFilter) throws Throwable;

    @NotNull
    List<Long> buscaCodOrdensServicoPendenteSincronizacao() throws Throwable;

    void atualizaStatusOsIntegrada(@NotNull final List<Long> codsInternoOsProlog,
                                   final boolean pendente,
                                   final boolean bloqueada,
                                   final boolean incrementarTentativas) throws Throwable;

    void logarStatusOsComErro(@NotNull final Long codInternoOsProlog,
                              @NotNull final Throwable throwable) throws Throwable;

    @NotNull
    List<Long> buscaCodOsByCodItem(@NotNull final List<Long> codItensProlog) throws Throwable;
}