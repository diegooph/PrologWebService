package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface AfericaoDao {
    /**
     * Insere uma aferição.
     *
     * @param conn             Conexão que será utilizada para inserir a aferição.
     * @param codUnidade       Código da unidade onde a Aferição foi realizada.
     * @param afericao         Objeto contendo as medidas capturadas no processo de aferição
     * @param deveAbrirServico Flag indicando se é preciso abrir ordem de serviço.
     * @return Código da aferição inserida.
     * @throws Throwable Se ocorrer erro na inserção.
     */
    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final Afericao afericao,
                final boolean deveAbrirServico) throws Throwable;

    /**
     * Insere uma aferição lincada com o código da unidade.
     *
     * @param afericao         A {@link Afericao} contendo os dados da realização.
     * @param codUnidade       Código da {@link Unidade}.
     * @param deveAbrirServico Flag indicando se é preciso abrir ordem de serviço.
     * @return Código da aferição inserida.
     * @throws Throwable Se ocorrer erro no banco.
     */
    @NotNull
    Long insert(@NotNull final Long codUnidade,
                @NotNull final Afericao afericao,
                final boolean deveAbrirServico) throws Throwable;

    /**
     * Busca objeto contendo informações necessárias para se iniciar uma aferição do {@link Veiculo}.
     *
     * @param placa        placa do veículo
     * @param tipoAfericao tipo da aferição que será realizada
     * @return retorna o objeto da nova aferição
     * @throws Throwable Se ocorrer erro no banco.
     */
    @NotNull
    NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                           @NotNull final String placa,
                                           @NotNull final String tipoAfericao) throws Throwable;

    /**
     * Busca objeto contendo informações necessárias para se iniciar uma aferição avulsa do {@link Pneu}.
     *
     * @param codUnidade                  Código da {@link Unidade}.
     * @param codPneu                     Código do {@link Pneu} que será aferido.
     * @param tipoMedicaoColetadaAfericao Tipo da medição que será realizada.
     * @return Obejto {@link NovaAfericaoAvulsa} contendo as informações para a Afericão.
     * @throws Throwable Se ocorrer erro no banco.
     */
    @NotNull
    NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable;

    /**
     * Retorna as restrições de medidas da unidade.
     *
     * @param codUnidade Código da {@link Unidade}.
     * @return A {@link Restricao} da unidade
     * @throws Throwable Se ocorrer erro no banco.
     */
    @NotNull
    Restricao getRestricaoByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Retorna as restrições de medidas da placa.
     *
     * @param placa Placa do {@link Veiculo}.
     * @return A {@link Restricao} da placa.
     * @throws Throwable Se ocorrer erro no banco.
     */
    @NotNull
    Restricao getRestricoesByPlaca(@NotNull final String placa) throws Throwable;

    /**
     * Retorna a lista de placas das unidades selecionadas e também a meta de dias em que cada placa deve ser aferido.
     *
     * @param codUnidades Códigos das unidades selecionadas para o filtro do cronograma.
     * @return Um {@link CronogramaAfericao} contendo as placas para ser aferidas.
     * @throws Throwable Para qualquer erro do banco.
     */
    @NotNull
    CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para buscar uma lista de pneus para serem aferidos seguindo o
     * {@link TipoProcessoColetaAfericao#PNEU_AVULSO}. Esse método retorna apenas pneus que estão
     * em {@link StatusPneu#ESTOQUE}.
     *
     * @param codUnidade Código da unidade aplicado na busca.
     * @return Uma lista de pneus para serem aferidos.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Método para buscar a lista de aferições realizadas seguindo o {@link TipoProcessoColetaAfericao#PLACA}.
     * Esta busca é páginada e utiliza o {@code limit} e {@code offset} como parametros para a busca dos dados.
     *
     * @param codUnidade     Código da unidade aplicado na busca.
     * @param codTipoVeiculo Tipo de veículo filtrado na busca.
     * @param placaVeiculo   Placa do veículo a ser buscado.
     * @param dataInicial    Data inicial do período de filtro.
     * @param dataFinal      Data final do período de filtro.
     * @param limit          Quantidade de itens a serem buscados.
     * @param offset         Ponto inicial da busca.
     * @return Uma lista contendo as {@link AfericaoPlaca}s realizadas.
     * @throws Throwable Se qualquer erro na busca ocorrer.
     */
    @NotNull
    List<AfericaoPlaca> getAfericoesPlacas(@NotNull final Long codUnidade,
                                           @NotNull final String codTipoVeiculo,
                                           @NotNull final String placaVeiculo,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal,
                                           final int limit,
                                           final long offset) throws Throwable;

    /**
     * Método para buscar a lista de aferições realizadas seguindo o {@link TipoProcessoColetaAfericao#PNEU_AVULSO}.
     * Esta busca é páginada e utiliza o {@code limit} e {@code offset} como parametros para a busca dos dados.
     *
     * @param codUnidade  Código da {@link Unidade} que os dados serão buscados.
     * @param dataInicial Data inicial do período de filtro.
     * @param dataFinal   Data final do período de filtro.
     * @param limit       Quantidade de itens a serem buscados.
     * @param offset      Ponto inicial da busca.
     * @return Uma lista contendo as {@link AfericaoAvulsa}s realizadas.
     * @throws Throwable Se qualquer erro na busca ocorrer.
     */
    @NotNull
    List<AfericaoAvulsa> getAfericoesAvulsas(@NotNull final Long codUnidade,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal,
                                             final int limit,
                                             final long offset) throws Throwable;

    /**
     * Método para gerar um relatório contendo as aferições avulsas realizadas pelo colaborador
     * especificado pelo {@code codColaborador}.
     *
     * @param codUnidade     Código da {@link Unidade} que os dados serão buscados.
     * @param codColaborador O código do colaborador que realizou a aferição, pelo qual as buscas serão filtradas.
     *                       É opcional.
     * @param dataInicial    Data inicial do período de filtro.
     * @param dataFinal      Data final do período de filtro.
     * @return Um objeto {@link Report} com os dados filtrados.
     * @throws Throwable Se algum erro na busca dos dados ocorrer.
     */
    @NotNull
    Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                               @Nullable final Long codColaborador,
                               @NotNull final LocalDate dataInicial,
                               @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Busca uma aferição específico pelo código.
     *
     * @param codUnidade  Código da unidade.
     * @param codAfericao Código da aferição.
     * @return Uma {@link Afericao aferição.}
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    Afericao getByCod(@NotNull final Long codUnidade, @NotNull final Long codAfericao) throws Throwable;

    /**
     * Método utilizado para buscar as configurações de aferição para uma {@code placa} de específica.
     *
     * @param placa Placa para qual as configurações serão buscadas.
     * @return {@link ConfiguracaoNovaAfericao} contendo os atributos de configuração.
     * @throws Throwable Se algum erro no processo ocorrer.
     */
    @NotNull
    ConfiguracaoNovaAfericao getConfiguracaoNovaAfericao(@NotNull final String placa) throws Throwable;
}