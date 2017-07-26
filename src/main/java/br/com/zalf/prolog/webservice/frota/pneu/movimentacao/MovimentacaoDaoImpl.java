package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;

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
                    "cod_pneu, sulco_interno, sulco_central_interno, sulco_central_externo, sulco_externo,  vida, observacao)\n " +
                    "VALUES (?,?,?,\n " +
                    "COALESCE ((select altura_sulco_interno from pneu where codigo = ? and cod_unidade = ?),0),\n " +
                    "COALESCE ((select altura_sulco_central_interno from pneu where codigo = ? and cod_unidade = ?),0),\n " +
                    "COALESCE ((select altura_sulco_central_externo from pneu where codigo = ? and cod_unidade = ?),0),\n " +
                    "COALESCE ((select altura_sulco_externo from pneu where codigo = ? and cod_unidade = ?),0),?,?) RETURNING codigo; ");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setLong(2, movimentacao.getUnidade().getCodigo());
            for (Movimentacao mov : movimentacao.getMovimentacoes()) {
                stmt.setString(3, mov.getPneu().getCodigo());
                stmt.setString(4, mov.getPneu().getCodigo());
                stmt.setLong(5, movimentacao.getUnidade().getCodigo());
                stmt.setString(6, mov.getPneu().getCodigo());
                stmt.setLong(7, movimentacao.getUnidade().getCodigo());
                stmt.setString(8, mov.getPneu().getCodigo());
                stmt.setLong(9, movimentacao.getUnidade().getCodigo());
                stmt.setString(10, mov.getPneu().getCodigo());
                stmt.setLong(11, movimentacao.getUnidade().getCodigo());
                stmt.setDouble(12, mov.getPneu().getVidaAtual());
                stmt.setString(13, mov.getObservacao());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    mov.setCodigo(rSet.getLong("CODIGO"));
                    insertOrigem(conn, mov, movimentacao.getUnidade().getCodigo());
                    insertDestino(conn, mov, movimentacao.getUnidade().getCodigo());
                    fecharServicosPneu(conn, mov, movimentacao.getUnidade().getCodigo(), movimentacao.getColaborador().getCpf());
                    if (mov.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                        adicionaPneuVeiculo(conn, mov, movimentacao.getUnidade().getCodigo());
                    }
                    // pneu voltou recapado, devemos incrementar a vida
                    if(mov.getOrigem().getTipo().equals(OrigemDestinoConstants.ANALISE) &&
                            mov.getDestino().getTipo().equals(OrigemDestinoConstants.ESTOQUE)){
                        PneuDao pneuDao = Injection.providePneuDao();
                        mov.getPneu().setVidaAtual(mov.getPneu().getVidaAtual()+1);
                        pneuDao.updateVida(conn, mov.getPneu(), movimentacao.getUnidade().getCodigo());
                        pneuDao.insertTrocaVidaPneu(mov.getPneu(), movimentacao.getUnidade().getCodigo(), conn);
                        pneuDao.updateSulcos(mov.getPneu(), movimentacao.getUnidade().getCodigo());
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
            if (mov.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                OrigemVeiculo origem = (OrigemVeiculo) mov.getOrigem();
                removePneuVeiculo(conn, movimentacao.getUnidade().getCodigo(), origem.getVeiculo().getPlaca(), mov.getPneu().getCodigo());
            }
        }
    }

    private void removePneuVeiculo(Connection conn, Long codUnidade, String placa, String codPneu) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE COD_UNIDADE = ? AND PLACA = ? AND COD_PNEU = ?");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placa);
            stmt.setString(3, codPneu);
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

    private void insertOrigem(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_origem (tipo_origem, cod_movimentacao, " +
                    "placa, km_veiculo, posicao_pneu_origem) values ((SELECT p.status\n" +
                    "FROM pneu p " +
                    "WHERE P.CODIGO = ? AND COD_UNIDADE = ? AND ? in (select p.status from pneu p WHERE p.codigo = ? and p.cod_unidade = ?)),?,?,?,?)");
            stmt.setString(1, movimentacao.getPneu().getCodigo());
            stmt.setLong(2, codUnidade);
            stmt.setString(3, movimentacao.getOrigem().getTipo());
            stmt.setString(4, movimentacao.getPneu().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, movimentacao.getCodigo());
            if (movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
                VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(origemVeiculo.getVeiculo().getPlaca(), origemVeiculo.getVeiculo().getKmAtual(), conn);
                stmt.setString(7, origemVeiculo.getVeiculo().getPlaca());
                stmt.setLong(8, origemVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(9, origemVeiculo.getPosicaoOrigemPneu());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.BIGINT);
                stmt.setNull(9, Types.INTEGER);
            }
            if(stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir a origem da movimentação");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void insertDestino(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO movimentacao_destino(cod_movimentacao, " +
                    "tipo_destino, placa, km_veiculo, posicao_pneu_destino) values (?,?,?,?,?)");
            stmt.setLong(1, movimentacao.getCodigo());
            stmt.setString(2, movimentacao.getDestino().getTipo());
            if (movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)) {
                DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(destinoVeiculo.getVeiculo().getPlaca(), destinoVeiculo.getVeiculo().getKmAtual(), conn);
                stmt.setString(3, destinoVeiculo.getVeiculo().getPlaca());
                stmt.setLong(4, destinoVeiculo.getVeiculo().getKmAtual());
                stmt.setInt(5, destinoVeiculo.getPosicaoDestinoPneu());
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.BIGINT);
                stmt.setNull(5, Types.INTEGER);
            }
            if(stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o destino da movimentação");
            }
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
            DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setString(1, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setString(2, movimentacao.getPneu().getCodigo());
            stmt.setLong(3, codUnidade);
            stmt.setInt(4, destinoVeiculo.getPosicaoDestinoPneu());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao vincular o pneu " + movimentacao.getPneu() + " ao veículo " + destinoVeiculo.getVeiculo().getPlaca());
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void atualizaStatusPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PneuDao pneuDao = Injection.providePneuDao();
        pneuDao.updateStatus(movimentacao.getPneu(), codUnidade, movimentacao.getDestino().getTipo(), conn);
    }
}
