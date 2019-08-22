package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatusVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuDaoImpl extends DatabaseConnection implements ApiPneuDao {
    @Override
    public void atualizaStatusPneus(
            @NotNull final String tokenIntegracao,
            @NotNull final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            for (final ApiPneuAlteracaoStatus pneuAlteracaoStatus : pneusAtualizacaoStatus) {
                stmt.setLong(1, pneuAlteracaoStatus.getCodigoPneuSistemaIntegrado());
                stmt.setString(2, pneuAlteracaoStatus.getCodigoPneuCliente());
                stmt.setLong(3, pneuAlteracaoStatus.getCodUnidadePneu());
                stmt.setString(4, pneuAlteracaoStatus.getCpfColaboradorAlteracaoStatus());
                stmt.setObject(5, pneuAlteracaoStatus.getDataHoraAlteracaoStatus());
                stmt.setString(6, pneuAlteracaoStatus.getStatusPneu().asString());
                if (pneuAlteracaoStatus.getStatusPneu().equals(ApiStatusPneu.EM_USO)) {
                    final ApiPneuAlteracaoStatusVeiculo pneuAlteracaoStatusveiculo =
                            (ApiPneuAlteracaoStatusVeiculo) pneuAlteracaoStatus;
                    stmt.setString(7, pneuAlteracaoStatusveiculo.getPlacaVeiculoPneuAplicado());
                    stmt.setInt(8, pneuAlteracaoStatusveiculo.getPosicaoVeiculoPneuAplicado());
                } else {
                    stmt.setNull(7, SqlType.VARCHAR.asIntTypeJava());
                    stmt.setNull(8, SqlType.INTEGER.asIntTypeJava());
                }
                stmt.setString(9, tokenIntegracao);
                stmt.addBatch();
            }
            final int[] ints = stmt.executeBatch();
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
