package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Contém os métodos que estão disponíveis para um {@link Sistema} utilizar através do {@link IntegradorProLog} caso
 * precise de informações extras para funcionar.
 */
public interface InformacoesProvidas {

    @NotNull
    Colaborador getColaboradorByToken(@NotNull final String userToken) throws Exception;

    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Exception;

    @NotNull
    Optional<DiagramaVeiculo> getDiagramaVeiculoByCodDiagrama(@NotNull final Short codDiagrama) throws Exception;

    @NotNull
    Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final String placaVeiculo) throws Exception;

    @NotNull
    String getCodUnidadeClienteByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Exception;
}