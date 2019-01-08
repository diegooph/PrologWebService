package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Icone;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 20/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TipoMarcacaoDaoImpl extends DatabaseConnection implements TipoMarcacaoDao {

    @NotNull
    @Override
    public Long insertTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao,
                                   @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO INTERVALO_TIPO(NOME, ICONE, TEMPO_RECOMENDADO_MINUTOS, " +
                    "TEMPO_ESTOURO_MINUTOS, HORARIO_SUGERIDO, COD_UNIDADE, TIPO_JORNADA, ATIVO) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, TRUE) RETURNING CODIGO;");
            stmt.setString(1, tipoMarcacao.getNome());
            stmt.setString(2, tipoMarcacao.getIcone().getNomeIcone());
            stmt.setLong(3, tipoMarcacao.getTempoRecomendado().toMinutes());
            stmt.setLong(4, tipoMarcacao.getTempoLimiteEstouro().toMinutes());
            stmt.setTime(5, tipoMarcacao.getHorarioSugerido());
            stmt.setLong(6, tipoMarcacao.getUnidade().getCodigo());
            if (tipoMarcacao.isTipoJornada()) {
                stmt.setBoolean(7, true);
            } else {
                stmt.setNull(7, SqlType.BOOLEAN.asIntTypeJava());
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                tipoMarcacao.setCodigo(rSet.getLong("CODIGO"));
                associaCargosTipoMarcacao(tipoMarcacao, conn);
                // Avisamos o listener que um tipo de marcação FOI INCLUÍDO.
                listener.onTiposMarcacaoChanged(conn, tipoMarcacao.getUnidade().getCodigo());
                // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
                conn.commit();
                return tipoMarcacao.getCodigo();
            } else {
                throw new SQLException("Erro ao inserir o Tipo de Intervalo de nome: " + tipoMarcacao.getNome());
            }
        } catch (Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao,
                                   @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE INTERVALO_TIPO " +
                    "SET NOME = ?, ICONE = ?, TEMPO_RECOMENDADO_MINUTOS = ?, TEMPO_ESTOURO_MINUTOS = ?, " +
                    "HORARIO_SUGERIDO = ?, TIPO_JORNADA = ? WHERE COD_UNIDADE = ? AND CODIGO = ? AND ATIVO = TRUE;");
            stmt.setString(1, tipoMarcacao.getNome());
            stmt.setString(2, tipoMarcacao.getIcone().getNomeIcone());
            stmt.setLong(3, tipoMarcacao.getTempoRecomendado().toMinutes());
            stmt.setLong(4, tipoMarcacao.getTempoLimiteEstouro().toMinutes());
            stmt.setTime(5, tipoMarcacao.getHorarioSugerido());
            if (tipoMarcacao.isTipoJornada()) {
                stmt.setBoolean(6, true);
            } else {
                stmt.setNull(6, SqlType.BOOLEAN.asIntTypeJava());
            }
            stmt.setLong(7, tipoMarcacao.getUnidade().getCodigo());
            stmt.setLong(8, tipoMarcacao.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o Tipo de Marcação de código: " + tipoMarcacao.getCodigo());
            }
            associaCargosTipoMarcacao(tipoMarcacao, conn);
            // Avisamos o listener que um tipo de marcação mudou.
            listener.onTiposMarcacaoChanged(conn, tipoMarcacao.getUnidade().getCodigo());

            // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
            conn.commit();
        } catch (Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, null);
        }
    }

    @NotNull
    @Override
    public List<TipoMarcacao> getTiposMarcacoes(@NotNull final Long codUnidade,
                                                final boolean apenasAtivos,
                                                final boolean withCargos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PUBLIC.FUNC_MARCACAO_GET_TIPOS_MARCACOES(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setBoolean(2, apenasAtivos);
            rSet = stmt.executeQuery();
            final List<TipoMarcacao> tipos = new ArrayList<>();
            while (rSet.next()) {
                tipos.add(createTipoMarcacao(conn, rSet, withCargos));
            }
            return tipos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public TipoMarcacao getTipoMarcacao(@NotNull final Long codTipoMarcacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "IT.CODIGO AS CODIGO_TIPO_INTERVALO, " +
                    "IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE, " +
                    "IT.NOME AS NOME_TIPO_INTERVALO, " +
                    "IT.COD_UNIDADE, " +
                    "IT.ATIVO, " +
                    "IT.HORARIO_SUGERIDO, " +
                    "IT.ICONE, " +
                    "IT.TEMPO_ESTOURO_MINUTOS, " +
                    "IT.TEMPO_RECOMENDADO_MINUTOS, " +
                    "IT.TIPO_JORNADA " +
                    "FROM INTERVALO_TIPO_CARGO ITC JOIN VIEW_INTERVALO_TIPO IT ON ITC.COD_UNIDADE = IT.COD_UNIDADE " +
                    "AND ITC.COD_TIPO_INTERVALO = IT.CODIGO " +
                    " WHERE IT.CODIGO = ?;");
            stmt.setLong(1, codTipoMarcacao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createTipoMarcacao(conn, rSet, true);
            } else {
                throw new SQLException("Nenhum tipo de marcação encontrado com o código: " + codTipoMarcacao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateStatusAtivoTipoMarcacao(@NotNull final Long codTipoMarcacao,
                                              @NotNull final TipoMarcacao tipoMarcacao,
                                              @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE INTERVALO_TIPO " +
                    "SET ATIVO = ? WHERE CODIGO = ? RETURNING COD_UNIDADE;");
            stmt.setBoolean(1, tipoMarcacao.isAtivo());
            stmt.setLong(2, codTipoMarcacao);
            rSet = stmt.executeQuery();

            if (rSet.next()) {
                final Long codUnidade = rSet.getLong("COD_UNIDADE");
                // Avisamos o listener que um tipo de marcação mudou.
                listener.onTiposMarcacaoChanged(conn, codUnidade);

                // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
                conn.commit();
            } else {
                throw new SQLException("Erro ao inativar o Tipo de Marcação de código: " + codTipoMarcacao);
            }
        } catch (final Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void associaCargosTipoMarcacao(@NotNull final TipoMarcacao tipoIntervalo,
                                           @NotNull final Connection conn) throws Throwable {
        deleteCargosTipoMarcacao(
                tipoIntervalo.getUnidade().getCodigo(),
                tipoIntervalo.getCodigo(),
                conn);
        insertCargosTipoMarcacao(
                tipoIntervalo.getUnidade().getCodigo(),
                tipoIntervalo.getCodigo(),
                tipoIntervalo.getCargos(),
                conn);
    }

    private void deleteCargosTipoMarcacao(@NotNull final Long codUnidade,
                                          @NotNull final Long codTipoIntervalo,
                                          @NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM INTERVALO_TIPO_CARGO WHERE COD_UNIDADE = ? AND " +
                    "COD_TIPO_INTERVALO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            stmt.executeUpdate();
            // Não precisamos verificar se o delete afetou alguma linha pois o tipo de marcação pode não ter nenhum cargo
            // vinculado.
        } finally {
            close(stmt);
        }
    }

    private void insertCargosTipoMarcacao(@NotNull final Long codUnidade,
                                          @NotNull final Long codTipoIntervalo,
                                          @NotNull final List<Cargo> cargos,
                                          @NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO_TIPO_CARGO VALUES (?,?,?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            for (final Cargo cargo : cargos) {
                stmt.setLong(3, cargo.getCodigo());
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao vincular cargo ao tipo de marcação");
                }
            }
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private TipoMarcacao createTipoMarcacao(@NotNull final Connection conn,
                                            @NotNull final ResultSet rSet,
                                            final boolean withCargos) throws Throwable {
        final TipoMarcacao tipoMarcacao = new TipoMarcacao();
        tipoMarcacao.setCodigo(rSet.getLong("CODIGO_TIPO_INTERVALO"));
        tipoMarcacao.setCodigoPorUnidade(rSet.getLong("CODIGO_TIPO_INTERVALO_POR_UNIDADE"));
        tipoMarcacao.setNome(rSet.getString("NOME_TIPO_INTERVALO"));
        final Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        tipoMarcacao.setUnidade(unidade);
        tipoMarcacao.setAtivo(rSet.getBoolean("ATIVO"));
        tipoMarcacao.setHorarioSugerido(rSet.getTime("HORARIO_SUGERIDO"));
        tipoMarcacao.setIcone(Icone.fromString(rSet.getString("ICONE")));
        tipoMarcacao.setTempoLimiteEstouro(Duration.ofMinutes(rSet.getLong("TEMPO_ESTOURO_MINUTOS")));
        tipoMarcacao.setTempoRecomendado(Duration.ofMinutes(rSet.getLong("TEMPO_RECOMENDADO_MINUTOS")));
        tipoMarcacao.setTipoJornada(rSet.getBoolean("TIPO_JORNADA"));
        if (withCargos) {
            tipoMarcacao.setCargos(getCargosByTipoMarcacao(conn, tipoMarcacao));
        }
        return tipoMarcacao;
    }

    @NotNull
    private List<Cargo> getCargosByTipoMarcacao(@NotNull final Connection conn,
                                                @NotNull final TipoMarcacao tipoMarcacao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT F.* FROM " +
                    "  INTERVALO_TIPO_CARGO ITC JOIN UNIDADE U ON U.CODIGO = ITC.COD_UNIDADE " +
                    "JOIN FUNCAO F ON F.cod_emprESA = U.cod_empresa AND F.codigo = ITC.COD_CARGO " +
                    "WHERE ITC.COD_TIPO_INTERVALO = ? and ITC.COD_UNIDADE = ?;");
            stmt.setLong(1, tipoMarcacao.getCodigo());
            stmt.setLong(2, tipoMarcacao.getUnidade().getCodigo());
            rSet = stmt.executeQuery();
            final List<Cargo> cargos = new ArrayList<>();
            while (rSet.next()) {
                cargos.add(new Cargo(rSet.getLong("CODIGO"), rSet.getString("NOME")));
            }
            return cargos;
        } finally {
            close(stmt, rSet);
        }
    }
}