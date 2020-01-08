package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcaModeloPneuDaoImpl extends DatabaseConnection implements ApiMarcaModeloPneuDao {
    @NotNull
    @Override
    public List<ApiMarcaPneu> getMarcasPneu(@NotNull final String tokenIntegracao,
                                            final boolean apenasMarcasPneuAtivas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_PNEU_LISTA_MARCAS_MODELOS_PNEUS_EMPRESA(?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setBoolean(2, apenasMarcasPneuAtivas);
            rSet = stmt.executeQuery();
            return ApiMarcaModeloCreator.createMarcasPneu(rSet);
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
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_PNEUS_EMPRESA(?, ?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codMarcaPneu);
            stmt.setBoolean(3, apenasModelosPneuAtivos);
            rSet = stmt.executeQuery();
            return ApiMarcaModeloCreator.createModelosPneu(rSet);
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
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_PNEU_LISTA_MARCAS_MODELOS_BANDA_EMPRESA(?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setBoolean(2, apenasMarcasBandaAtivas);
            rSet = stmt.executeQuery();
            return ApiMarcaModeloCreator.createMarcasBanda(rSet);
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
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_PNEU_LISTA_MODELOS_BANDAS_EMPRESA(?, ?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codMarcaBanda);
            stmt.setBoolean(3, apenasModelosBandaAtivos);
            rSet = stmt.executeQuery();
            return ApiMarcaModeloCreator.createModelosBanda(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
