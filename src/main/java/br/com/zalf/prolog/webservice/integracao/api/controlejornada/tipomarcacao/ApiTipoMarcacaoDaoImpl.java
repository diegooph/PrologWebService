package br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ApiMarcacaoCreator;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiTipoMarcacaoDaoImpl extends DatabaseConnection implements ApiTipoMarcacaoDao {
    @NotNull
    @Override
    public List<ApiTipoMarcacao> getTiposMarcacoes(@NotNull final String tokenIntegracao,
                                                   final boolean apenasTiposMarcacoesAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_MARCACAO_LISTA_TIPOS_MARCACOES(" +
                    "F_TOKEN_INTEGRACAO := ?, " +
                    "F_APENAS_TIPO_MARCACOES_ATIVAS := ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setBoolean(2, apenasTiposMarcacoesAtivos);
            rSet = stmt.executeQuery();
            final List<ApiTipoMarcacao> tiposMarcacoes = new ArrayList<>();
            while (rSet.next()) {
                tiposMarcacoes.add(ApiMarcacaoCreator.createTipoMarcacao(rSet));
            }
            return tiposMarcacoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
