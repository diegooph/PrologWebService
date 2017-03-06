package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.pneu.movimentacao.Movimentacao;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoConstants;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoValidator;
import br.com.zalf.prolog.frota.pneu.movimentacao.destino.DestinoVeiculo;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDaoImpl;

import java.sql.*;
import java.util.Date;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoDaoImpl extends DatabaseConnection{

    public boolean insert (Movimentacao movimentacao, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        OrigemDestinoValidator origemDestinoValidator = new OrigemDestinoValidator(movimentacao.getOrigem(),
                movimentacao.getDestino());
        origemDestinoValidator.validate();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO movimentacao_pneu (cod_unidade, data_hora, cpf_responsavel," +
                    " cod_pneu, sulco_interno, sulco_central, sulco_externo,  vida, observacao) " +
                    "VALUES (?,?,?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, codUnidade);
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
                stmt.setLong(3, movimentacao.getColaborador().getCpf());
                stmt.setLong(4, movimentacao.getPneu().getCodigo());
                stmt.setDouble(5, movimentacao.getPneu().getSulcoAtual().getInterno());
                stmt.setDouble(6, movimentacao.getPneu().getSulcoAtual().getCentral());
                stmt.setDouble(7, movimentacao.getPneu().getSulcoAtual().getExterno());
                stmt.setDouble(8, movimentacao.getPneu().getVidaAtual());
                stmt.setString(9, movimentacao.getObservacao());
                rSet = stmt.executeQuery();
                if(rSet.next()){
                    insertOrigem(conn, movimentacao, codUnidade);
                    insertDestino(conn, movimentacao, codUnidade);
                    fecharServicosPneu(conn, movimentacao, codUnidade);
                    movimentaPneu(conn, movimentacao, codUnidade);
                    PneuDaoImpl pneuDao = new PneuDaoImpl();
                    pneuDao.updateStatus(movimentacao.getPneu(), codUnidade, movimentacao.getDestino().getTipo(), conn);
                    atualizaStatusPneu(conn, movimentacao, codUnidade);
                }
//                validar Origem e Destino
//                setar stmt para salvar a movimentação - ok
//                chamar método para salvar a origem - ok
//                chamar método para salvar o destino - ok
//                fechar qualquer serviço aberto para o pneu - ok
//                verificar se na origem ou no destino há um veículo, para atualizar os pneus vinculados - ok
//                atualizar o status do Pneu na tabela Pneu - ok
                int count = stmt.executeUpdate();
                if(count == 0){
                    conn.rollback();
                    return false;
                }
                conn.commit();
        }catch(SQLException e) {
            conn.rollback();
            throw e;
        }

        finally {
            closeConnection(conn, stmt, null);
        }
        return false;
    }

    private boolean insertOrigem(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt;
        stmt = conn.prepareStatement("INSERT INTO movimentacao_pneu_origem (cod_movimentacao, cod_unidade, " +
                "tipo_origem, placa, km_veiculo, posicao_pneu_origem) values (?,?,?,?,?,?)");
        stmt.setLong(1, movimentacao.getCodigo());
        stmt.setLong(2, codUnidade);
        stmt.setString(3, movimentacao.getOrigem().getTipo());
        if(movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)){
            OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
            stmt.setString(4, origemVeiculo.getVeiculo().getPlaca());
            stmt.setLong(5, origemVeiculo.getVeiculo().getKmAtual());
            stmt.setInt(6, origemVeiculo.getPosicaoOrigemPneu());
        }else{
            stmt.setNull(4, Types.VARCHAR);
            stmt.setNull(5, Types.BIGINT);
            stmt.setNull(6, Types.INTEGER);
        }
        closeConnection(null, stmt, null);
        return stmt.executeUpdate() == 0;
    }

    private boolean insertDestino(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt;
        stmt = conn.prepareStatement("INSERT INTO movimentacao_pneu_destino (cod_movimentacao, cod_unidade, " +
                "tipo_origem, placa, km_veiculo, posicao_pneu_origem) values (?,?,?,?,?,?)");
        stmt.setLong(1, movimentacao.getCodigo());
        stmt.setLong(2, codUnidade);
        stmt.setString(3, movimentacao.getOrigem().getTipo());
        if(movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)){
            DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setString(4, destinoVeiculo.getVeiculo().getPlaca());
            stmt.setLong(5, destinoVeiculo.getVeiculo().getKmAtual());
            stmt.setInt(6, destinoVeiculo.getPosicaoDestinoPneu());
        }else{
            stmt.setNull(4, Types.VARCHAR);
            stmt.setNull(5, Types.BIGINT);
            stmt.setNull(6, Types.INTEGER);
        }
        int count = stmt.executeUpdate();
        closeConnection(null, stmt, null);
        return count == 0;
    }

    private boolean fecharServicosPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt;
        stmt = conn.prepareStatement("UPDATE afericao_manutencao SET\n" +
                "  data_hora_resolucao = ?,\n" +
                "  cpf_mecanico = ?,\n" +
                "  km_momento_conserto = ?,\n" +
                "WHERE cod_pneu = ? and cod_unidade = ?");
        stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
        stmt.setLong(2, movimentacao.getColaborador().getCpf());
        if(movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)){
            OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
            stmt.setLong(3, origemVeiculo.getVeiculo().getKmAtual());
        }else if(movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)){
            DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setLong(3, destinoVeiculo.getVeiculo().getKmAtual());
        }else{
            stmt.setNull(3, Types.INTEGER);
        }
        stmt.setLong(4, movimentacao.getPneu().getCodigo());
        stmt.setLong(5, codUnidade);
        int count = stmt.executeUpdate();
        closeConnection(null, stmt, null);
        return count == 0;
    }

    private boolean movimentaPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("DELETE FROM veiculo_pneu \n" +
                "WHERE placa = ? and cod_pneu = ? and cod_unidade = ?");
        if(movimentacao.getOrigem().getTipo().equals(OrigemDestinoConstants.VEICULO)){
            OrigemVeiculo origemVeiculo = (OrigemVeiculo) movimentacao.getOrigem();
            stmt.setString(1, origemVeiculo.getVeiculo().getPlaca());
        }else if(movimentacao.getDestino().getTipo().equals(OrigemDestinoConstants.VEICULO)){
            DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
            stmt.setString(1, destinoVeiculo.getVeiculo().getPlaca());
        }else{
            return true;
        }
        stmt.setLong(2, movimentacao.getPneu().getCodigo());
        stmt.setLong(3, codUnidade);
        int count = stmt.executeUpdate();
        closeConnection(null, stmt, null);
        return count == 0;
    }

    private boolean atualizaStatusPneu(Connection conn, Movimentacao movimentacao, Long codUnidade) throws SQLException{
        PneuDaoImpl pneuDao = new PneuDaoImpl();
        return pneuDao.updateStatus(movimentacao.getPneu(), codUnidade, movimentacao.getDestino().getTipo(), conn);
    }
}
