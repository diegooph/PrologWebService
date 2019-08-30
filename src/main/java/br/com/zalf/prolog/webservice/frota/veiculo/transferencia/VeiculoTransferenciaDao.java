package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoSelecaoTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
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
     * @param dadosChecklistOfflineChangedListener Listener para informarmos quando os veículos forem transferidos
     *                                             assim a versão dos dados será incrementada na unidade de origem e
     *                                             destino. Desde que as unidades já possuam uma versão criada,
     *                                             do contrário, nada será feito.
     * @return Código do processo de transferência que foi inserido.
     * @throws Throwable Se algum erro ocorrer ao realizar o processo de transferência.
     */
    @NotNull
    Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable;

    /**
     * Busca os veículos da unidade de origem da transferência que estão aptos a seleção para serem transferidos.
     *
     * @param codUnidadeOrigem Código da unidade de origem da transferência, para a qual serão buscados os veículos.
     * @return Uma lista de {@link VeiculoSelecaoTransferencia veículos} disponíveis na unidade de origem para serem
     * transferidos.
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<VeiculoSelecaoTransferencia> getVeiculosParaSelecaoTransferencia(
            @NotNull final Long codUnidadeOrigem) throws Throwable;

    /**
     * Método utilizado para listar os processos de transferência realizados.
     * <p>
     * Esse método possibilita a filtragem por várias Origens e Destinos. Com os parâmetros é possível filtrar processos
     * realizados da unidade A para B e A para C, por exemplo. Como também é possível filtrar da unidade C para a A e da
     * B para a A.
     * <p>
     * Somente serão buscados processos que tiverem a Unidade de Origem e a Unidade de Destino selecionadas na filtragem
     * e, é claro, que foram realizados dentro do período filtrado.
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

    /**
     * Método utilizado buscar um {@link ProcessoTransferenciaVeiculoVisualizacao processo de transferência} de veículo.
     *
     * @param codProcessoTransferencia Código do processo de transferência.
     * @return Um {@link ProcessoTransferenciaVeiculoVisualizacao processo de transferência}.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao(
            @NotNull final Long codProcessoTransferencia) throws Throwable;

    /**
     * Método utilizado para buscar os {@link DetalhesVeiculoTransferido detalhes} de uma placa transferida.
     *
     * @param codProcessoTransferencia Código do processo de transferência.
     * @param codVeiculo               Código do veículo que será buscado.
     * @return {@link DetalhesVeiculoTransferido Detalhes} de uma placa transferida.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    DetalhesVeiculoTransferido getDetalhesVeiculoTransferido(@NotNull final Long codProcessoTransferencia,
                                                             @NotNull final Long codVeiculo) throws Throwable;

    @NotNull
    boolean verificaFluxoTransferencia(@NotNull final Long codEmpresa) throws Throwable;
}
