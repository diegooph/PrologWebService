package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ApiCadastroPneuDaoImpl extends DatabaseConnection implements ApiCadastroPneuDao {
    @Override
    public Long insertPneuCadastro(@NotNull final String tokenIntegracao,
                                   @NotNull final ApiPneuCadastro pneuCadastro) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(" +
                    " F_COD_UNIDADE_VEICULO_ALOCADO   := ?," +
                    " F_PLACA_VEICULO_CADASTRADO      := ?," +
                    " F_KM_ATUAL_VEICULO_CADASTRADO   := ?," +
                    " F_COD_MODELO_VEICULO_CADASTRADO := ?," +
                    " F_COD_TIPO_VEICULO_CADASTRADO   := ?," +
                    " F_DATA_HORA_VEICULO_CADASTRO    := ?," +
                    " F_TOKEN_INTEGRACAO              := ?) AS COD_VEICULO_PROLOG;");
            stmt.setObject(6, Now.localDateTimeUtc());
            stmt.setString(7, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoProlog = rSet.getLong("COD_VEICULO_PROLOG");
                if (codVeiculoProlog <= 0) {
                    throw new SQLException("Erro na function de inserir veículo, não atualizou as tabelas");
                }
            } else {
                throw new SQLException("Erro ao inserir um veículo do Globus no ProLog");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
