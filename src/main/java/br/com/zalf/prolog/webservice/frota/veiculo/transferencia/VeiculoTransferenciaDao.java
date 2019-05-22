package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface VeiculoTransferenciaDao {

    /**
     * Este método realiza a transferência de placas, e pneus aplicados, entre unidades de uma mesma empresa.
     * <p>
     * Um {@link ProcessoTransferenciaVeiculoRealizacao processo de transferência} pode conter a transferência de várias
     * placas de uma unidade de origem para outra unidade de destino. Origem e destino não podem ser iguais.
     * Não é possível transferir placas de diferentes unidades para um único destino. O processo deve incluir sempre,
     * somente duas unidades, a origem e a destino.
     * <p>
     * O processo de transferência de placas também transfere os pneus que estão aplicados na placa.
     *
     * @param processoTransferenciaVeiculo Objeto que contém as placas que serão transferidas.
     * @return Código do processo de transferência que foi inserido.
     * @throws Throwable Se algum erro ocorrer ao realizar o processo de transferência.
     */
    @NotNull
    Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws Throwable;

    /**
     * Método utilizado para listar os processos de transferência realizados.
     * <p>
     * Este método possibilita a filtragem por várias Origens e Destinos. Com os parâmetros é possível filtrar processos
     * realizados da unidade A para B e A para C, por exemplo. Como também é possível filtrar da unidade C para a A e da
     * B para a A.
     * <p>
     * Somente serão buscados processos que tiverem a Unidade de Origem e a Unidade de Destino selecionadas na filtragem
     * e é claro, foram realizados dentro do período filtrado.
     *
     * @param codUnidadesOrigem  Lista de códigos das unidades de Origem dos processos de transferência.
     * @param codUnidadesDestino Lista de códigos das unidades de Destino dos processos de transferência.
     * @param dataInicial        Data inicial do filtro de processos.
     * @param dataFinal          Data final do filtro de processos.
     * @return Uma lista de {@link ProcessoTransferenciaVeiculoListagem processos de transferência} que foram realizados
     * dentro dos parâmetros filtrados.
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<ProcessoTransferenciaVeiculoListagem> getProcessosTransferenciaVeiculoListagem(
            @NotNull final List<Long> codUnidadesOrigem,
            @NotNull final List<Long> codUnidadesDestino,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao(
            @NotNull final Long codProcessoTransferencia) throws Throwable;

}
