package br.com.zalf.prolog.webservice.implantacao.conferencia;

import br.com.zalf.prolog.webservice.implantacao.conferencia._model.ConferenciaDadosTabelaImport;
import br.com.zalf.prolog.webservice.implantacao.conferencia._model.TipoImport;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model.PneuDadosTabelaImport;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 13/12/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ConferenciaDaoImpl implements ConferenciaDao {

    @Override
    public ConferenciaDadosTabelaImport createDadosTabelaImport(@NotNull final Long codEmpresa,
                                                                @NotNull final Long codUnidade,
                                                                @NotNull final String usuario,
                                                                @NotNull final TipoImport tipoImport) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_IMPORT_INSERE_DADOS_AUTOR(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_UNIDADE := ?," +
                    "F_USUARIO := ?," +
                    "F_TIPO_IMPORT := ?)");

            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, usuario);
            stmt.setString(4, tipoImport.toString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final ConferenciaDadosTabelaImport conferenciaDadosTabelaImport = new ConferenciaDadosTabelaImport(
                        rSet.getLong("COD_DADOS_AUTOR_IMPORT"),
                        rSet.getString("NOME_TABELA_IMPORT"));
                return conferenciaDadosTabelaImport;
            } else {
                throw new SQLException("Erro criar tabela de import");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
