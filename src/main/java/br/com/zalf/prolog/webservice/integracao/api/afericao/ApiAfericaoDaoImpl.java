package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.AfericaoRealizada;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
final class ApiAfericaoDaoImpl extends DatabaseConnection implements ApiAfericaoDao {

    @NotNull
    @Override
    public List<AfericaoRealizada> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                          @NotNull final Long codUltimaAfericao) throws Throwable {
        final List<AfericaoRealizada> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_REALIZADAS_EMPRESA(?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codUltimaAfericao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                afericoes.add(AfericaoConverter.convert(rSet));
            }
            return afericoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
