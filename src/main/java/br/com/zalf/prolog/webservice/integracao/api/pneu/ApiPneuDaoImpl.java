package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuDaoImpl extends DatabaseConnection implements ApiPneuDao {
    @NotNull
    @Override
    public List<ApiMarcaPneu> getMarcasPneu(@NotNull final String tokenIntegracao,
                                            final boolean apenasMarcasPneuAtivas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            final List<ApiMarcaPneu> marcasPneu = new ArrayList<>();
            while (rSet.next()) {

            }
            return marcasPneu;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ApiModeloPneu> getModelosPneu(@NotNull final String tokenIntegracao,
                                              @NotNull final Long codMarcaPneu,
                                              final boolean apenasModelosPneuAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            final List<ApiModeloPneu> modeloPneus = new ArrayList<>();
            while (rSet.next()) {

            }
            return modeloPneus;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ApiMarcaBanda> getMarcasBanda(@NotNull final String tokenIntegracao,
                                              final boolean apenasMarcasBandaAtivas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            final List<ApiMarcaBanda> marcasBanda = new ArrayList<>();
            while (rSet.next()) {

            }
            return marcasBanda;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ApiModeloBanda> getModelosBanda(@NotNull final String tokenIntegracao,
                                                @NotNull final Long codMarcaBanda,
                                                final boolean apenasModelosBandaAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            final List<ApiModeloBanda> modelosBanda = new ArrayList<>();
            while (rSet.next()) {

            }
            return modelosBanda;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
