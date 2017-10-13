package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;

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
    DiagramaVeiculo getDiagramaVeiculoByCodDiagrama(@NotNull final Long codDiagrama) throws Exception;

    @NotNull
    DiagramaVeiculo getDiagramaVeiculoByPlaca(@NotNull final String placaVeiculo) throws Exception;

    @NotNull
    String getCodUnidadeClienteByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Exception;
}