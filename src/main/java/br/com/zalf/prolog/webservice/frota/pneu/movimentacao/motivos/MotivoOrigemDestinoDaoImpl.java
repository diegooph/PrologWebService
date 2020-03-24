package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoVisualizacaoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoApp;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoOrigemDestinoDaoImpl extends DatabaseConnection implements MotivoOrigemDestinoDao {

    @Override
    @NotNull
    public Long insert(@NotNull final MotivoOrigemDestinoInsercao motivoOrigemDestinoInsercao,
                       @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_ORIGEM_DESTINO_INSERE(" +
                    "F_COD_MOTIVO := ?," +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_UNIDADE :=?," +
                    "F_ORIGEM := ?::origem_destino_type," +
                    "F_DESTINO := ?::origem_destino_type," +
                    "F_OBRIGATORIO := ?," +
                    "F_DATA_HORA_INSERCAO := ?," +
                    "F_TOKEN_AUTENTICACAO := ?)" +
                    "AS V_COD_MOTIVO_ORIGEM_DESTINO;");

            stmt.setLong(1, motivoOrigemDestinoInsercao.getCodMotivo());
            stmt.setLong(2, motivoOrigemDestinoInsercao.getCodEmpresa());
            stmt.setLong(3, motivoOrigemDestinoInsercao.getCodUnidade());
            stmt.setString(4, motivoOrigemDestinoInsercao.getOrigemMovimentacao().asString());
            stmt.setString(5, motivoOrigemDestinoInsercao.getDestinoMovimentacao().asString());
            stmt.setBoolean(6, true);
            stmt.setObject(7, Now.offsetDateTimeUtc());
            stmt.setString(8, TokenCleaner.getOnlyToken(tokenAutenticacao));

            rSet = stmt.executeQuery();

            while (rSet.next()) {
                return rSet.getLong("V_COD_MOTIVO_ORIGEM_DESTINO");
            }

        } finally {
            close(conn, stmt);
        }

        return null;
    }

    @Override
    public @NotNull MotivoOrigemDestinoVisualizacaoListagem getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                                   @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_ORIGEM_DESTINO_VISUALIZACAO(" +
                    "F_COD_MOTIVO_ORIGEM_DESTINO := ?," +
                    "F_TOKEN := ?);");
            stmt.setLong(1, codMotivoOrigemDestino);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return MotivoOrigemDestinoConverter.createMotivoOrigemDestinoVisualizacaoListagem(rSet);
            } else {
                throw new IllegalStateException("Nenhuma relação motivo, origem e destino foi encontrada com o código: " + codMotivoOrigemDestino);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public @NotNull List<MotivoOrigemDestinoVisualizacaoListagem> getMotivosOrigemDestino(@NotNull final Long codEmpresa,
                                                                                          @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_ORIGEM_DESTINO_LISTAGEM(" +
                    "F_COD_EMPRESA := ?," +
                    "F_TOKEN := ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));

            rSet = stmt.executeQuery();

            final List<MotivoOrigemDestinoVisualizacaoListagem> motivosOrigemDestino = new ArrayList();
            while (rSet.next()) {
                motivosOrigemDestino.add(MotivoOrigemDestinoConverter.createMotivoOrigemDestinoVisualizacaoListagem(rSet));
            }

            return motivosOrigemDestino;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public @NotNull MotivoOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(@NotNull final OrigemDestinoEnum origem,
                                                                                              @NotNull final OrigemDestinoEnum destino,
                                                                                              @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_GET_BY_ORIGEM_DESTINO(" +
                    "F_ORIGEM := ?::ORIGEM_DESTINO_TYPE," +
                    "F_DESTINO := ?::ORIGEM_DESTINO_TYPE," +
                    "F_COD_UNIDADE := ?);");
            stmt.setString(1, origem.asString());
            stmt.setString(2, destino.asString());
            stmt.setLong(3, codUnidade);

            rSet = stmt.executeQuery();

            final MotivoOrigemDestinoListagemMotivos origemDestino = new MotivoOrigemDestinoListagemMotivos(origem, destino);
            final List<MotivoVisualizacaoApp> motivos = new ArrayList();

            while (rSet.next()) {
                motivos.add(MotivoConverter.createMotivoListagemApp(rSet));
                origemDestino.setObrigatorio(rSet.getBoolean("OBRIGATORIO"));
            }

            origemDestino.setMotivos(motivos);

            return origemDestino;
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
