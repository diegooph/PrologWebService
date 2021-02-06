package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaModeloBandaDaoImpl implements PneuMarcaModeloBandaDao {

    @NotNull
    @Override
    public Long insertMarcaBanda(@NotNull final PneuMarcaBandaInsercao marcaBanda) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_CADASTRA_MARCA_BANDA(" +
                    "F_COD_EMPRESA := ?," +
                    "F_MARCA_BANDA := ?) AS CODIGO;");
            stmt.setLong(1, marcaBanda.getCodEmpresa());
            stmt.setString(2, marcaBanda.getNome().trim());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao inserir marca de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long updateMarcaBanda(@NotNull final PneuMarcaBandaEdicao marcaBanda) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_EDITA_MARCA_BANDA(" +
                    "F_COD_MARCA_BANDA := ?," +
                    "F_NOME_MARCA_BANDA := ?) AS CODIGO;");
            stmt.setLong(1, marcaBanda.getCodigo());
            stmt.setString(2, marcaBanda.getNome());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao atualizar a marca de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<PneuMarcaBandaListagem> getListagemMarcasBanda(@NotNull final Long codEmpresa,
                                                               final boolean comModelos,
                                                               final boolean incluirMarcasNaoUtilizadas)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            if (comModelos) {
                conn = getConnection();
                stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELOS_BANDA_LISTAGEM(" +
                        "F_COD_EMPRESA                   := ?," +
                        "F_COD_MARCA                     := ?," +
                        "F_INCLUIR_MARCAS_NAO_UTILIZADAS := ?);");
                stmt.setLong(1, codEmpresa);
                stmt.setNull(2, SqlType.BIGINT.asIntTypeJava());
                stmt.setBoolean(3, incluirMarcasNaoUtilizadas);
                rSet = stmt.executeQuery();
                PneuMarcaBandaListagem marca = null;
                List<PneuModeloBandaListagem> modelos;
                final List<PneuMarcaBandaListagem> marcas = new ArrayList<>();
                while (rSet.next()) {
                    if (marca == null) {
                        marca = PneuMarcaModeloBandaConverter.createPneuMarcaBandaListagem(rSet);
                        marcas.add(marca);
                        if (rSet.getLong("COD_MODELO_BANDA") > 0) {
                            modelos = new ArrayList<>();
                            modelos.add(PneuMarcaModeloBandaConverter.createPneuModeloBandaListagem(rSet));
                            marca.setModelos(modelos);
                        }
                    } else {
                        if (marca.getCodigo() == rSet.getLong("COD_MARCA_BANDA")) {
                            marca.getModelos().add(PneuMarcaModeloBandaConverter.createPneuModeloBandaListagem(rSet));
                        } else {
                            marca = PneuMarcaModeloBandaConverter.createPneuMarcaBandaListagem(rSet);
                            marcas.add(marca);
                            if (rSet.getLong("COD_MODELO_BANDA") > 0) {
                                modelos = new ArrayList<>();
                                modelos.add(PneuMarcaModeloBandaConverter.createPneuModeloBandaListagem(rSet));
                                marca.setModelos(modelos);
                            }
                        }
                    }
                }
                return marcas;
            } else {
                conn = getConnection();
                stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MARCAS_BANDA_LISTAGEM(" +
                        "F_COD_EMPRESA := ? )");
                stmt.setLong(1, codEmpresa);
                rSet = stmt.executeQuery();
                final List<PneuMarcaBandaListagem> marcas = new ArrayList<>();
                while (rSet.next()) {
                    marcas.add(PneuMarcaModeloBandaConverter.createPneuMarcaBandaListagem(rSet));
                }
                return marcas;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public PneuMarcaBandaVisualizacao getMarcaBanda(@NotNull final Long codMarca) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MARCA_BANDA_VISUALIZACAO(" +
                    "F_COD_MARCA := ? )");
            stmt.setLong(1, codMarca);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return PneuMarcaModeloBandaConverter.createPneuMarcaBandaVisualizacao(rSet);
            } else {
                throw new Throwable("Erro ao buscar marca de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("Duplicates")
    public Long insertModeloBanda(@NotNull final PneuModeloBandaInsercao modeloBanda) throws Throwable {
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
            stmt.setLong(1, modeloBanda.getCodEmpresa());
            stmt.setLong(2, modeloBanda.getCodMarca());
            stmt.setString(3, modeloBanda.getNome());
            stmt.setInt(4, modeloBanda.getQuantidadeSulcos());
            stmt.setDouble(5, modeloBanda.getAlturaSulcos());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao inserir modelo de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("Duplicates")
    public Long updateModeloBanda(@NotNull final PneuModeloBandaEdicao modeloBanda) throws Throwable {
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
            stmt.setLong(1, modeloBanda.getCodEmpresa());
            stmt.setLong(2, modeloBanda.getCodMarca());
            stmt.setLong(3, modeloBanda.getCodigo());
            stmt.setString(4, modeloBanda.getNome());
            stmt.setInt(5, modeloBanda.getQuantidadeSulcos());
            stmt.setDouble(6, modeloBanda.getAlturaSulcos());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao atualizar o modelo de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<PneuModeloBandaListagem> getListagemModelosBandas(@NotNull final Long codEmpresa,
                                                                  @Nullable final Long codMarca)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELOS_BANDA_LISTAGEM(" +
                    "F_COD_EMPRESA                   := ?," +
                    "F_COD_MARCA                     := ?," +
                    "F_INCLUIR_MARCAS_NAO_UTILIZADAS := ?);");
            stmt.setLong(1, codEmpresa);
            StatementUtils.bindValueOrNull(stmt, 2, codMarca, SqlType.BIGINT);
            stmt.setBoolean(3, false);
            rSet = stmt.executeQuery();
            final List<PneuModeloBandaListagem> marcasModelosBandas = new ArrayList<>();
            while (rSet.next()) {
                marcasModelosBandas.add(PneuMarcaModeloBandaConverter.createPneuModeloBandaListagem(rSet));
            }
            return marcasModelosBandas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public PneuModeloBandaVisualizacao getModeloBanda(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELO_BANDA_VISUALIZACAO(" +
                    "F_COD_MODELO_BANDA := ? ) ");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return PneuMarcaModeloBandaConverter.createPneuModeloBandaVisualizacao(rSet);
            } else {
                throw new Throwable("Erro ao buscar marca e modelo de banda");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}