package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

public interface ServicoDao {

    @NotNull
    Long criaServico(@NotNull final Connection conn,
                     @NotNull final Long codUnidade,
                     @NotNull final Long codPneu,
                     @NotNull final Long codAfericao,
                     @NotNull final TipoServico tipoServico) throws Throwable;

    void incrementaQtdApontamentosServico(@NotNull final Connection conn,
                                          @NotNull final Long codUnidade,
                                          @NotNull final Long codPneu,
                                          @NotNull final TipoServico tipoServico) throws Throwable;

    void calibragemToInspecao(@NotNull final Connection conn,
                              @NotNull final Long codUnidade,
                              @NotNull final Long codPneu) throws Throwable;

    @NotNull
    List<TipoServico> getServicosCadastradosByPneu(@NotNull final Long codUnidade,
                                                   @NotNull final Long codPneu) throws Throwable;

    ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException;

    @NotNull
    ServicoHolder getServicoHolder(@NotNull final String placa, @NotNull final Long codUnidade) throws Throwable;

    List<Servico> getServicosAbertosByPlaca(@NotNull String placa, @Nullable TipoServico tipoServico)
            throws SQLException;

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

    List<Servico> getServicosFechadosPneu(final Long codUnidade,
                                          final Long codPneu,
                                          final long dataInicial,
                                          final long dataFinal) throws SQLException;

    List<Servico> getServicosFechadosVeiculo(final Long codUnidade,
                                             final String placaVeiculo,
                                             final long dataInicial,
                                             final long dataFinal) throws SQLException;

    int getQuantidadeServicosEmAbertoPneu(final Long codUnidade,
                                          final Long codPneu,
                                          final Connection connection) throws SQLException;

    int fecharAutomaticamenteTodosServicosPneu(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final Long codProcesso,
            @NotNull final OffsetDateTime dataHorafechamentoServico,
            final long kmColetadoVeiculo,
            @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico) throws SQLException;

    int fecharAutomaticamenteServicosInspecaoPneu(@NotNull final Connection conn,
                                                  @NotNull final Long codUnidade,
                                                  @NotNull final Long codPneu,
                                                  @NotNull final Long codProcesso,
                                                  @NotNull final OffsetDateTime dataHorafechamentoServico,
                                                  final long kmColetadoVeiculo,
                                                  @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException;

    int fecharAutomaticamenteServicosCalibragemPneu(@NotNull final Connection conn,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final Long codPneu,
                                                    @NotNull final Long codProcesso,
                                                    @NotNull final OffsetDateTime dataHorafechamentoServico,
                                                    final long kmColetadoVeiculo,
                                                    @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException;

    int fecharAutomaticamenteServicosMovimentacaoPneu(@NotNull final Connection conn,
                                                      @NotNull final Long codUnidade,
                                                      @NotNull final Long codPneu,
                                                      @NotNull final Long codProcesso,
                                                      @NotNull final OffsetDateTime dataHorafechamentoServico,
                                                      final long kmColetadoVeiculo,
                                                      @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException;

    @NotNull
    VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico, @NotNull final String placaVeiculo)
            throws SQLException;
}