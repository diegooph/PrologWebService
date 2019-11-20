package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.*;
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
public final class PneuMarcaModeloDaoImpl implements PneuMarcaModeloDao {

    @NotNull
    @Override
    public List<PneuMarcaListagem> getListagemMarcasPneu(@NotNull final Long codEmpresa,
                                                         final boolean comModelos,
                                                         final boolean incluirMarcasNaoUtilizadas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            if (comModelos) {
                conn = getConnection();
                stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(" +
                        "F_COD_EMPRESA                   := ?," +
                        "F_COD_MARCA                     := ?," +
                        "F_INCLUIR_MARCAS_NAO_UTILIZADAS := ?);");
                stmt.setLong(1, codEmpresa);
                stmt.setNull(2, SqlType.BIGINT.asIntTypeJava());
                stmt.setBoolean(3, incluirMarcasNaoUtilizadas);
                rSet = stmt.executeQuery();
                PneuMarcaListagem marca = null;
                List<PneuModeloListagem> modelos;
                final List<PneuMarcaListagem> marcas = new ArrayList<>();
                while (rSet.next()) {
                    if (marca == null) {
                        marca = PneuMarcaModeloConverter.createPneuMarcaListagem(rSet);
                        marcas.add(marca);
                        if (rSet.getLong("COD_MODELO_PNEU") > 0) {
                            modelos = new ArrayList<>();
                            modelos.add(PneuMarcaModeloConverter.createPneuModeloListagem(rSet));
                            marca.setModelos(modelos);
                        }
                    } else {
                        if (marca.getCodigo() == rSet.getLong("COD_MARCA_PNEU")) {
                            marca.getModelos().add(PneuMarcaModeloConverter.createPneuModeloListagem(rSet));
                        } else {
                            marca = PneuMarcaModeloConverter.createPneuMarcaListagem(rSet);
                            marcas.add(marca);
                            if (rSet.getLong("COD_MODELO_PNEU") > 0) {
                                modelos = new ArrayList<>();
                                modelos.add(PneuMarcaModeloConverter.createPneuModeloListagem(rSet));
                                marca.setModelos(modelos);
                            }
                        }
                    }
                }
                return marcas;
            } else {
                conn = getConnection();
                stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MARCAS_PNEU_LISTAGEM(" +
                        "F_COD_EMPRESA                   := ?," +
                        "F_INCLUIR_MARCAS_NAO_UTILIZADAS := ?);");
                stmt.setLong(1, codEmpresa);
                stmt.setBoolean(2, incluirMarcasNaoUtilizadas);
                rSet = stmt.executeQuery();
                final List<PneuMarcaListagem> marcas = new ArrayList<>();
                while (rSet.next()) {
                    marcas.add(PneuMarcaModeloConverter.createPneuMarcaListagem(rSet));
                }
                return marcas;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("Duplicates")
    public Long insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_CADASTRA_MODELO_PNEU(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_MARCA_PNEU := ?," +
                    "F_NOME_MODELO_PNEU := ?," +
                    "F_QTD_SULCOS := ?," +
                    "F_ALTURA_SULCOS := ?) AS CODIGO;");
            stmt.setLong(1, pneuModeloInsercao.getCodEmpresa());
            stmt.setLong(2, pneuModeloInsercao.getCodMarca());
            stmt.setString(3, pneuModeloInsercao.getNome());
            stmt.setInt(4, pneuModeloInsercao.getQuantidadeSulcos());
            stmt.setDouble(5, pneuModeloInsercao.getAlturaSulcos());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao inserir o modelo do pneu ou modelo já existente");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("Duplicates")
    public Long updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_PNEU_EDITA_MODELO_PNEU(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_MARCA_PNEU := ?, " +
                    "F_COD_MODELO_PNEU := ?, " +
                    "F_NOME_MODELO_PNEU := ?," +
                    "F_QTD_SULCOS := ?," +
                    "F_ALTURA_SULCOS := ?) AS CODIGO;");
            stmt.setLong(1, pneuModeloEdicao.getCodEmpresa());
            stmt.setLong(2, pneuModeloEdicao.getCodMarca());
            stmt.setLong(3, pneuModeloEdicao.getCodigo());
            stmt.setString(4, pneuModeloEdicao.getNome());
            stmt.setInt(5, pneuModeloEdicao.getQuantidadeSulcos());
            stmt.setDouble(6, pneuModeloEdicao.getAlturaSulcos());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao atualizar o modelo de banda código: " + pneuModeloEdicao.getCodigo());
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<PneuModeloListagem> getListagemModelosPneu(@NotNull final Long codEmpresa,
                                                           @Nullable final Long codMarca) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(" +
                    "F_COD_EMPRESA                   := ?," +
                    "F_COD_MARCA                     := ?," +
                    "F_INCLUIR_MARCAS_NAO_UTILIZADAS := ?);");
            stmt.setLong(1, codEmpresa);
            StatementUtils.bindValueOrNull(stmt, 2, codMarca, SqlType.BIGINT);
            stmt.setBoolean(3, false);
            rSet = stmt.executeQuery();
            final List<PneuModeloListagem> marcas = new ArrayList<>();
            while (rSet.next()) {
                marcas.add(PneuMarcaModeloConverter.createPneuModeloListagem(rSet));
            }
            return marcas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELO_PNEU_VISUALIZACAO(" +
                    "F_COD_MODELO := ? );");
            stmt.setLong(1, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return PneuMarcaModeloConverter.createModeloPneu(rSet);
            } else {
                throw new Throwable("Erro ao buscar modelo pelo código: " + codModelo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}