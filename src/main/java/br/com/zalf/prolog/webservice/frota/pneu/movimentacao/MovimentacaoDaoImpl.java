package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoFuncaoProlog;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoResposta;
import br.com.zalf.prolog.webservice.customfields._model.ColunaTabelaResposta;
import br.com.zalf.prolog.webservice.customfields._model.ColunaTabelaRespostaBuilder;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.Motivo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.PneuServicoRealizadoDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizadoIncrementaVida;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.OrigemFechamentoAutomaticoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum.VEICULO;

/**
 * Created by Zart on 03/03/17.
 */
public final class MovimentacaoDaoImpl extends DatabaseConnection implements MovimentacaoDao {
    private static final String TAG = MovimentacaoDaoImpl.class.getSimpleName();

    @NotNull
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codigoProcessoMovimentacao = insert(
                    conn,
                    servicoDao,
                    campoPersonalizadoDao,
                    processoMovimentacao,
                    dataHoraMovimentacao,
                    fecharServicosAutomaticamente,
                    false);
            conn.commit();
            return codigoProcessoMovimentacao;
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        return insert(conn,
                      servicoDao,
                      campoPersonalizadoDao,
                      processoMovimentacao,
                      dataHoraMovimentacao,
                      fecharServicosAutomaticamente,
                      false);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente,
                       final boolean veioDoServico) throws Throwable {
        validaMovimentacoes(processoMovimentacao.getMovimentacoes());
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO MOVIMENTACAO_PROCESSO(COD_UNIDADE, DATA_HORA, CPF_RESPONSAVEL, OBSERVACAO) " +
                            "VALUES (?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, processoMovimentacao.getUnidade().getCodigo());
            stmt.setObject(2, dataHoraMovimentacao);
            stmt.setLong(3, processoMovimentacao.getColaborador().getCpf());
            stmt.setString(4, processoMovimentacao.getObservacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codigoProcesso = rSet.getLong("CODIGO");
                processoMovimentacao.setCodigo(codigoProcesso);
                insertMovimentacoes(
                        conn,
                        servicoDao,
                        processoMovimentacao,
                        dataHoraMovimentacao,
                        fecharServicosAutomaticamente);
                updateKmVeiculoIfNeeded(conn, processoMovimentacao, dataHoraMovimentacao, veioDoServico);
                final List<CampoPersonalizadoResposta> respostas =
                        processoMovimentacao.getRespostasCamposPersonalizados();
                if (respostas != null && !respostas.isEmpty()) {
                    campoPersonalizadoDao.salvaRespostasCamposPersonalizados(
                            conn,
                            CampoPersonalizadoFuncaoProlog.MOVIMENTACAO,
                            respostas,
                            new ColunaTabelaRespostaBuilder()
                                    .addColunaEspecifica(
                                            new ColunaTabelaResposta("cod_processo_movimentacao", codigoProcesso))
                                    .getColunas());
                }
                return codigoProcesso;
            } else {
                throw new SQLException("Erro ao inserir processo de movimentação");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insertMovimentacaoServicoAfericao(@NotNull final Connection conn,
                                                  @NotNull final ServicoDao servicoDao,
                                                  @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                                                  @NotNull final ProcessoMovimentacao processoMovimentacao,
                                                  @NotNull final OffsetDateTime dataHoraMovimentacao,
                                                  final boolean fecharServicosAutomaticamente) throws Throwable {
        return insert(conn,
                      servicoDao,
                      campoPersonalizadoDao,
                      processoMovimentacao,
                      dataHoraMovimentacao,
                      fecharServicosAutomaticamente,
                      true);
    }

    @NotNull
    @Override
    public Long insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO " +
                                                 "movimentacao_motivo_descarte_empresa(cod_empresa, motivo, ativo, " +
                                                 "data_hora_insercao, data_hora_ultima_alteracao) " +
                                                 "VALUES (?, ?, ?, ?, ?) RETURNING codigo");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, motivo.getMotivo());
            stmt.setBoolean(3, true);
            final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
            stmt.setObject(4, now);
            // Ao inserir um motivo setamos a data de alteração como a data atual.
            stmt.setObject(5, now);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir novo motivo de descarte");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<Motivo> getMotivos(@NotNull final Long codEmpresa, final boolean onlyAtivos) throws Throwable {
        final List<Motivo> motivos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            if (onlyAtivos) {
                stmt = conn.prepareStatement(
                        "SELECT * FROM movimentacao_motivo_descarte_empresa WHERE cod_empresa = ? AND ativo = TRUE");
            } else {
                stmt =
                        conn.prepareStatement("SELECT * FROM movimentacao_motivo_descarte_empresa WHERE cod_empresa =" +
                                                      " ?");
            }
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                motivos.add(createMotivo(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return motivos;
    }

    @Override
    public void updateMotivoStatus(@NotNull final Long codEmpresa,
                                   @NotNull final Long codMotivo,
                                   @NotNull final Motivo motivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE movimentacao_motivo_descarte_empresa " +
                                                 "SET ativo = ?, data_hora_ultima_alteracao = ?" +
                                                 " WHERE cod_empresa = ? AND codigo = ?");
            stmt.setBoolean(1, motivo.isAtivo());
            stmt.setObject(2, LocalDateTime.now(Clock.systemUTC()));
            stmt.setLong(3, codEmpresa);
            stmt.setLong(4, codMotivo);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar o status do motivo: " + codMotivo);
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @NotNull
    private Motivo createMotivo(@NotNull final ResultSet rSet) throws Throwable {
        final MotivoDescarte motivo = new MotivoDescarte();
        motivo.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        motivo.setCodigo(rSet.getLong("CODIGO"));
        motivo.setMotivo(rSet.getString("MOTIVO"));
        motivo.setAtivo(rSet.getBoolean("ATIVO"));
        return motivo;
    }

    @SuppressWarnings("checkstyle:SingleSpaceSeparator")
    private void insertMovimentacoes(@NotNull final Connection conn,
                                     @NotNull final ServicoDao servicoDao,
                                     @NotNull final ProcessoMovimentacao processoMov,
                                     @NotNull final OffsetDateTime dataHoraMovimentacao,
                                     final boolean fecharServicosAutomaticamente) throws Throwable {
        final PneuDao pneuDao = Injection.providePneuDao();
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final PneuServicoRealizadoDao pneuServicoRealizadoDao = Injection.providePneuServicoRealizadoDao();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            removePneusComOrigemVeiculo(conn, veiculoDao, processoMov);
            final Long codUnidade = processoMov.getUnidade().getCodigo();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOVIMENTACAO_INSERE_MOVIMENTACAO(" +
                                                 "F_COD_UNIDADE => ?, " +
                                                 "F_COD_MOVIMENTACAO_PROCESSO => ? ," +
                                                 "F_COD_PNEU => ?," +
                                                 "F_OBSERVACAO => ?) AS V_COD_MOVIMENTACAO_REALIZADA; ");
            // Podemos realizar o suppress pois neste ponto já temos que possuir um código não nulo.
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, processoMov.getCodigo());
            for (final Movimentacao mov : processoMov.getMovimentacoes()) {
                final Pneu pneu = mov.getPneu();
                stmt.setLong(3, pneu.getCodigo());
                stmt.setString(4, mov.getObservacao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    mov.setCodigo(rSet.getLong("V_COD_MOVIMENTACAO_REALIZADA"));
                    insertOrigem(conn, pneuDao, pneuServicoRealizadoDao, codUnidade, mov);
                    insertDestino(conn, veiculoDao, codUnidade, mov);
                    if (mov.getCodMotivoMovimento() != null) {
                        insertMotivoMovimento(conn, mov.getCodigo(), mov.getCodMotivoMovimento());
                    }
                    if (fecharServicosAutomaticamente) {
                        fecharServicosPneu(
                                conn,
                                servicoDao,
                                codUnidade,
                                processoMov.getCodigo(),
                                mov,
                                dataHoraMovimentacao);
                    }
                    pneuDao.updateStatus(conn, pneu, mov.getDestino().getTipo().toStatusPneu());
                }
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void updateKmVeiculoIfNeeded(@NotNull final Connection conn,
                                         @NotNull final ProcessoMovimentacao processoMovimentacao,
                                         @NotNull final OffsetDateTime dataHoraMovimentacao,
                                         final boolean veioDoServico) {
        final Optional<Veiculo> veiculoMovimentacao = getVeiculoMovimentacao(processoMovimentacao);
        veiculoMovimentacao
                .ifPresent(veiculo -> {
                    if (!veioDoServico) {
                        //noinspection ConstantConditions
                        Injection
                                .provideVeiculoDao()
                                .updateKmByCodVeiculo(conn,
                                                      processoMovimentacao.getUnidade().getCodigo(),
                                                      veiculo.getCodigo(),
                                                      processoMovimentacao.getCodigo(),
                                                      VeiculoTipoProcesso.MOVIMENTACAO,
                                                      dataHoraMovimentacao,
                                                      veiculo.getKmAtual(),
                                                      true);
                    }
                });
    }

    @NotNull
    private Optional<Veiculo> getVeiculoMovimentacao(@NotNull final ProcessoMovimentacao processoMovimentacao) {
        final Optional<Movimentacao> movimentacao = processoMovimentacao
                .getMovimentacoes()
                .stream()
                .filter(mov -> mov.isFrom(VEICULO) || mov.isTo(VEICULO))
                .findFirst();

        if (movimentacao.isPresent()) {
            final Movimentacao mov = movimentacao.get();
            return Optional.of(
                    mov.isFrom(VEICULO)
                            ? ((OrigemVeiculo) mov.getOrigem()).getVeiculo()
                            : ((DestinoVeiculo) mov.getDestino()).getVeiculo());
        }

        return Optional.empty();
    }

    /**
     * Antes de o {@link ProcessoMovimentacao} começar a ser processado. Todos os pneus movimentados com origem no
     * veículo são antes desvinculados do mesmo. Com isso, removemos a dependência temporal no processamento das
     * movimentações, pois podemos primeiro movimentar um {@link Pneu} do estoque para o veículo mesmo que na posição
     * de destino dele já existisse um pneu.
     */
    private void removePneusComOrigemVeiculo(@NotNull final Connection conn,
                                             @NotNull final VeiculoDao veiculoDao,
                                             @NotNull final ProcessoMovimentacao processoMovimentacao)
            throws Throwable {
        for (final Movimentacao mov : processoMovimentacao.getMovimentacoes()) {
            if (mov.getOrigem().getTipo().equals(VEICULO)) {
                final OrigemVeiculo origem = (OrigemVeiculo) mov.getOrigem();
                veiculoDao.removePneuVeiculo(
                        conn,
                        processoMovimentacao.getUnidade().getCodigo(),
                        origem.getVeiculo().getCodigo(),
                        mov.getPneu().getCodigo());
            }
        }
    }

    private void validaMovimentacoes(@NotNull final List<Movimentacao> movimentacoes)
            throws OrigemDestinoInvalidaException {
        // Garantimos que não exista mais de uma movimentação para um mesmo pneu.
        for (final Movimentacao m1 : movimentacoes) {
            int numCount = 0;
            final Pneu pneuMovimentado = m1.getPneu();
            for (final Movimentacao m2 : movimentacoes) {
                if (pneuMovimentado.equals(m2.getPneu())) {
                    numCount++;
                    if (numCount > 1) {
                        throw new IllegalStateException("Não é possível movimentar o mesmo pneu mais de uma vez no " +
                                                                "mesmo processo de movimentação! Pneu com mais de uma" +
                                                                " movimentação: "
                                                                + pneuMovimentado.getCodigo());
                    }
                }
            }
        }

        // Aqui validamos que todas as movimentações respeitam as regras de origem/destino.
        final OrigemDestinoValidator origemDestinoValidator = new OrigemDestinoValidator();
        for (final Movimentacao movimentacao : movimentacoes) {
            origemDestinoValidator.validate(movimentacao.getOrigem(), movimentacao.getDestino());
        }
    }

    private void insertMotivoMovimento(@NotNull final Connection conn,
                                       @NotNull final Long codMovimento,
                                       @NotNull final Long codMotivo) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT FUNC_MOVIMENTACAO_INSERE_MOTIVO_MOVIMENTO_RESPOSTA(" +
                                                 "F_COD_MOVIMENTO := ?," +
                                                 "F_COD_MOTIVO := ?);");
            stmt.setLong(1, codMovimento);
            stmt.setLong(2, codMotivo);
            stmt.executeQuery();
        } finally {
            close(stmt);
        }
    }

    private void insertOrigem(@NotNull final Connection conn,
                              @NotNull final PneuDao pneuDao,
                              @NotNull final PneuServicoRealizadoDao pneuServicoRealizadoDao,
                              @NotNull final Long codUnidade,
                              @NotNull final Movimentacao movimentacao) throws Throwable {
        switch (movimentacao.getOrigem().getTipo()) {
            case VEICULO:
                insertMovimentacaoOrigemVeiculo(conn, codUnidade, movimentacao);
                break;
            case ESTOQUE:
                insertMovimentacaoOrigemEstoque(conn, codUnidade, movimentacao);
                break;
            case ANALISE:
                insertMovimentacaoOrigemAnalise(conn, codUnidade, movimentacao);
                // Apenas movimentações da ANALISE para o ESTOQUE possuem serviços realizados no pneu
                if (movimentacao.isTo(OrigemDestinoEnum.ESTOQUE)) {
                    insertServicosRealizadosPneu(conn, pneuDao, pneuServicoRealizadoDao, codUnidade, movimentacao);
                }
                break;
            case DESCARTE:
                throw new SQLException("O ProLog não possibilita movimentar pneus do DESCARTE para nenhum outro " +
                                               "destino");
        }
    }

    private void insertDestino(@NotNull final Connection conn,
                               @NotNull final VeiculoDao veiculoDao,
                               @NotNull final Long codUnidade,
                               @NotNull final Movimentacao movimentacao) throws Throwable {
        switch (movimentacao.getDestino().getTipo()) {
            case VEICULO:
                insertMovimentacaoDestinoVeiculo(conn, veiculoDao, codUnidade, movimentacao);
                break;
            case ESTOQUE:
                insertMovimentacaoDestinoEstoque(conn, movimentacao);
                break;
            case ANALISE:
                insertMovimentacaoDestinoAnalise(conn, movimentacao);
                break;
            case DESCARTE:
                insertMovimentacaoDestinoDescarte(conn, movimentacao);
                break;
        }
    }

    private void insertServicosRealizadosPneu(@NotNull final Connection conn,
                                              @NotNull final PneuDao pneuDao,
                                              @NotNull final PneuServicoRealizadoDao pneuServicoRealizadoDao,
                                              @NotNull final Long codUnidade,
                                              @NotNull final Movimentacao movimentacao) throws Throwable {
        final OrigemAnalise origemAnalise = (OrigemAnalise) movimentacao.getOrigem();
        final List<PneuServicoRealizado> servicosRealizados = origemAnalise.getServicosRealizados();
        validaServicosRealizados(movimentacao.getPneu().getCodigo(), servicosRealizados);
        final Pneu pneuMovimentacao = movimentacao.getPneu();
        for (final PneuServicoRealizado servico : servicosRealizados) {
            final Long codServicoRealizado = pneuServicoRealizadoDao.insertServicoByMovimentacao(
                    conn,
                    pneuDao,
                    codUnidade,
                    pneuMovimentacao,
                    servico);
            insertMovimentacaoServicoRealizado(conn, codServicoRealizado, movimentacao.getCodigo());
            // Como não foi modelado desde o começo do sistema o conceito de recapadoras, existem pneus em análise que
            // não possuem nenhuma recapadora vinculada. Para esses pneus, nós não podemos inserir na tabela de vínculo
            // que liga uma movimentação a uma recapadora.
            if (pneuTemRecapadora(conn, pneuMovimentacao.getCodigo())) {
                insertMovimentacaoServicoRealizadoRecapadora(
                        conn,
                        pneuMovimentacao.getCodigo(),
                        codServicoRealizado,
                        movimentacao.getCodigo());
            }
        }
    }

    private void validaServicosRealizados(@NotNull final Long codPneu,
                                          @NotNull final List<PneuServicoRealizado> servicosRealizados)
            throws Throwable {
        if (servicosRealizados.isEmpty()) {
            throw new IllegalStateException("O pneu " + codPneu + " foi movido dá análise " +
                                                    "para o estoque e " +
                                                    "não teve nenhum serviço aplicado!");
        }

        boolean temIncrementaVida = false;
        for (int i = 0; i < servicosRealizados.size(); i++) {
            final PneuServicoRealizado servico = servicosRealizados.get(i);
            if (servico instanceof PneuServicoRealizadoIncrementaVida) {
                if (temIncrementaVida) {
                    throw new GenericException("Não é possível realizar dois serviços de troca de banda na " +
                                                       "mesma movimentação");
                }
                temIncrementaVida = true;
            }
        }
    }

    private boolean pneuTemRecapadora(@NotNull final Connection conn,
                                      @NotNull final Long codPneu) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                                                 "  CASE " +
                                                 "  WHEN (SELECT MD.COD_RECAPADORA_DESTINO " +
                                                 "        FROM MOVIMENTACAO_DESTINO AS MD " +
                                                 "          JOIN MOVIMENTACAO AS M ON MD.COD_MOVIMENTACAO = M.CODIGO " +
                                                 "        WHERE M.COD_PNEU = ? AND MD.TIPO_DESTINO = 'ANALISE' " +
                                                 "        ORDER BY M.CODIGO DESC LIMIT 1) IS NULL " +
                                                 "    THEN FALSE " +
                                                 "  ELSE TRUE " +
                                                 "  END AS TEM_RECAPADORA;");
            stmt.setLong(1, codPneu);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("TEM_RECAPADORA");
            } else {
                throw new SQLException("Não foi possível descobrir se o pneu " + codPneu + " tem recapadora associada");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertMovimentacaoServicoRealizado(@NotNull final Connection conn,
                                                    @NotNull final Long codServicoRealizado,
                                                    @NotNull final Long codMovimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_PNEU_SERVICO_REALIZADO " +
                                                 "(COD_MOVIMENTACAO, COD_SERVICO_REALIZADO) " +
                                                 "VALUES (?, ?);");
            stmt.setLong(1, codMovimentacao);
            stmt.setLong(2, codServicoRealizado);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inserir o Serviço Realizado " +
                                               "na tabela de vínculo com a Movimentação " +
                                               "(movimentacao_servico_realizado)");
            }
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoServicoRealizadoRecapadora(@NotNull final Connection conn,
                                                              @NotNull final Long codPneu,
                                                              @NotNull final Long codServicoRealizado,
                                                              @NotNull final Long codMovimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA " +
                                                 "(COD_MOVIMENTACAO, COD_SERVICO_REALIZADO_MOVIMENTACAO, " +
                                                 "COD_RECAPADORA) " +
                                                 "VALUES (?, ?, (SELECT MD.COD_RECAPADORA_DESTINO " +
                                                 "FROM MOVIMENTACAO_DESTINO AS MD " +
                                                 "JOIN MOVIMENTACAO AS M ON MD.COD_MOVIMENTACAO = M.CODIGO " +
                                                 "WHERE M.COD_PNEU = ? AND MD.TIPO_DESTINO = 'ANALISE' " +
                                                 "ORDER BY M.CODIGO DESC LIMIT 1));");
            stmt.setLong(1, codMovimentacao);
            stmt.setLong(2, codServicoRealizado);
            stmt.setLong(3, codPneu);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir vinculo da movimentacao com o serviço realizado " +
                                               "na tabale MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA");
            }
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoOrigemAnalise(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Movimentacao movimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_ORIGEM(TIPO_ORIGEM, COD_MOVIMENTACAO) " +
                                                 "VALUES ((SELECT P.STATUS " +
                                                 "  FROM PNEU P " +
                                                 "  WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? IN (SELECT P.STATUS " +
                                                 "FROM PNEU P WHERE P.CODIGO = ? " +
                                                 "  and P.COD_UNIDADE = ?)), ?);");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo().asString());
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem estoque da movimentação");
            }
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoOrigemEstoque(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Movimentacao movimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_ORIGEM(TIPO_ORIGEM, COD_MOVIMENTACAO) " +
                                                 "VALUES ((SELECT P.STATUS " +
                                                 "  FROM PNEU P " +
                                                 "  WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? IN (SELECT P.STATUS " +
                                                 "FROM PNEU P WHERE P.CODIGO = ? " +
                                                 "  and P.COD_UNIDADE = ?)), ?);");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo().asString());
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem estoque da movimentação");
            }
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoOrigemVeiculo(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Movimentacao movimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{ call func_movimentacao_insert_movimentacao_veiculo_origem(" +
                                            "f_cod_pneu => ?, " +
                                            "f_cod_unidade => ?, " +
                                            "f_tipo_origem => ?, " +
                                            "f_cod_movimentacao => ?, " +
                                            "f_cod_veiculo => ?," +
                                            "f_posicao_prolog => ?)}");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo().asString());
            stmt.setLong(4, movimentacao.getCodigo());
            final OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
            stmt.setLong(5, origemVeiculo.getVeiculo().getCodigo());
            stmt.setInt(6, origemVeiculo.getPosicaoOrigemPneu());
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoDestinoVeiculo(@NotNull final Connection conn,
                                                  @NotNull final VeiculoDao veiculoDao,
                                                  @NotNull final Long codUnidade,
                                                  @NotNull final Movimentacao movimentacao) throws Throwable {
        final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
        // Primeiro aplicamos o pneu ao veículo e também atualizamos o KM do veículo.
        veiculoDao.adicionaPneuVeiculo(
                conn,
                codUnidade,
                destinoVeiculo.getVeiculo().getPlaca(),
                movimentacao.getPneu().getCodigo(),
                destinoVeiculo.getPosicaoDestinoPneu());
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{call func_movimentacao_insert_movimentacao_veiculo_destino(" +
                                            "f_cod_movimentacao => ?, " +
                                            "f_tipo_destino => ?, " +
                                            "f_cod_veiculo => ?," +
                                            "f_posicao_prolog => ?)}");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, destinoVeiculo.getTipo().asString());
            stmt.setLong(3, destinoVeiculo.getVeiculo().getCodigo());
            stmt.setInt(4, destinoVeiculo.getPosicaoDestinoPneu());
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoDestinoEstoque(@NotNull final Connection conn,
                                                  @NotNull final Movimentacao movimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                                                 "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO) " +
                                                 "VALUES (?, ?);");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, movimentacao.getDestino().getTipo().asString());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino estoque da movimentação");
            }
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoDestinoAnalise(@NotNull final Connection conn,
                                                  @NotNull final Movimentacao movimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                                                 "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO, " +
                                                 "COD_RECAPADORA_DESTINO, COD_COLETA) " +
                                                 "VALUES (?, ?, ?, ?);");
            final DestinoAnalise destinoAnalise = (DestinoAnalise) movimentacao.getDestino();
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, destinoAnalise.getTipo().asString());
            stmt.setLong(3, destinoAnalise.getRecapadoraDestino().getCodigo());
            stmt.setString(4, destinoAnalise.getCodigoColeta());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino análise da movimentação");
            }
        } finally {
            close(stmt);
        }
    }

    private void insertMovimentacaoDestinoDescarte(@NotNull final Connection conn,
                                                   @NotNull final Movimentacao movimentacao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                                                 "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO, " +
                                                 "COD_MOTIVO_DESCARTE, " +
                                                 "URL_IMAGEM_DESCARTE_1, URL_IMAGEM_DESCARTE_2, " +
                                                 "URL_IMAGEM_DESCARTE_3) " +
                                                 "VALUES (?, ?, ?, ?, ?, ?);");
            final DestinoDescarte destinoDescarte = (DestinoDescarte) movimentacao.getDestino();
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, destinoDescarte.getTipo().asString());
            stmt.setLong(3, destinoDescarte.getMotivoDescarte().getCodigo());
            stmt.setString(4, destinoDescarte.getUrlImagemDescarte1());
            stmt.setString(5, destinoDescarte.getUrlImagemDescarte2());
            stmt.setString(6, destinoDescarte.getUrlImagemDescarte3());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino descarte da movimentação");
            }
        } finally {
            close(stmt);
        }
    }

    private void fecharServicosPneu(@NotNull final Connection conn,
                                    @NotNull final ServicoDao servicoDao,
                                    @NotNull final Long codUnidade,
                                    @NotNull final Long codProcessoMovimentacao,
                                    @NotNull final Movimentacao movimentacao,
                                    @NotNull final OffsetDateTime dataHoraMovimentacao) throws Throwable {
        if (movimentacao.isFromOrigemToDestino(VEICULO, VEICULO)) {
            Log.d(TAG, "O pneu " + movimentacao.getPneu().getCodigo()
                    + " está sendo movido dentro do mesmo veículo, não é preciso fechar seus serviços");
            return;
        }

        final Long codPneu = movimentacao.getPneu().getCodigo();
        final int qtdServicosEmAbertoPneu = servicoDao.getQuantidadeServicosEmAbertoPneu(
                codUnidade,
                codPneu,
                conn);
        if (qtdServicosEmAbertoPneu > 0) {
            if (movimentacao.isFrom(VEICULO)) {
                final OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                final int qtdServicosFechadosPneu = servicoDao.fecharAutomaticamenteTodosServicosPneu(
                        conn,
                        codUnidade,
                        codPneu,
                        codProcessoMovimentacao,
                        dataHoraMovimentacao,
                        origemVeiculo.getVeiculo().getKmAtual(),
                        OrigemFechamentoAutomaticoEnum.MOVIMENTACAO);

                if (qtdServicosEmAbertoPneu != qtdServicosFechadosPneu) {
                    throw new IllegalStateException("Erro ao fechar os serviços do pneu: " + codPneu + ". Deveriam " +
                                                            "ser fechados "
                                                            + qtdServicosEmAbertoPneu + " serviços mas foram fechados" +
                                                            " " + qtdServicosFechadosPneu + "!");
                }
            } else {
                throw new IllegalStateException("O pneu " + codPneu + " não está sendo movido do veículo mas possui " +
                                                        "serviços em aberto!");
            }
        } else {
            Log.d(TAG, "Não existem serviços em aberto para o pneu: " + codPneu);
        }
    }
}