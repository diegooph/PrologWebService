package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;

import java.sql.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoDaoImpl extends DatabaseConnection {

    public Long insert(ProcessoMovimentacao processoMovimentacao) throws SQLException, OrigemDestinoInvalidaException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            return insert(processoMovimentacao, connection);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            closeConnection(connection, null, null);
        }
    }

    public Long insert(ProcessoMovimentacao processoMovimentacao, Connection conn)
            throws SQLException, OrigemDestinoInvalidaException {
        validaMovimentacoes(processoMovimentacao.getMovimentacoes());
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_processo(cod_unidade, data_hora, cpf_responsavel, " +
                    "observacao) " +
                    "VALUES (?,?,?,?) RETURNING codigo;");
            stmt.setLong(1, processoMovimentacao.getUnidade().getCodigo());
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(3, processoMovimentacao.getColaborador().getCpf());
            stmt.setString(4, processoMovimentacao.getObservacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codigoProcesso = rSet.getLong("CODIGO");
                processoMovimentacao.setCodigo(codigoProcesso);
                insertValores(processoMovimentacao, conn);
                conn.commit();
                return codigoProcesso;
            } else {
                throw new SQLException("Erro ao inserir processo de movimentação");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    private void insertValores(ProcessoMovimentacao processoMov, Connection conn) throws SQLException {
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
                stmt.setString(3, pneu.getCodigo());
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
                    fecharServicosPneu(conn, mov, codUnidade, processoMov.getColaborador().getCpf());

                    if (mov.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                        adicionaPneuVeiculo(conn, mov, codUnidade);
                    }

                    // Pneu voltou recapado, devemos incrementar a vida.
                    if (mov.isFromDestinoToOrigem(OrigemDestinoConstants.ANALISE, OrigemDestinoConstants.ESTOQUE)) {
                        mov.getPneu().setVidaAtual(mov.getPneu().getVidaAtual() + 1);
                        pneuDao.updateVida(mov.getPneu(), codUnidade, conn);
                        pneuDao.insertTrocaVidaPneu(mov.getPneu(), codUnidade, conn);
                    }

                    // Atualiza o status do pneu.
                    pneuDao.updateStatus(
                            mov.getPneu(),
                            codUnidade,
                            mov.getDestino().getTipo(),
                            conn);
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

    private void removePneuVeiculo(Connection conn, Long codUnidade, String placa, String codPneu) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE COD_UNIDADE = ? AND PLACA = ? AND " +
                    "COD_PNEU = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setString(3, codPneu);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao deletar o pneu do veículo");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void validaMovimentacoes(List<Movimentacao> movimentacoes) throws OrigemDestinoInvalidaException {
        final OrigemDestinoValidator origemDestinoValidator = new OrigemDestinoValidator();
        for (final Movimentacao movimentacao : movimentacoes) {
            origemDestinoValidator.validate(movimentacao.getOrigem(), movimentacao.getDestino());
        }
    }

    private void insertOrigem(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_origem (tipo_origem, cod_movimentacao, " +
                    "placa, km_veiculo, posicao_pneu_origem) values ((SELECT p.status\n" +
                    "FROM pneu p " +
                    "WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? in (select p.status from pneu p WHERE p.codigo = ? " +
                    "and p.cod_unidade = ?)),?,?,?,?)");
            stmt.setString(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            stmt.setString(4, movimentacao.getPneu().getCodigo());
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

    private void insertDestino(Connection conn, Movimentacao movimentacao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_destino(cod_movimentacao, " +
                    "tipo_destino, placa, km_veiculo, posicao_pneu_destino) values (?,?,?,?,?)");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, movimentacao.getDestino().getTipo());
            if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(destinoVeiculo.getVeiculo().getPlaca(), destinoVeiculo.getVeiculo()
                        .getKmAtual(), conn);
                stmt.setString(3, destinoVeiculo.getVeiculo().getPlaca());
                stmt.setLong(4, destinoVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(5, destinoVeiculo.getPosicaoDestinoPneu());
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.BIGINT);
                stmt.setNull(5, Types.INTEGER);
            }
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino da movimentação");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void fecharServicosPneu(Connection conn, Movimentacao movimentacao, Long codUnidade, Long cpfColaborador)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE afericao_manutencao SET\n" +
                    "  data_hora_resolucao = ?,\n" +
                    "  cpf_mecanico = ?,\n" +
                    "  km_momento_conserto = ? \n" +
                    "WHERE cod_pneu = ? and cod_unidade = ?");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(2, cpfColaborador);
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                final OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                stmt.setLong(3, origemVeiculo.getVeiculo().getKmAtual());
            } else if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                stmt.setLong(3, destinoVeiculo.getVeiculo().getKmAtual());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.executeUpdate();
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void adicionaPneuVeiculo(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO veiculo_pneu (placa, cod_pneu, cod_unidade, posicao) " +
                    "VALUES (?,?,?,?)");
            final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setString(1, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setString(2, movimentacao.getPneu().getCodigo());
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