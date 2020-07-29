package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador;

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
 * Created on 29/07/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ColaboradorConferenciaDaoImpl implements ColaboradorConferenciaDao {

    @NotNull
    private ConferenciaDao dao = new ConferenciaDaoImpl();

    @Override
    public void importPlanilhaColaborador(@NotNull final Long codEmpresa,
                                          @NotNull final Long codUnidade,
                                          @NotNull final String usuario,
                                          @NotNull final String jsonPlanilha,
                                          @NotNull final TipoImport tipoImportColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            final ConferenciaDadosTabelaImport conferenciaDadosTabelaImport = dao.createDadosTabelaImport(codEmpresa,
                    codUnidade,
                    usuario,
                    tipoImportColaborador);

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_COLABORADOR_INSERE_PLANILHA_IMPORTACAO(" +
                    "F_COD_DADOS_AUTOR_IMPORT := ?," +
                    "F_NOME_TABELA_IMPORT := ?," +
                    "F_COD_EMPRESA   := ?," +
                    "F_COD_UNIDADE   := ?," +
                    "F_JSON_COLABORADORES := ?);");
            stmt.setLong(1, conferenciaDadosTabelaImport.getCodDadosAutorImport());
            stmt.setString(2, conferenciaDadosTabelaImport.getNomeTabelaImport());
            stmt.setLong(3, codEmpresa);
            stmt.setLong(4, codUnidade);
            final PGobject json = new PGobject();
            json.setType("jsonb");
            json.setValue(jsonPlanilha);
            stmt.setObject(5, json);
            rSet = stmt.executeQuery();
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
