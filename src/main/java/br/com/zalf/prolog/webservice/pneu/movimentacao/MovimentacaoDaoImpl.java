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

    public boolean insert(ProcessoMovimentacao movimentacao, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        validaMovimentacoes(movimentacao.getMovimentacoes());
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO movimentacao(cod_unidade, data_hora, cpf_responsavel, observacao) " +
                    "VALUES (?,?,?,?) RETURNING codigo;");
            stmt.setLong(1, codUnidade);
            stmt.setTimestamp(2, DateUtils.toTimestamp(movimentacao.getData()));
            stmt.setLong(3, movimentacao.getColaborador().getCpf());
            stmt.setString(4, movimentacao.getObservacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                movimentacao.setCodigo(rSet.getLong("CODIGO"));
                insertValores(movimentacao, codUnidade, conn);
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

    private boolean insertValores(ProcessoMovimentacao movimentacao, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_valores (cod_movimentacao, cod_unidade, \n " +
                    "cod_pneu, sulco_interno, sulco_central, sulco_externo,  vida, observacao)\n " +
                    "VALUES (?,?,?,\n " +
                    "(select altura_sulco_interno from pneu where codigo = ? and cod_unidade = ?),\n " +
                    "(select altura_sulco_central from pneu where codigo = ? and cod_unidade = ?),\n " +
                    "(select altura_sulco_externo from pneu where codigo = ? and cod_unidade = ?),?,?); ");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setLong(2, codUnidade);
            for (Movimentacao mov : movimentacao.getMovimentacoes()) {
                stmt.setLong(3, mov.getPneu().getCodigo());
                stmt.setLong(4, mov.getPneu().getCodigo());
                stmt.setLong(5, codUnidade);
                stmt.setLong(6, mov.getPneu().getCodigo());
                stmt.setLong(7, codUnidade);
                stmt.setLong(8, mov.getPneu().getCodigo());
                stmt.setLong(9, codUnidade);
                stmt.setDouble(10, mov.getPneu().getVidaAtual());
                stmt.setString(11, mov.getObservacao());
                int count = stmt.executeUpdate();
                if (count > 0) {
                    insertOrigem(conn, mov, codUnidade);
                    insertDestino(conn, mov, codUnidade);
                    fecharServicosPneu(conn, mov, codUnidade, movimentacao.getColaborador().getCpf());
                    movimentaPneu(conn, mov, codUnidade);
                    atualizaStatusPneu(conn, mov, codUnidade);
                    return true;
                }
            }
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

    private boolean movimentaPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        // verifica se tem algum veículo envolvido na movimentação
        if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
            // Destino = veiculo, ou seja, pneu foi adicionado
            return adicionaPneuVeiculo(conn, movimentacao, codUnidade);
        } else if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
            // Origem = veiculo, ou seja, pneu foi removido
            OrigemVeiculo origem = (OrigemVeiculo) movimentacao.getOrigem();
            return removePneuVeiculo(conn, movimentacao.getPneu().getCodigo(), origem.getPosicaoOrigemPneu(), codUnidade,
                    origem.getVeiculo().getPlaca());
        }
        return true;
    }

    private boolean adicionaPneuVeiculo(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO veiculo_pneu (placa, cod_pneu, cod_unidade, posicao) " +
                    "VALUES (?,?,?,?)");
            DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            // antes de inserir devo remover o pneu do veículo usando a posição de inserção do novo
            removePneuVeiculo(conn, 0, destinoVeiculo.getPosicaoDestinoPneu(), codUnidade, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setString(1, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setLong(2, movimentacao.getPneu().getCodigo());
            stmt.setLong(3, codUnidade);
            stmt.setInt(4, destinoVeiculo.getPosicaoDestinoPneu());
            return stmt.executeUpdate() == 0;
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private boolean removePneuVeiculo(Connection conn, long codPneu, int posicao, Long codUnidade, String placa) throws SQLException {
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("DELETE FROM veiculo_pneu \n" +
                "WHERE placa = ? and cod_unidade = ? and (cod_pneu = ? or posicao = ?) ");
        stmt.setString(1, placa);
        stmt.setLong(2, codUnidade);
        stmt.setLong(3, codPneu);
        stmt.setInt(4, posicao);
        int count = stmt.executeUpdate();
        closeConnection(null, stmt, null);
        return count == 0;
    }

    private boolean atualizaStatusPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PneuDaoImpl pneuDao = new PneuDaoImpl();
        return pneuDao.updateStatus(movimentacao.getPneu(), codUnidade, movimentacao.getDestino().getTipo(), conn);
    }
}
