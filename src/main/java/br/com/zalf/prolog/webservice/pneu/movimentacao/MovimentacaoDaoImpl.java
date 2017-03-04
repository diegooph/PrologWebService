package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.frota.pneu.movimentacao.Movimentacao;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoDaoImpl extends DatabaseConnection{

    public boolean insert (List<Movimentacao> movimentacoes, Long codUnidade) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("");

            for(Movimentacao movimentacao : movimentacoes){
//                setar stmt para salvar a movimentação
//                chamar método para salvar a origem
//                chamar método para salvar o destino
//                verificar se na origem ou no destino há um veículo, para atualizar os pneus vinculados
                int count = stmt.executeUpdate();
                if(count == 0){
                    conn.rollback();
                    return false;
                }
            }
        }finally {
            closeConnection(conn, stmt, null);
        }
        return false;
    }
}
