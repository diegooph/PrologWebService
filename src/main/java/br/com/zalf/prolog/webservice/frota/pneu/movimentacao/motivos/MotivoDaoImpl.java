package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

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
            stmt.setObject(4, Now.offsetDateTimeUtc());

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
    public @NotNull MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull final Long codMotivo,
                                                                 @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_VISUALIZACAO(" +
                    "F_COD_MOTIVO := ?," +
                    "F_TOKEN := ?)");
            stmt.setLong(1, codMotivo);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));
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
    public @NotNull List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                                        @Nullable final Boolean apenasAtivos,
                                                                        @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_LISTAGEM(" +
                    "F_COD_EMPRESA := ?," +
                    "F_TOKEN := ?," +
                    "F_APENAS_ATIVOS := ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));
            bindValueOrNull(stmt, 3, apenasAtivos, SqlType.BOOLEAN);

            rSet = stmt.executeQuery();

            final List<MotivoVisualizacaoListagem> motivos = new ArrayList();
            while (rSet.next()) {
                motivos.add(MotivoConverter.createMotivoVisualizacaoListagem(rSet));
            }

            return motivos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
