package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoDaoImpl extends DatabaseConnection implements ApiMarcacaoDao {
    @NotNull
    @Override
    public List<ApiMarcacao> getMarcacoesRealizadas(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimaMarcacaoSincronizada) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_MARCACAO_LISTA_MARCACOES_REALIZADAS(" +
                            "F_TOKEN_INTEGRACAO := ?, " +
                            "F_COD_ULTIMA_MARCACAO_SINCRONIZADA := ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codUltimaMarcacaoSincronizada);
            rSet = stmt.executeQuery();
            final List<ApiMarcacao> marcacoes = new ArrayList<>();
            while (rSet.next()) {
                marcacoes.add(ApiMarcacaoCreator.createMarcacao(rSet));
            }
            return marcacoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
