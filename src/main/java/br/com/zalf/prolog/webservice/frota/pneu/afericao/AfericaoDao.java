package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AfericaoDao {

    /**
     * Insere uma aferição lincada com o código da unidade.
     *
     * @param afericao   uma aferição
     * @param codUnidade código da unidade
     * @return valor da operação
     * @throws Throwable se ocorrer erro no banco
     */
    boolean insert(@NotNull final Afericao afericao, @NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca objeto contendo informações necessárias para se iniciar uma aferição do {@link Veiculo}.
     *
     * @param placa        placa do veículo
     * @param tipoAfericao tipo da aferição que será realizada
     * @return retorna o objeto da nova aferição
     * @throws SQLException se ocorrer erro na busca
     */
    @NotNull
    NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                           @NotNull final String placa,
                                           @NotNull final String tipoAfericao) throws Throwable;

    /**
     * Busca objeto contendo informações necessárias para se iniciar uma aferição do {@link Pneu}.
     *
     * @param codPneu                     placa do veículo
     * @param tipoMedicaoColetadaAfericao tipo da aferição que será realizada
     * @return retorna o objeto da nova aferição
     * @throws SQLException se ocorrer erro na busca
     */
    @NotNull
    NovaAfericaoAvulsa getNovaAfericaoAvulsa(@NotNull final Long codUnidade,
                                             @NotNull final Long codPneu,
                                             @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao)
            throws Throwable;

    /**
     * Retorna as restrições de medidas da unidade.
     *
     * @param codUnidade código da unidade
     * @return a restrição da unidade
     * @throws SQLException se ocorrer erro no banco
     */
    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Retorna as restrições de medidas da unidade.
     *
     * @param codUnidade código da unidade
     * @return a restrição da unidade
     * @throws SQLException se ocorrer erro no banco
     */
    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Connection conn, @NotNull final Long codUnidade) throws
            Throwable;

    /**
     * retorna as restrições de medidas da placa
     *
     * @param placa placa do veículo
     * @return a restrição da placa
     * @throws SQLException se ocorrer erro no banco
     */
    @NotNull
    Restricao getRestricoesByPlaca(String placa) throws Throwable;

    /**
     * retorna a lista de placas da unidade e também a meta de
     * dias em que cada placa deve ser aferido
     *
     * @param codUnidade código da unidade
     * @return um {@link CronogramaAfericao} contendo as placas para ser aferidas
     * @throws SQLException para qualquer erro do banco
     */
    @NotNull
    CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                           @NotNull final String codTipoVeiculo,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal,
                                           final int limit,
                                           final long offset) throws Throwable;

    @NotNull
    List<AfericaoAvulsa> getAfericoesAvulsas(@NotNull final Long codUnidade,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal,
                                             final int limit,
                                             final long offset) throws Throwable;

    /**
     * retorna uma aferição através do código dela
     *
     * @param codUnidade  código da unidade
     * @param codAfericao código da aferição
     * @return a aferição
     * @throws SQLException se ocorrer erro no banco
     */
    @NotNull
    Afericao getByCod(@NotNull final Long codUnidade, @NotNull final Long codAfericao) throws Throwable;
}