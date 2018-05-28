package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.MotivoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoDaoImpl extends DatabaseConnection implements MovimentacaoDao {
    private static final String TAG = MovimentacaoDaoImpl.class.getSimpleName();

    @Override
    public Long insert(@NotNull ProcessoMovimentacao processoMovimentacao,
                       @NotNull ServicoDao servicoDao,
                       boolean fecharServicosAutomaticamente) throws SQLException, OrigemDestinoInvalidaException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            final Long codigoProcessoMovimentacao = insert(
                    processoMovimentacao,
                    servicoDao,
                    fecharServicosAutomaticamente,
                    connection);
            connection.commit();
            return codigoProcessoMovimentacao;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public Long insert(@NotNull ProcessoMovimentacao processoMovimentacao,
                       @NotNull ServicoDao servicoDao,
                       boolean fecharServicosAutomaticamente,
                       @NotNull Connection conn) throws SQLException, OrigemDestinoInvalidaException {
        validaMovimentacoes(processoMovimentacao.getMovimentacoes());
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_processo(cod_unidade, data_hora, cpf_responsavel, " +
                    "observacao) " +
                    "VALUES (?,?,?,?) RETURNING codigo;");
            stmt.setLong(1, processoMovimentacao.getUnidade().getCodigo());
            stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(3, processoMovimentacao.getColaborador().getCpf());
            stmt.setString(4, processoMovimentacao.getObservacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codigoProcesso = rSet.getLong("CODIGO");
                processoMovimentacao.setCodigo(codigoProcesso);
                insertMovimentacoes(processoMovimentacao, servicoDao, fecharServicosAutomaticamente, conn);
                return codigoProcesso;
            } else {
                throw new SQLException("Erro ao inserir processo de movimentação");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    @Override
    public Long insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement("INSERT INTO " +
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
            closeConnection(connection, stmt, rSet);
        }
    }

    @Override
    public List<Motivo> getMotivos(@NotNull final Long codEmpresa, boolean onlyAtivos) throws SQLException {
        final List<Motivo> motivos = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            connection = getConnection();
            if (onlyAtivos) {
                stmt = connection.prepareStatement("SELECT * FROM movimentacao_motivo_descarte_empresa WHERE cod_empresa = ? AND ativo = TRUE");
            } else {
                stmt = connection.prepareStatement("SELECT * FROM movimentacao_motivo_descarte_empresa WHERE cod_empresa = ?");
            }
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                motivos.add(createMotivo(rSet));
            }
        } finally {
            closeConnection(connection, stmt, rSet);
        }
        return motivos;
    }

    @Override
    public void updateMotivoStatus(@NotNull final Long codEmpresa,
                                   @NotNull final Long codMotivo,
                                   @NotNull final Motivo motivo) throws SQLException {
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

    private Motivo createMotivo(@NotNull final ResultSet rSet) throws SQLException {
        final MotivoDescarte motivo = new MotivoDescarte();
        motivo.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        motivo.setCodigo(rSet.getLong("CODIGO"));
        motivo.setMotivo(rSet.getString("MOTIVO"));
        motivo.setAtivo(rSet.getBoolean("ATIVO"));
        return motivo;
    }

    private void insertMovimentacoes(ProcessoMovimentacao processoMov,
                                     ServicoDao servicoDao,
                                     boolean fecharServicosAutomaticamente,
                                     Connection conn) throws SQLException {
        final PneuDao pneuDao = Injection.providePneuDao();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            removePneusComOrigemVeiculo(processoMov, conn);
            final Long codUnidade = processoMov.getUnidade().getCodigo();
            stmt = conn.prepareStatement("INSERT INTO movimentacao(cod_movimentacao_processo, cod_unidade, " +
                    "cod_pneu, sulco_interno, sulco_central_interno, sulco_central_externo, sulco_externo, vida, " +
                    "observacao) VALUES (?,?,?,?,?,?,?,?,?) RETURNING codigo;");
            stmt.setLong(1, processoMov.getCodigo());
            stmt.setLong(2, codUnidade);
            for (final Movimentacao mov : processoMov.getMovimentacoes()) {
                final Pneu pneu = mov.getPneu();
                stmt.setLong(3, pneu.getCodigo());
                stmt.setDouble(4, pneu.getSulcosAtuais().getInterno());
                stmt.setDouble(5, pneu.getSulcosAtuais().getCentralInterno());
                stmt.setDouble(6, pneu.getSulcosAtuais().getCentralExterno());
                stmt.setDouble(7, pneu.getSulcosAtuais().getExterno());
                stmt.setDouble(8, pneu.getVidaAtual());
                stmt.setString(9, mov.getObservacao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    mov.setCodigo(rSet.getLong("CODIGO"));
                    insertOrigem(conn, mov, codUnidade);
                    insertDestino(conn, mov);
                    if (fecharServicosAutomaticamente) {
                        fecharServicosPneu(codUnidade, processoMov.getCodigo(), mov, servicoDao, conn);
                    }

                    if (mov.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                        adicionaPneuVeiculo(conn, mov, codUnidade);
                    }

                    // Pneu voltou recapado, devemos incrementar a vida.
                    if (mov.isFromDestinoToOrigem(OrigemDestinoConstants.ANALISE, OrigemDestinoConstants.ESTOQUE)) {
                        pneu.setVidaAtual(pneu.getVidaAtual() + 1);
                        pneuDao.trocarVida(pneu, codUnidade, conn);
                    }

                    // Atualiza o status do pneu.
                    pneuDao.updateStatus(pneu, codUnidade, mov.getDestino().getTipo(), conn);
                }
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    /**
     * Antes de o {@link ProcessoMovimentacao} começar a ser processado. Todos os pneus movimentados com origem no
     * veículo são antes desvinculados do mesmo. Com isso, removemos a dependência temporal no processamento das
     * movimentações, pois podemos primeiro movimentar um {@link Pneu} do estoque para o veículo mesmo que na posição
     * de destino dele já existisse um pneu.
     */
    private void removePneusComOrigemVeiculo(ProcessoMovimentacao processoMovimentacao, Connection conn) throws SQLException {
        for (final Movimentacao mov : processoMovimentacao.getMovimentacoes()) {
            if (mov.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                final OrigemVeiculo origem = (OrigemVeiculo) mov.getOrigem();
                removePneuVeiculo(
                        conn,
                        processoMovimentacao.getUnidade().getCodigo(),
                        origem.getVeiculo().getPlaca(),
                        mov.getPneu().getCodigo());
            }
        }
    }

    private void removePneuVeiculo(Connection conn, Long codUnidade, String placa, Long codPneu) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE COD_UNIDADE = ? AND PLACA = ? AND " +
                    "COD_PNEU = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setLong(3, codPneu);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao deletar o pneu do veículo");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void validaMovimentacoes(List<Movimentacao> movimentacoes) throws OrigemDestinoInvalidaException {
        // Garantimos que não exista mais de uma movimentação para um mesmo pneu.
        for (final Movimentacao m1 : movimentacoes) {
            int numCount = 0;
            final Pneu pneuMovimentado = m1.getPneu();
            for (final Movimentacao m2 : movimentacoes) {
                if (pneuMovimentado.equals(m2.getPneu())) {
                    numCount++;
                    if (numCount > 1) {
                        throw new IllegalStateException("Não é possível movimentar o mesmo pneu mais de uma vez no " +
                                "mesmo processo de movimentação! Pneu com mais de uma movimentação: "
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

    private void insertOrigem(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {

        switch (movimentacao.getOrigem().getTipo()) {
            case OrigemDestinoConstants.VEICULO:
                insertMovimentacaoOrigemVeiculo(conn, codUnidade, movimentacao);
                break;
            case OrigemDestinoConstants.ESTOQUE:
                insertMovimentacaoOrigemEstoque(conn, codUnidade, movimentacao);
                break;
            case OrigemDestinoConstants.ANALISE:
                insertMovimentacaoOrigemAnalise(conn, codUnidade, movimentacao);
                break;
            case OrigemDestinoConstants.DESCARTE:
                throw new SQLException("O ProLog não possibilita movimentar pneus do DESCARTE para nenhum outro destino");
        }

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_origem (tipo_origem, cod_movimentacao, " +
                    "placa, km_veiculo, posicao_pneu_origem) values ((SELECT p.status\n" +
                    "FROM pneu p " +
                    "WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? in (select p.status from pneu p WHERE p.codigo = ? " +
                    "and p.cod_unidade = ?)),?,?,?,?)");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                final OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(origemVeiculo.getVeiculo().getPlaca(), origemVeiculo.getVeiculo()
                        .getKmAtual(), conn);
                stmt.setString(7, origemVeiculo.getVeiculo().getPlaca());
                stmt.setLong(8, origemVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(9, origemVeiculo.getPosicaoOrigemPneu());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
            }
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem da movimentação");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void insertMovimentacaoOrigemAnalise(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_ORIGEM(TIPO_ORIGEM, COD_MOVIMENTACAO, PLACA, KM_VEICULO, " +
                    "                                POSICAO_PNEU_ORIGEM, COD_RECAPADORA_ORIGEM, COD_TIPO_SERVICO_RECAPADORA) " +
                    "VALUES ((SELECT P.STATUS " +
                    "  FROM PNEU P " +
                    "  WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? IN (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = ? " +
                    "  and P.COD_UNIDADE = ?)), ?, ?, ?, ?, (SELECT MD.COD_RECAPADORA_DESTINO " +
                    "                                        FROM MOVIMENTACAO_DESTINO AS MD " +
                    "                                          JOIN MOVIMENTACAO AS M ON MD.COD_MOVIMENTACAO = M.CODIGO " +
                    "                                        WHERE M.COD_UNIDADE = ? " +
                    "                                              AND M.COD_PNEU = ? " +
                    "                                              AND MD.TIPO_DESTINO = 'ANALISE' " +
                    "                                        ORDER BY M.CODIGO DESC LIMIT 1), ?);");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            stmt.setNull(7, Types.VARCHAR);
            stmt.setNull(8, Types.BIGINT);
            stmt.setNull(9, Types.INTEGER);
            stmt.setLong(10, codUnidade);
            stmt.setLong(11, movimentacao.getPneu().getCodigo());
            final OrigemAnalise origemAnalise = (OrigemAnalise) movimentacao.getOrigem();
//            stmt.setLong(12, origemAnalise.);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem estoque da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertMovimentacaoOrigemEstoque(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_ORIGEM(TIPO_ORIGEM, " +
                    "COD_MOVIMENTACAO, PLACA, KM_VEICULO, POSICAO_PNEU_ORIGEM) " +
                    "VALUES ((SELECT P.STATUS " +
                    "  FROM PNEU P " +
                    "  WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? IN (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = ? " +
                    "  and P.COD_UNIDADE = ?)), ?, ?, ?, ?);");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            stmt.setNull(7, Types.VARCHAR);
            stmt.setNull(8, Types.BIGINT);
            stmt.setNull(9, Types.INTEGER);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem estoque da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertMovimentacaoOrigemVeiculo(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_ORIGEM(TIPO_ORIGEM, " +
                    "COD_MOVIMENTACAO, PLACA, KM_VEICULO, POSICAO_PNEU_ORIGEM) " +
                    "VALUES ((SELECT P.STATUS " +
                    "  FROM PNEU P " +
                    "  WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? IN (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = ? " +
                    "  and P.COD_UNIDADE = ?)), ?, ?, ?, ?);");
            stmt.setLong(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            final OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            veiculoDao.updateKmByPlaca(
                    origemVeiculo.getVeiculo().getPlaca(),
                    origemVeiculo.getVeiculo().getKmAtual(),
                    conn);
            stmt.setString(7, origemVeiculo.getVeiculo().getPlaca());
            stmt.setLong(8, origemVeiculo.getVeiculo().getKmAtual());
            stmt.setInt(9, origemVeiculo.getPosicaoOrigemPneu());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem veiculo da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertDestino(@NotNull final Connection conn,
                               @NotNull final Movimentacao movimentacao) throws SQLException {
        switch (movimentacao.getDestino().getTipo()) {
            case OrigemDestinoConstants.VEICULO:
                insertMovimentacaoDestinoVeiculo(conn, movimentacao);
                break;
            case OrigemDestinoConstants.ESTOQUE:
                insertMovimentacaoDestinoEstoque(conn, movimentacao);
                break;
            case OrigemDestinoConstants.ANALISE:
                insertMovimentacaoDestinoAnalise(conn, movimentacao);
                break;
            case OrigemDestinoConstants.DESCARTE:
                insertMovimentacaoDestinoDescarte(conn, movimentacao);
                break;
        }
    }

    private void insertMovimentacaoDestinoVeiculo(@NotNull final Connection conn,
                                                  @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO, PLACA, KM_VEICULO, POSICAO_PNEU_DESTINO) " +
                    "VALUES (?, ?, ?, ?, ?);");
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, destinoVeiculo.getTipo());
            veiculoDao.updateKmByPlaca(
                    destinoVeiculo.getVeiculo().getPlaca(),
                    destinoVeiculo.getVeiculo().getKmAtual(),
                    conn);
            stmt.setString(3, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setLong(4, destinoVeiculo.getVeiculo().getKmAtual());
            stmt.setInt(5, destinoVeiculo.getPosicaoDestinoPneu());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino veiculo da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertMovimentacaoDestinoEstoque(@NotNull final Connection conn,
                                                  @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO) " +
                    "VALUES (?, ?);");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, movimentacao.getDestino().getTipo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino estoque da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertMovimentacaoDestinoAnalise(@NotNull final Connection conn,
                                                  @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO, COD_RECAPADORA_DESTINO, COD_COLETA) " +
                    "VALUES (?, ?, ?, ?);");
            final DestinoAnalise destinoAnalise = (DestinoAnalise) movimentacao.getDestino();
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, destinoAnalise.getTipo());
            if (destinoAnalise.getRecapadoraDestino() != null) {
                stmt.setLong(3, destinoAnalise.getRecapadoraDestino().getCodigo());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            stmt.setString(4, destinoAnalise.getCodigoColeta());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino analise da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }

    }

    private void insertMovimentacaoDestinoDescarte(@NotNull final Connection conn,
                                                   @NotNull final Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO, COD_MOTIVO_DESCARTE, " +
                    "URL_IMAGEM_DESCARTE_1, URL_IMAGEM_DESCARTE_2, URL_IMAGEM_DESCARTE_3) " +
                    "VALUES (?, ?, ?, ?, ?, ?);");
            final DestinoDescarte destinoDescarte = (DestinoDescarte) movimentacao.getDestino();
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, destinoDescarte.getTipo());
            stmt.setLong(3, destinoDescarte.getMotivoDescarte().getCodigo());
            stmt.setString(4, destinoDescarte.getUrlImagemDescarte1());
            stmt.setString(5, destinoDescarte.getUrlImagemDescarte2());
            stmt.setString(6, destinoDescarte.getUrlImagemDescarte3());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino descarte da movimentação");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void fecharServicosPneu(Long codUnidade,
                                    Long codProcessoMovimentacao,
                                    Movimentacao movimentacao,
                                    ServicoDao servicoDao,
                                    Connection conn) throws SQLException {
        if (movimentacao.isFromDestinoToOrigem(OrigemDestinoConstants.VEICULO, OrigemDestinoConstants.VEICULO)) {
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
            if (movimentacao.isFrom(OrigemDestinoConstants.VEICULO)) {
                final OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                final int qtdServicosFechadosPneu = servicoDao.fecharAutomaticamenteServicosPneu(
                        codUnidade,
                        codPneu,
                        codProcessoMovimentacao,
                        origemVeiculo.getVeiculo().getKmAtual(),
                        conn);

                if (qtdServicosEmAbertoPneu != qtdServicosFechadosPneu) {
                    throw new IllegalStateException("Erro ao fechar os serviços do pneu: " + codPneu + ". Deveriam ser fechados "
                            + qtdServicosEmAbertoPneu + " serviços mas foram fechados " + qtdServicosFechadosPneu + "!");
                }
            } else {
                throw new IllegalStateException("O pneu " + codPneu + " não está sendo movido do veículo mas possui " +
                        "serviços em aberto!");
            }
        } else {
            Log.d(TAG, "Não existem serviços em aberto para o pneu: " + codPneu);
        }
    }

    private void adicionaPneuVeiculo(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO veiculo_pneu (placa, cod_pneu, cod_unidade, posicao) " +
                    "VALUES (?, ?, ?, ?)");
            final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setString(1, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setLong(2, movimentacao.getPneu().getCodigo());
            stmt.setLong(3, codUnidade);
            stmt.setInt(4, destinoVeiculo.getPosicaoDestinoPneu());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao vincular o pneu " + movimentacao.getPneu() + " ao veículo " +
                        destinoVeiculo.getVeiculo().getPlaca());
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }
}