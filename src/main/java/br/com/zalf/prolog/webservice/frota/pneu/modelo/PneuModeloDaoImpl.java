package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloListagem;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloVisualizacao;
import org.jetbrains.annotations.NotNull;

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
public final class PneuModeloDaoImpl implements PneuModeloDao {

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
    public List<PneuModeloListagem> getListagemMarcasModelosPneu(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(" +
                    "F_COD_EMPRESA := ?);");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<PneuModeloListagem> marcas = new ArrayList<>();
            while (rSet.next()) {
                marcas.add(PneuModeloConverter.createPneuModeloListagem(rSet));
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
                return PneuModeloConverter.createModeloPneu(rSet);
            } else {
                throw new Throwable("Erro ao buscar modelo pelo código: " + codModelo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}