package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.pneu.movimentacao.Movimentacao;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoConstants;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoValidator;
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

    public boolean insert(List<Movimentacao> movimentacoes, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        validaMovimentacoes(movimentacoes);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO movimentacao_pneu (cod_unidade, data_hora, cpf_responsavel," +
                    " cod_pneu, sulco_interno, sulco_central, sulco_externo,  vida, observacao) " +
                    "VALUES (?,?,?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, codUnidade);
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            for (Movimentacao movimentacao : movimentacoes) {
                stmt.setLong(3, movimentacao.getColaborador().getCpf());
                stmt.setLong(4, movimentacao.getPneu().getCodigo());
                stmt.setDouble(5, movimentacao.getPneu().getSulcoAtual().getInterno());
                stmt.setDouble(6, movimentacao.getPneu().getSulcoAtual().getCentral());
                stmt.setDouble(7, movimentacao.getPneu().getSulcoAtual().getExterno());
                stmt.setDouble(8, movimentacao.getPneu().getVidaAtual());
                stmt.setString(9, movimentacao.getObservacao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    movimentacao.setCodigo(rSet.getLong("CODIGO"));
                    insertOrigem(conn, movimentacao, codUnidade);
                    insertDestino(conn, movimentacao, codUnidade);
                    fecharServicosPneu(conn, movimentacao, codUnidade);
                    movimentaPneu(conn, movimentacao, codUnidade);
                    atualizaStatusPneu(conn, movimentacao, codUnidade);
                    conn.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
        return false;
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
            stmt = conn.prepareStatement("INSERT INTO movimentacao_pneu_origem (cod_movimentacao, cod_unidade, " +
                    "tipo_origem, placa, km_veiculo, posicao_pneu_origem) values (?,?,?,?,?,?)");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                stmt.setString(4, origemVeiculo.getVeiculo().getPlaca());
                stmt.setLong(5, origemVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(6, origemVeiculo.getPosicaoOrigemPneu());
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.BIGINT);
                stmt.setNull(6, Types.INTEGER);
            }
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.RECAPAGEM)) {
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
            stmt = conn.prepareStatement("INSERT INTO movimentacao_pneu_destino (cod_movimentacao, cod_unidade, " +
                    "tipo_destino, placa, km_veiculo, posicao_pneu_origem) values (?,?,?,?,?,?)");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getDestino().getTipo());
            if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                stmt.setString(4, destinoVeiculo.getVeiculo().getPlaca());
                stmt.setLong(5, destinoVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(6, destinoVeiculo.getPosicaoDestinoPneu());
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.BIGINT);
                stmt.setNull(6, Types.INTEGER);
            }
            return stmt.executeUpdate() == 0;
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void fecharServicosPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE afericao_manutencao SET\n" +
                    "  data_hora_resolucao = ?,\n" +
                    "  cpf_mecanico = ?,\n" +
                    "  km_momento_conserto = ? \n" +
                    "WHERE cod_pneu = ? and cod_unidade = ?");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(2, movimentacao.getColaborador().getCpf());
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

    private boolean movimentaPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        // verifica se tem algum veículo envolvido na movimentação
        if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
            // Destino = veiculo, ou seja, pneu foi adicionado
            return adicionaPneuVeiculo(conn, movimentacao, codUnidade);
        } else if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
            // Origem = veiculo, ou seja, pneu foi removido
            return removePneuVeiculo(conn, movimentacao, codUnidade);
        }
        return true;
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

    private boolean removePneuVeiculo(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("DELETE FROM veiculo_pneu \n" +
                "WHERE placa = ? and cod_pneu = ? and cod_unidade = ?");
        OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
        stmt.setString(1, origemVeiculo.getVeiculo().getPlaca());
        stmt.setLong(2, movimentacao.getPneu().getCodigo());
        stmt.setLong(3, codUnidade);
        int count = stmt.executeUpdate();
        closeConnection(null, stmt, null);
        return count == 0;
    }

    private boolean atualizaStatusPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PneuDaoImpl pneuDao = new PneuDaoImpl();
        return pneuDao.updateStatus(movimentacao.getPneu(), codUnidade, movimentacao.getDestino().getTipo(), conn);
    }
}
