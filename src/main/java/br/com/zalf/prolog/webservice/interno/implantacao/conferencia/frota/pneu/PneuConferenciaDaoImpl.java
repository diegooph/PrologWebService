package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.pneu;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.ConferenciaDao;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.ConferenciaDaoImpl;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.ConferenciaDadosTabelaImport;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuConferenciaDaoImpl implements PneuConferenciaDao {
    @NotNull
    private ConferenciaDao dao = new ConferenciaDaoImpl();

    @Override
    public void importPlanilhaPneus(@NotNull final Long codEmpresa,
                                    @NotNull final Long codUnidade,
                                    @NotNull final String usuario,
                                    @NotNull final String jsonPlanilha,
                                    @NotNull final TipoImport tipoImportPneu) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            final ConferenciaDadosTabelaImport conferenciaDadosTabelaImport = dao.createDadosTabelaImport(codEmpresa, codUnidade, usuario, tipoImportPneu);

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_PNEU_INSERE_PLANILHA_IMPORTACAO(" +
                    "F_COD_DADOS_AUTOR_IMPORT := ?," +
                    "F_NOME_TABELA_IMPORT := ?," +
                    "F_COD_UNIDADE   := ?," +
                    "F_JSON_PNEUS := ?);");
            stmt.setLong(1, conferenciaDadosTabelaImport.getCodDadosAutorImport());
            stmt.setString(2, conferenciaDadosTabelaImport.getNomeTabelaImport());
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
}
