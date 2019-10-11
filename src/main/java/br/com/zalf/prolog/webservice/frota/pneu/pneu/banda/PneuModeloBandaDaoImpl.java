package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.*;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloBandaDaoImpl implements PneuModeloBandaDao {

    @Override
    public List<PneuMarcaBandas> listagemMarcasModelosBandas(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<PneuMarcaBandas> marcas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MARCA_BANDA_BY_COD_EMPRESA(" +
                    "F_COD_EMPRESA := ? )");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final PneuMarcaBandas marca = new PneuMarcaBandas();
                marca.setCodigo(rSet.getLong("CODIGO"));
                marca.setNome(rSet.getString("NOME"));
                marca.setModelos(getModelosBanda(conn, codEmpresa, marca.getCodigo()));
                marcas.add(marca);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return marcas;
    }

    @Override
    public PneuMarcaBanda getMarcaModeloBanda(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MARCA_BANDA_BY_COD_MODELO_BANDA(" +
                    "F_COD_MODELO_BANDA := ? )");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                final PneuMarcaBanda marca = new PneuMarcaBanda();
                marca.setCodigo(rSet.getLong("COD_MARCA_BANDA"));
                marca.setNome(rSet.getString("NOME_MARCA_BANDA"));
                marca.setModelo(PneuBandaConverter.createModeloBanda(rSet));
                return marca;
            }else{
                throw new SQLException("Erro ao buscar marca e modelo de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Long insertMarcaBanda(@NotNull final PneuMarcaBandas marca,
                                 @NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_CADASTRA_MARCA_BANDA(" +
                    "F_COD_EMPRESA := ?," +
                    "F_MARCA_BANDA := ?) AS CODIGO;");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, marca.getNome().trim().toLowerCase().replaceAll("\\s+", " "));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            }else{
                throw new SQLException("Erro ao inserir marca de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean updateMarcaBanda(@NotNull final PneuMarcaBandas marca,
                                    @NotNull final  Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_EDITA_MARCA_BANDA(" +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_MARCA_BANDA := ?" +
                    "F_MARCA_BANDA := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, marca.getCodigo());
            stmt.setString(3, marca.getNome());
            if (stmt.executeUpdate() == 0) {
                throw new Throwable("Erro ao atualizar a marca da banca: " + marca.getCodigo());
            }
        } finally {
            close(conn, stmt);
        }
        return true;
    }

    @NotNull
    private List<PneuModeloBandaVisualizacao> getModelosBanda(@NotNull final Connection conn,
                                                              @NotNull final Long codEmpresa,
                                                              @NotNull final Long codMarcaBanda) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<PneuModeloBandaVisualizacao> modelos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELOS_BANDA_BY_COD_EMPRESA_COD_MARCA(" +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_MARCA_BANDA := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codMarcaBanda);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final PneuModeloBandaVisualizacao pneuModeloBanda = new PneuModeloBandaVisualizacao(
                        rSet.getLong("CODIGO"),
                        rSet.getString("NOME"),
                        rSet.getInt("QT_SULCOS"),
                        rSet.getDouble("ALTURA_SULCOS"),
                        null);
                modelos.add(pneuModeloBanda);
            }
        } finally {
            close(stmt, rSet);
        }
        return modelos;
    }

    @Override
    public Long insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_CADASTRA_MODELO_BANDA(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_MARCA_BANDA := ?," +
                    "F_NOME_MODELO_BANDA := ?," +
                    "F_QTD_SULCOS := ?," +
                    "F_ALTURA_SULCOS := ?) AS CODIGO;");
            stmt.setLong(1, pneuModeloBandaInsercao.getCodEmpresa());
            stmt.setLong(2, pneuModeloBandaInsercao.getCodMarca());
            stmt.setString(3, pneuModeloBandaInsercao.getNome());
            stmt.setInt(4, pneuModeloBandaInsercao.getQuantidadeSulcos());
            stmt.setDouble(5, pneuModeloBandaInsercao.getAlturaSulcos());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            }else{
                throw new Throwable("Erro ao inserir modelo de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Long updateModeloBanda(@NotNull final PneuModeloBandaEdicao pneuModeloBandaEdicao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_PNEU_EDITA_MODELO_BANDA(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_MARCA_BANDA := ?, " +
                    "F_COD_MODELO_BANDA := ?, " +
                    "F_NOME_MODELO_BANDA := ?," +
                    "F_QTD_SULCOS := ?," +
                    "F_ALTURA_SULCOS := ?) AS CODIGO;");
            stmt.setLong(1, pneuModeloBandaEdicao.getCodEmpresa());
            stmt.setLong(2, pneuModeloBandaEdicao.getCodMarca());
            stmt.setLong(3, pneuModeloBandaEdicao.getCodigo());
            stmt.setString(4, pneuModeloBandaEdicao.getNome());
            stmt.setInt(5, pneuModeloBandaEdicao.getQuantidadeSulcos());
            stmt.setDouble(6, pneuModeloBandaEdicao.getAlturaSulcos());

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            }else{
                throw new Throwable("Erro ao atualizar o modelo de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}