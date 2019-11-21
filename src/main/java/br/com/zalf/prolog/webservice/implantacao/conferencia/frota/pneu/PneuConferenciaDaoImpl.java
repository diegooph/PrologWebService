package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu;

import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model.PneuDadosTabelaImport;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuConferenciaDaoImpl implements PneuConferenciaDao {

    @Override
    public void importPlanilhaPneus(@NotNull final Long codEmpresa,
                                    @NotNull final Long codUnidade,
                                    @NotNull final String usuario,
                                    @NotNull final String jsonPlanilha) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            final PneuDadosTabelaImport pneuDadosTabelaImport = createDadosTabelaImport(codEmpresa, codUnidade, usuario);

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_PNEU_INSERE_PLANILHA_IMPORTACAO(" +
                    "F_COD_DADOS_AUTOR_IMPORT := ?," +
                    "F_NOME_TABELA_IMPORT := ?," +
                    "F_COD_UNIDADE   := ?," +
                    "F_JSON_PNEUS := ?);");
            stmt.setLong(1, pneuDadosTabelaImport.getCodDadosAutorImport());
            stmt.setString(2, pneuDadosTabelaImport.getNomeTabelaImport());
            stmt.setLong(3, codUnidade);
            final PGobject json = new PGobject();
            json.setType("jsonb");
            json.setValue(jsonPlanilha);
            stmt.setObject(4, json);
            rSet = stmt.executeQuery();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private static PneuDadosTabelaImport createDadosTabelaImport(@NotNull final Long codEmpresa,
                                                                 @NotNull final Long codUnidade,
                                                                 @NotNull final String usuario) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_PNEU_IMPORT_INSERE_DADOS_AUTOR(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_UNIDADE := ?," +
                    "F_USUARIO := ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, usuario);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final PneuDadosTabelaImport pneuDadosTabelaImport = new PneuDadosTabelaImport(
                        rSet.getLong("COD_DADOS_AUTOR_IMPORT"),
                        rSet.getString("NOME_TABELA_IMPORT"));
                return pneuDadosTabelaImport;
            } else {
                throw new SQLException("Erro criar tabela de import");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
