package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
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
    ConfiguracaoNovaAfericao getConfiguracaoNovaAfericao(@NotNull final String placa) throws Throwable;

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
    ApiAutenticacaoHolder getApiAutenticacaoHolder(@NotNull final Connection conn,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final SistemaKey sistemaKey,
                                                   @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    @NotNull
    List<Long> getCodUnidadesIntegracaoBloqueada(@NotNull final String userToken) throws Throwable;
}