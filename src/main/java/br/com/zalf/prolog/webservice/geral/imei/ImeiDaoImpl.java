package br.com.zalf.prolog.webservice.geral.imei;

import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.geral.imei.model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ImeiDaoImpl extends DatabaseConnection implements ImeiDao {

    @NotNull
    @Override
    public List<MarcaCelularSelecao> getMarcasCelular() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_GERAL_GET_MARCAS_CELULAR();");
            rSet = stmt.executeQuery();
            final List<MarcaCelularSelecao> marcas = new ArrayList<>();
            while (rSet.next()) {
                marcas.add(ImeiConverter.createMarcaCelularSelecao(rSet));
            }
            return marcas;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}