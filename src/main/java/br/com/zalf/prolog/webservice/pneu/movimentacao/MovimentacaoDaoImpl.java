package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.pneu.movimentacao.*;
import br.com.zalf.prolog.frota.pneu.movimentacao.destino.DestinoVeiculo;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDaoImpl;

import java.sql.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoDaoImpl extends DatabaseConnection {

    public boolean insert(ProcessoMovimentacao movimentacao) throws SQLException, OrigemDestinoInvalidaException {
        validaMovimentacoes(movimentacao.getMovimentacoes());
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO movimentacao_processo(cod_unidade, data_hora, cpf_responsavel, observacao) " +
                    "VALUES (?,?,?,?) RETURNING codigo;");
            stmt.setLong(1, movimentacao.getUnidade().getCodigo());
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(3, movimentacao.getColaborador().getCpf());
            stmt.setString(4, movimentacao.getObservacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                movimentacao.setCodigo(rSet.getLong("CODIGO"));
                insertValores(movimentacao, conn);
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return false;
    }

    private boolean insertValores(ProcessoMovimentacao movimentacao, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // antes de fazer qualquer movimentação, remover todos os pneus que sairam do veículo
            removeOrigensVeiculo(movimentacao, conn);
            stmt = conn.prepareStatement("INSERT INTO movimentacao(cod_movimentacao_processo, cod_unidade, \n " +
                    "cod_pneu, sulco_interno, sulco_central, sulco_externo,  vida, observacao)\n " +
                    "VALUES (?,?,?,\n " +
                    "(select altura_sulco_interno from pneu where codigo = ? and cod_unidade = ?),\n " +
                    "(select altura_sulco_central from pneu where codigo = ? and cod_unidade = ?),\n " +
                    "(select altura_sulco_externo from pneu where codigo = ? and cod_unidade = ?),?,?) RETURNING codigo; ");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setLong(2, movimentacao.getUnidade().getCodigo());
            for (Movimentacao mov : movimentacao.getMovimentacoes()) {
                stmt.setLong(3, mov.getPneu().getCodigo());
                stmt.setLong(4, mov.getPneu().getCodigo());
                stmt.setLong(5, movimentacao.getUnidade().getCodigo());
                stmt.setLong(6, mov.getPneu().getCodigo());
                stmt.setLong(7, movimentacao.getUnidade().getCodigo());
                stmt.setLong(8, mov.getPneu().getCodigo());
                stmt.setLong(9, movimentacao.getUnidade().getCodigo());
                stmt.setDouble(10, mov.getPneu().getVidaAtual());
                stmt.setString(11, mov.getObservacao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    mov.setCodigo(rSet.getLong("CODIGO"));
                    insertOrigem(conn, mov, movimentacao.getUnidade().getCodigo());
                    insertDestino(conn, mov, movimentacao.getUnidade().getCodigo());
                    fecharServicosPneu(conn, mov, movimentacao.getUnidade().getCodigo(), movimentacao.getColaborador().getCpf());
                    if (mov.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                        adicionaPneuVeiculo(conn, mov, movimentacao.getUnidade().getCodigo());
                    }
                    atualizaStatusPneu(conn, mov, movimentacao.getUnidade().getCodigo());
                }
            }
            return true;
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void removeOrigensVeiculo(ProcessoMovimentacao movimentacao, Connection conn) throws SQLException {
        for (Movimentacao mov : movimentacao.getMovimentacoes()) {
            OrigemVeiculo origem = (OrigemVeiculo) mov.getOrigem();
            if (mov.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                removePneuVeiculo(conn, movimentacao.getUnidade().getCodigo(), origem.getVeiculo().getPlaca(), mov.getPneu().getCodigo());
            }
        }
    }

    private void removePneuVeiculo(Connection conn, Long codUnidade, String placa, int codPneu) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE COD_UNIDADE = ? AND PLACA = ? AND COD_PNEU = ?");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setInt(3, codPneu);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao deletar o pneu do veiculo");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void validaMovimentacoes(List<Movimentacao> movimentacoes) throws OrigemDestinoInvalidaException {
        OrigemDestinoValidator origemDestinoValidator = new OrigemDestinoValidator();
        for (Movimentacao movimentacao : movimentacoes) {
            origemDestinoValidator.validate(movimentacao.getOrigem(), movimentacao.getDestino());
        }
    }

    private boolean insertOrigem(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_origem (cod_movimentacao, " +
                    "tipo_origem, placa, km_veiculo, posicao_pneu_origem) values (?,?,?,?,?)");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, movimentacao.getOrigem().getTipo());
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                stmt.setString(3, origemVeiculo.getVeiculo().getPlaca());
                stmt.setLong(4, origemVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(5, origemVeiculo.getPosicaoOrigemPneu());
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.BIGINT);
                stmt.setNull(5, Types.INTEGER);
            }
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.ANALISE)) {
                PneuDao pneuDao = new PneuDaoImpl();
                pneuDao.incrementaVida(conn, movimentacao.getPneu().getCodigo(), codUnidade);
            }
            return stmt.executeUpdate() == 0;
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private boolean insertDestino(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_destino(cod_movimentacao, " +
                    "tipo_destino, placa, km_veiculo, posicao_pneu_destino) values (?,?,?,?,?)");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, movimentacao.getDestino().getTipo());
            if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                stmt.setString(3, destinoVeiculo.getVeiculo().getPlaca());
                stmt.setLong(4, destinoVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(5, destinoVeiculo.getPosicaoDestinoPneu());
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.BIGINT);
                stmt.setNull(5, Types.INTEGER);
            }
            return stmt.executeUpdate() == 0;
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void fecharServicosPneu(Connection conn, Movimentacao movimentacao, Long codUnidade, Long cpfColaborador) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE afericao_manutencao SET\n" +
                    "  data_hora_resolucao = ?,\n" +
                    "  cpf_mecanico = ?,\n" +
                    "  km_momento_conserto = ? \n" +
                    "WHERE cod_pneu = ? and cod_unidade = ?");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(2, cpfColaborador);
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                stmt.setLong(3, origemVeiculo.getVeiculo().getKmAtual());
            } else if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                stmt.setLong(3, destinoVeiculo.getVeiculo().getKmAtual());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setLong(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.executeUpdate();
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private boolean adicionaPneuVeiculo(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO veiculo_pneu (placa, cod_pneu, cod_unidade, posicao) " +
                    "VALUES (?,?,?,?)");
            DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setString(1, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setLong(2, movimentacao.getPneu().getCodigo());
            stmt.setLong(3, codUnidade);
            stmt.setInt(4, destinoVeiculo.getPosicaoDestinoPneu());
            return stmt.executeUpdate() == 0;
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private boolean atualizaStatusPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PneuDaoImpl pneuDao = new PneuDaoImpl();
        return pneuDao.updateStatus(movimentacao.getPneu(), codUnidade, movimentacao.getDestino().getTipo(), conn);
    }
}
