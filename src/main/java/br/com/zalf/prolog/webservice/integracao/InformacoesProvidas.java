package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Contém os métodos que estão disponíveis para um {@link Sistema} utilizar através do {@link IntegradorProLog} caso
 * precise de informações extras para funcionar.
 */
public interface InformacoesProvidas {
    @NotNull
    Colaborador getColaboradorByToken(@NotNull final String userToken) throws Exception;

    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    Optional<DiagramaVeiculo> getDiagramaVeiculoByCodDiagrama(@NotNull final Short codDiagrama) throws Exception;

    @NotNull
    String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable;

    @NotNull
    Long getCodEmpresaByCodUnidadeProLog(@NotNull final Connection conn,
                                         @NotNull final Long codUnidadeProLog) throws Throwable;

    @NotNull
    String getUrl(@NotNull final Connection conn,
                  @NotNull final Long codEmpresa,
                  @NotNull final SistemaKey sistemaKey,
                  @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    String getCodAuxiliarByCodUnidadeProlog(@NotNull final Connection conn,
                                            @NotNull final Long codUnidadeProlog) throws Throwable;

    @NotNull
    ApiAutenticacaoHolder getApiAutenticacaoHolder(@NotNull final Connection conn,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final SistemaKey sistemaKey,
                                                   @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    List<Long> getCodUnidadesIntegracaoBloqueada(@NotNull final String userToken,
                                                 @NotNull final SistemaKey sistemaKey,
                                                 @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable;

    boolean getConfigAberturaServicoPneuIntegracao(@NotNull final Long codUnidade) throws Throwable;
}