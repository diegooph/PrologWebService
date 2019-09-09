package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ApiMarcacaoCreator;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiAjusteMarcacaoDaoImpl extends DatabaseConnection implements ApiAjusteMarcacaoDao {
    @NotNull
    @Override
    public List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimoAjusteMarcacaoSincronizado) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_MARCACAO_LISTA_AJUSTES_MARCACOES_REALIZADOS(" +
                            "F_TOKEN_INTEGRACAO := ?," +
                            "F_COD_ULTIMO_AJUSTE_MARCACAO_SINCRONIZADO := ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codUltimoAjusteMarcacaoSincronizado);
            rSet = stmt.executeQuery();
            final List<ApiAjusteMarcacao> ajustesMarcacao = new ArrayList<>();
            while (rSet.next()) {
                ajustesMarcacao.add(ApiMarcacaoCreator.createAjusteMarcacao(rSet));
            }
            return ajustesMarcacao;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
