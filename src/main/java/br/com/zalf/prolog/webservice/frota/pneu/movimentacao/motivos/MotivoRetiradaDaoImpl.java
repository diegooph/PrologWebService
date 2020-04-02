package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoRetiradaDaoImpl extends DatabaseConnection implements MotivoRetiradaDao {

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
                    "F_COD_EMPRESA_MOTIVO => ?," +
                    "F_DESCRICAO_MOTIVO => ?," +
                    "F_ATIVO_MOTIVO => ?," +
                    "F_COD_AUXILIAR_MOTIVO => ?," +
                    "F_DATA_HORA_INSERCAO_MOTIVO => ?," +
                    "F_COD_COLABORADOR_AUTENTICADO => ?)" +
                    "AS COD_MOTIVO");
            stmt.setLong(1, motivoRetiradaInsercao.getCodEmpresaMotivoRetirada());
            stmt.setString(2, motivoRetiradaInsercao.getDescricaoMotivoRetirada());
            stmt.setBoolean(3, motivoRetiradaInsercao.isAtivoMotivoRetirada());
            stmt.setString(4, motivoRetiradaInsercao.getCodAuxiliarMotivoRetirada());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setLong(6, codigoColaborador);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_MOTIVO");
            } else {
                throw new IllegalStateException("Erro ao inserir motivo de movimento!");
            }
        } finally {
            close(conn, stmt, rSet);

        }
    }

    @Override
    @NotNull
    public MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
                                                        @NotNull final ZoneId timeZone) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_VISUALIZACAO(" +
                    "F_COD_MOTIVO => ?," +
                    "F_TIME_ZONE => ?)");
            stmt.setLong(1, codMotivo);
            stmt.setString(2, timeZone.toString());
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
                                                           final boolean apenasAtivos,
                                                           @NotNull final ZoneId timeZone) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_LISTAGEM(" +
                    "F_COD_EMPRESA => ?," +
                    "F_APENAS_ATIVOS => ?," +
                    "F_TIME_ZONE => ?)");
            stmt.setLong(1, codEmpresa);
            stmt.setBoolean(2, apenasAtivos);
            stmt.setString(3, timeZone.toString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<MotivoRetiradaListagem> motivos = new ArrayList<>();
                do {
                    motivos.add(MotivoRetiradaConverter.createMotivoRetiradaListagem(rSet));
                } while (rSet.next());
                return motivos;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao,
                       @NotNull final Long codColaboradorUpdate) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_MOTIVO_RETIRADA_ATUALIZA(" +
                    "F_COD_MOTIVO => ?," +
                    "F_DESCRICAO_MOTIVO => ?::CITEXT," +
                    "F_ATIVO_MOTIVO => ?," +
                    "F_COD_AUXILIAR_MOTIVO => ?," +
                    "F_DATA_ULTIMA_ALTERACAO => ?," +
                    "F_COD_COLABORADOR_ALTERACAO => ?);");
            stmt.setLong(1, motivoRetiradaEdicao.getCodMotivoRetirada());
            stmt.setString(2, motivoRetiradaEdicao.getDescricaoMotivoRetirada());
            stmt.setBoolean(3, motivoRetiradaEdicao.isAtivoMotivoRetirada());
            stmt.setString(4, motivoRetiradaEdicao.getCodAuxiliarMotivoRetirada());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setLong(6, codColaboradorUpdate);
            stmt.executeQuery();
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivo(@NotNull final Long codMotivoRetirada,
                                                                      @NotNull final ZoneId timeZone)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_HISTORICO_LISTAGEM(" +
                    "F_COD_MOTIVO => ?," +
                    "F_TIME_ZONE => ?)");
            stmt.setLong(1, codMotivoRetirada);
            stmt.setString(2, timeZone.toString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<MotivoRetiradaHistoricoListagem> historicoMotivo = new ArrayList<>();
                do {
                    historicoMotivo.add(MotivoRetiradaConverter.createMotivoRetiradaHistoricoListagem(rSet));
                } while (rSet.next());
                return historicoMotivo;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
