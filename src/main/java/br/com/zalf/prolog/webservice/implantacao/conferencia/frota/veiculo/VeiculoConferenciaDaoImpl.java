package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo._model.VeiculoDadosTabelaImport;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoConferenciaDaoImpl implements VeiculoConferenciaDao {

    @Override
    public void importPlanilhaVeiculos(@NotNull final Long codEmpresa,
                                       @NotNull final Long codUnidade,
                                       @NotNull final String usuario,
                                       @NotNull final String jsonPlanilha) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            final VeiculoDadosTabelaImport veiculoDadosTabelaImport = createDadosTabelaImport(codEmpresa, codUnidade, usuario);

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(" +
                    "F_COD_DADOS_AUTOR_IMPORT := ?," +
                    "F_NOME_TABELA_IMPORT := ?," +
                    "F_COD_UNIDADE   := ?," +
                    "F_JSON_VEICULOS := ?);");
            stmt.setLong(1, veiculoDadosTabelaImport.getCodDadosAutorImport());
            stmt.setString(2, veiculoDadosTabelaImport.getNomeTabelaImport());
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

    private static VeiculoDadosTabelaImport createDadosTabelaImport(@NotNull final Long codEmpresa,
                                                                    @NotNull final Long codUnidade,
                                                                    @NotNull final String usuario) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_VEICULO_IMPORT_INSERE_DADOS_AUTOR(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_UNIDADE := ?," +
                    "F_USUARIO := ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, usuario);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final VeiculoDadosTabelaImport veiculoDadosTabelaImport = new VeiculoDadosTabelaImport(
                        rSet.getLong("COD_DADOS_AUTOR_IMPORT"),
                        rSet.getString("NOME_TABELA_IMPORT"));
                return veiculoDadosTabelaImport;
            } else {
                throw new SQLException("Erro criar tabela de import");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}