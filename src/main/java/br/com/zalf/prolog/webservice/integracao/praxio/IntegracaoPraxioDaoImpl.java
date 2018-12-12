package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class IntegracaoPraxioDaoImpl extends DatabaseConnection implements IntegracaoPraxioDao {
    private static final long COD_EMPRESA_PICCOLOTUR = 11;

    @NotNull
    @Override
    public List<AfericaoIntegracaoPraxio> getAfericoesRealizadas(
            @NotNull final Long codUltimaAfericao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_EMPRESA(?, ?);");
            stmt.setLong(1, COD_EMPRESA_PICCOLOTUR);
            stmt.setLong(2, codUltimaAfericao);
            rSet = stmt.executeQuery();
            final List<AfericaoIntegracaoPraxio> afericoes = new ArrayList<>();
            while (rSet.next()) {
                afericoes.add(AfericaoIntegracaoPraxioConverter.convert(rSet));
            }
            return afericoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean verifyIfTokenIntegracaoExists(@NotNull final String tokenIntegracao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT TI.TOKEN_INTEGRACAO " +
                    "              FROM INTEGRACAO.TOKEN_INTEGRACAO TI " +
                    "              WHERE TI.TOKEN_INTEGRACAO = ?) AS EXISTE_TOKEN;");
            stmt.setString(1, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTE_TOKEN");
            } else {
                throw new SQLException(
                        "Não foi possível verifica a existencia do token de integração: " + tokenIntegracao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
