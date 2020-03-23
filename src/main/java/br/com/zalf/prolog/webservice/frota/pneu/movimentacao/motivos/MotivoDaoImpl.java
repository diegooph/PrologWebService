package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoListagemApp;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoDaoImpl extends DatabaseConnection implements MotivoDao {

    @Override
    @NotNull
    public Long insert(@NotNull final MotivoInsercao motivoInsercao,
                       @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_INSERE(" +
                    "F_COD_EMPRESA_MOTIVO := ?," +
                    "F_DESCRICAO_MOTIVO := ?," +
                    "F_DATA_HORA_INSERCAO_MOTIVO := ?," +
                    "F_TOKEN_AUTENTICACAO := ?)" +
                    "AS V_COD_MOTIVO");

            stmt.setLong(1, motivoInsercao.getCodEmpresaMotivo());
            stmt.setString(2, motivoInsercao.getDescricaoMotivo());
            stmt.setObject(3, Now.offsetDateTimeUtc());
            stmt.setString(4, TokenCleaner.getOnlyToken(tokenAutenticacao));

            rSet = stmt.executeQuery();

            while (rSet.next()) {
                return rSet.getLong("V_COD_MOTIVO");
            }

        } finally {
            close(conn, stmt);

        }

        return null;
    }

    @Override
    @NotNull
    public MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull final Long codMotivo,
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
    @NotNull
    public List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                               @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_LISTAGEM(" +
                    "F_COD_EMPRESA := ?," +
                    "F_TOKEN := ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));

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

    @Override
    @Nullable
    public void update(@NotNull final MotivoEdicao motivoEdicao,
                       @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_MOTIVO_ATUALIZA(" +
                    "F_COD_MOTIVO := ?," +
                    "F_DESCRICAO_MOTIVO := ?," +
                    "F_DATA_ULTIMA_ALTERACAO := ?," +
                    "F_TOKEN_AUTENTICACAO := ?);");

            stmt.setLong(1, motivoEdicao.getCodMotivo());
            stmt.setString(2, motivoEdicao.getDescricaoMotivo());
            stmt.setObject(3, Now.offsetDateTimeUtc());
            stmt.setString(4, TokenCleaner.getOnlyToken(tokenAutenticacao));

            stmt.executeQuery();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    @Nullable
    public void delete(@NotNull final Long codMotivo, @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_MOTIVO_DELETA(" +
                    "F_COD_MOTIVO := ?," +
                    "F_DATA_ULTIMA_ALTERACAO := ?," +
                    "F_TOKEN_AUTENTICACAO := ?);");

            stmt.setLong(1, codMotivo);
            stmt.setObject(2, Now.offsetDateTimeUtc());
            stmt.setString(3, TokenCleaner.getOnlyToken(tokenAutenticacao));

            stmt.executeQuery();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public @NotNull List<MotivoListagemApp> getMotivosByOrigemAndDestino(@NotNull final OrigemDestinoEnum origem,
                                                                         @NotNull final OrigemDestinoEnum destino) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_GET_BY_ORIGEM_DESTINO(" +
                    "F_ORIGEM := ?," +
                    "F_DESTINO := ?)");
            stmt.setString(1, origem.asString());
            stmt.setString(2, destino.asString());

            rSet = stmt.executeQuery();

            final List<MotivoListagemApp> motivos = new ArrayList();
            while (rSet.next()) {
                motivos.add(MotivoConverter.createMotivoListagemApp(rSet));
            }

            return motivos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
