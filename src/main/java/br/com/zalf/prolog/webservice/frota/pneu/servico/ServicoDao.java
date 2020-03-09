package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

public interface ServicoDao {

    /**
     * Método utilizado para criar um {@link TipoServico} para um {@link Pneu}
     * com base em uma {@link Afericao} realizada.
     *
     * @param conn        {@link Connection} para ser utilizada para a comunicação com o banco de dados.
     * @param codUnidade  Código da {@link Unidade}.
     * @param codPneu     Código do {@link Pneu}.
     * @param codAfericao Código da {@link Afericao} que gerou o serviço.
     * @param tipoServico {@link TipoServico} que será inserido no banco de dados.
     * @return Código do {@link TipoServico} inserido no banco de dados.
     * @throws Throwable Se qualquer erro ocorrer na busca dos dados.
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    Long criaServico(@NotNull final Connection conn,
                     @NotNull final Long codUnidade,
                     @NotNull final Long codPneu,
                     @NotNull final Long codAfericao,
                     @NotNull final TipoServico tipoServico) throws Throwable;

    /**
     * Este método irá incrementar a contagem de vezes que esse problema foi identificado no {@link Pneu}.
     *
     * @param conn        {@link Connection} para ser utilizada para a comunicação com o banco de dados.
     * @param codUnidade  Código da {@link Unidade}.
     * @param codPneu     Código do {@link Pneu}.
     * @param tipoServico {@link TipoServico} que será incrementado.
     * @throws Throwable Se qualquer erro ocorrer na busca dos dados.
     */
    void incrementaQtdApontamentosServico(@NotNull final Connection conn,
                                          @NotNull final Long codUnidade,
                                          @NotNull final Long codPneu,
                                          @NotNull final TipoServico tipoServico) throws Throwable;

    /**
     * Método usado para converter um serviço em aberto de {@link TipoServico#CALIBRAGEM} para
     * {@link TipoServico#INSPECAO}. Essa troca só deve acontecer quando já existir um serviço de calibragem em
     * aberto e é identificado um serviço de inspeção no Pneu.
     *
     * @param conn       {@link Connection} para ser utilizada para a comunicação com o banco de dados.
     * @param codUnidade Código da {@link Unidade}.
     * @param codPneu    Código do {@link Pneu}.
     * @throws Throwable Se qualquer erro ocorrer na busca dos dados.
     */
    void calibragemToInspecao(@NotNull final Connection conn,
                              @NotNull final Long codUnidade,
                              @NotNull final Long codPneu) throws Throwable;

    /**
     * Método que lista todos os {@link TipoServico} em aberto que o {@link Pneu} tem.
     *
     * @param codUnidade Código da {@link Unidade}.
     * @param codPneu    Código do {@link Pneu}.
     * @return Uma lista contendo todos os {@link TipoServico} que o pneu possui.
     * @throws Throwable Se qualquer erro ocorrer na busca dos dados.
     */
    @NotNull
    List<TipoServico> getServicosCadastradosByPneu(@NotNull final Long codUnidade,
                                                   @NotNull final Long codPneu) throws Throwable;

    ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException;

    @NotNull
    ServicoHolder getServicoHolder(@NotNull final String placa, @NotNull final Long codUnidade) throws Throwable;

    List<Servico> getServicosAbertosByPlaca(@NotNull String placa, @Nullable TipoServico tipoServico) throws SQLException;

    void fechaServico(@NotNull final Long codUnidade,
                      @NotNull final OffsetDateTime dataHorafechamentoServico,
                      @NotNull final Servico servico) throws Throwable;

    Servico getServicoByCod(final Long codUnidade, final Long codServico) throws SQLException;

    ServicosFechadosHolder getQuantidadeServicosFechadosByVeiculo(final Long codUnidade,
                                                                  final long dataInicial,
                                                                  final long dataFinal) throws SQLException;


    ServicosFechadosHolder getQuantidadeServicosFechadosByPneu(final Long codUnidade,
                                                               final long dataInicial,
                                                               final long dataFinal) throws SQLException;

    List<Servico> getServicosFechados(final Long codUnidade,
                                      final long dataInicial,
                                      final long dataFinal) throws SQLException;

    /**
     * Retorna os serviços fechados referentes ao pneu com código {@code codPneu}.
     */
    List<Servico> getServicosFechadosPneu(final Long codUnidade,
                                          final Long codPneu,
                                          final long dataInicial,
                                          final long dataFinal) throws SQLException;

    /**
     * Retorna os serviços fechados referentes ao veículo com placa {@code placaVeiculo}.
     */
    List<Servico> getServicosFechadosVeiculo(final Long codUnidade,
                                             final String placaVeiculo,
                                             final long dataInicial,
                                             final long dataFinal) throws SQLException;

    int getQuantidadeServicosEmAbertoPneu(final Long codUnidade,
                                          final Long codPneu,
                                          final Connection connection) throws SQLException;

    /**
     * Fecha automaticamente os serviços de um {@link PneuComum}. Apenas através desse método é possível fechar serviços
     * deixando campos como CPF de quem realizou o fechamento em branco. O único modo de fechar automaticamente
     * serviços é através de uma {@link Movimentacao}, por isso o código do {@link ProcessoMovimentacao} é obrigatório,
     * já que ele deve estar acontecendo para este método ser usado.
     *
     * @return A quantidade de serviços fechados.
     * @throws SQLException Caso aconteça algum erro na operação com o BD.
     */
    int fecharAutomaticamenteServicosPneu(@NotNull final Connection conn,
                                          @NotNull final Long codUnidade,
                                          @NotNull final Long codPneu,
                                          @NotNull final Long codProcessoMovimentacao,
                                          @NotNull final OffsetDateTime dataHorafechamentoServico,
                                          final long kmColetadoVeiculo) throws SQLException;

    /**
     * Remonta um veículo como ele estava na época da abertura do {@link Servico}. Com todos os seus pneus
     * ({@link PneuComum}) na posição onde estavam na época e com os valores de sulco e pressão setados.
     */
    @NotNull
    VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico, @NotNull final String placaVeiculo)
            throws SQLException;
}