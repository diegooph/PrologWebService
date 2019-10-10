package test.br.com.zalf.prolog.webservice.routines;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 12/7/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class CorrecaoVinculoVeiculoPneu extends DatabaseConnection {

    public void run() throws  SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("SELECT VP.COD_PNEU\n" +
                    "FROM veiculo_pneu VP\n" +
                    "JOIN PNEU P ON P.codigo = VP.cod_pneu AND P.cod_unidade = VP.cod_unidade\n" +
                    "WHERE P.status != 'EM_USO' AND VP.cod_unidade = 1");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                statement = connection.prepareStatement("UPDATE PNEU P SET STATUS = 'EM_USO' WHERE P.CODIGO = ?");
                statement.setString(1, resultSet.getString("COD_PNEU"));
                final int count = statement.executeUpdate();
                if (count == 0) {
                    throw new SQLException("Erro ao atualizar status do pneu");
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            closeConnection(connection, statement, resultSet);
        }
    }
}