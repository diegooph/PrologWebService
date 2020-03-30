package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
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
public class MotivoRetiradaDaoImpl extends DatabaseConnection implements MotivoRetiradaDao {

    @Override
    @NotNull
    public Long insert(@NotNull final MotivoRetiradaInsercao motivoRetiradaInsercao,
                       @NotNull final Long codigoColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_INSERE(" +
                    "F_COD_EMPRESA_MOTIVO := ?," +
                    "F_DESCRICAO_MOTIVO := ?," +
                    "F_ATIVO_MOTIVO := ?," +
                    "F_COD_AUXILIAR_MOTIVO := ?," +
                    "F_DATA_HORA_INSERCAO_MOTIVO := ?," +
                    "F_COD_COLABORADOR_AUTENTICADO := ?)" +
                    "AS V_COD_MOTIVO");

            stmt.setLong(1, motivoRetiradaInsercao.getCodEmpresaMotivoRetirada());
            stmt.setString(2, motivoRetiradaInsercao.getDescricaoMotivoRetirada());
            stmt.setBoolean(3, motivoRetiradaInsercao.isAtivoMotivoRetirada());
            stmt.setString(4, motivoRetiradaInsercao.getCodAuxiliarMotivoRetirada());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setLong(6, codigoColaborador);

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
    public MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
                                                        @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_VISUALIZACAO(" +
                    "F_COD_MOTIVO := ?," +
                    "F_TOKEN := ?)");
            stmt.setLong(1, codMotivo);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return MotivoRetiradaConverter.createMotivoRetiradaVisualizacao(rSet);
            } else {
                throw new IllegalStateException("Nenhum motivo encontrado com o código: " + codMotivo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public List<MotivoRetiradaListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                           @NotNull final boolean apenasAtivos,
                                                           @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_LISTAGEM(" +
                    "F_COD_EMPRESA := ?," +
                    "F_APENAS_ATIVOS := ?," +
                    "F_TOKEN := ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setBoolean(2, apenasAtivos);
            stmt.setString(3, TokenCleaner.getOnlyToken(tokenAutenticacao));

            rSet = stmt.executeQuery();

            final List<MotivoRetiradaListagem> motivos = new ArrayList();
            while (rSet.next()) {
                motivos.add(MotivoRetiradaConverter.createMotivoRetiradaListagem(rSet));
            }

            return motivos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @Nullable
    public void update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao,
                       @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_MOTIVO_RETIRADA_ATUALIZA(" +
                    "F_COD_MOTIVO := ?," +
                    "F_DESCRICAO_MOTIVO := ?::CITEXT," +
                    "F_ATIVO_MOTIVO := ?," +
                    "F_COD_AUXILIAR_MOTIVO := ?," +
                    "F_DATA_ULTIMA_ALTERACAO := ?," +
                    "F_TOKEN_AUTENTICACAO := ?);");

            stmt.setLong(1, motivoRetiradaEdicao.getCodMotivoRetirada());
            stmt.setString(2, motivoRetiradaEdicao.getDescricaoMotivoRetirada());
            stmt.setBoolean(3, motivoRetiradaEdicao.isAtivoMotivoRetirada());
            stmt.setString(4, motivoRetiradaEdicao.getCodAuxiliarMotivoRetirada());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setString(6, TokenCleaner.getOnlyToken(tokenAutenticacao));

            stmt.executeQuery();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public @NotNull List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivo(@NotNull final Long codMotivoRetirada,
                                                                               @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_HISTORICO_LISTAGEM(" +
                    "F_COD_MOTIVO := ?," +
                    "F_TOKEN := ?)");
            stmt.setLong(1, codMotivoRetirada);
            stmt.setString(2, TokenCleaner.getOnlyToken(tokenAutenticacao));

            rSet = stmt.executeQuery();

            final List<MotivoRetiradaHistoricoListagem> historicoMotivo = new ArrayList();
            while (rSet.next()) {
                historicoMotivo.add(MotivoRetiradaConverter.createMotivoRetiradaHistoricoListagem(rSet));
            }

            return historicoMotivo;
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
