package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface AfericaoDao {

    /**
     * Insere uma aferição lincada com o código da unidade.
     *
     * @param afericao uma aferição
     * @param codUnidade código da unidade
     * @return valor da operação
     * @throws Throwable se ocorrer erro no banco
     */
    boolean insert(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Throwable;

    /**
     * Atualiza uma aferição
     *
     * @param afericaoPlaca      objeto {@link AfericaoPlaca} para ser atualizado
     * @return              verdadeiro se operação for sucesso, falso caso contrário
     * @throws SQLException se ocorrer erro no banco
     */
    boolean update(AfericaoPlaca afericaoPlaca) throws SQLException;

    /**
     * Busca objeto contendo informações necessárias para se iniciar uma aferição do {@link Veiculo}.
     *
     * @param placa placa do veículo
     * @param tipoAfericao tipo da aferição que será realizada
     * @return retorna o objeto da nova aferição
     * @throws SQLException se ocorrer erro na busca
     */
    @NotNull
    NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                           @NotNull final String placa,
                                           @NotNull final String tipoAfericao) throws SQLException;

    /**
     * Busca objeto contendo informações necessárias para se iniciar uma aferição do {@link Pneu}.
     *
     * @param codPneu placa do veículo
     * @param tipoMedicaoColetadaAfericao tipo da aferição que será realizada
     * @return retorna o objeto da nova aferição
     * @throws SQLException se ocorrer erro na busca
     */
    @NotNull
    NovaAfericaoAvulsa getNovaAfericaoAvulsa(@NotNull final Long codUnidade,
                                             @NotNull final Long codPneu,
                                             @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable;

    /**
     * retorna as restrições de medidas da unidade
     *
     * @param codUnidade código da unidade
     * @return a restrição da unidade
     * @throws SQLException se ocorrer erro no banco
     */
    Restricao getRestricaoByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * retorna as restrições de medidas da placa
     *
     * @param placa placa do veículo
     * @return a restrição da placa
     * @throws SQLException se ocorrer erro no banco
     */
    Restricao getRestricoesByPlaca(String placa) throws SQLException;

    /**
     * retorna a lista de placas da unidade e também a meta de
     * dias em que cada placa deve ser aferido
     *
     * @param codUnidade    código da unidade
     * @return              um {@link CronogramaAfericao} contendo as placas para ser aferidas
     * @throws SQLException para qualquer erro do banco
     */
    CronogramaAfericao getCronogramaAfericao(Long codUnidade) throws SQLException;

    List<AfericaoPlaca> getAfericoes(Long codUnidade, String codTipoVeiculo, String placaVeiculo, long dataInicial,
                                     long dataFinal, int limit, long offset) throws SQLException;

    /**
     * retorna uma aferição através do código dela
     *
     * @param codUnidade  código da unidade
     * @param codAfericao código da aferição
     * @return a aferição
     * @throws SQLException se ocorrer erro no banco
     */
    AfericaoPlaca getByCod(Long codUnidade, Long codAfericao) throws SQLException;

    /**
     * pega a lista de aferições executadas
     *
     * @param codUnidades código da unidade
     * @param placas      placa do veículo
     * @param limit       limite de busca de dados no banco
     * @param offset      offset de busca no banco de dados
     * @return uma lista de aferições
     * @throws SQLException se ocorrer erro no banco
     */
    @Deprecated
    List<AfericaoPlaca> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, int limit, long offset) throws SQLException;
}
