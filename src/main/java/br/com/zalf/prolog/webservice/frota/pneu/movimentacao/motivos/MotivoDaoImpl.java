package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoDaoImpl extends DatabaseConnection implements MotivoDao {

    @Override
    @NotNull
    public Long insert(@NotNull final MotivoInsercao motivoInsercao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_INSERE(" +
                    "F_COD_EMPRESA_MOTIVO := ?," +
                    "F_DESCRICAO_MOTIVO := ?," +
                    "F_ATIVO_MOTIVO := ?," +
                    "F_DATA_HORA_INSERCAO_MOTIVO := ?)" +
                    "AS F_COD_MOTIVO");

            stmt.setLong(1, motivoInsercao.getCodEmpresaMotivo());
            stmt.setString(2, motivoInsercao.getDescricaoMotivo());
            stmt.setBoolean(3, true);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            rSet = stmt.executeQuery();

            while (rSet.next()) {
                return rSet.getLong("F_COD_MOTIVO");
            }

        } finally {
            close(conn, stmt);

        }

        return null;
    }

    @Override
    public @NotNull MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull final Long codMotivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_VISUALIZACAO(" +
                    "F_COD_MOTIVO := ?)");
            stmt.setLong(1, codMotivo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return MotivoConverter.createMotivoVisualizacaoListagem(rSet);
            } else {
                throw new IllegalStateException("Nenhum motivo encontrado com o código: " + codMotivo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public @NotNull List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa) throws Throwable {
        return null;
    }

}
