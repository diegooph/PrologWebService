package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ControleIntervaloDaoImpl extends DatabaseConnection implements ControleIntervaloDao {

    public ControleIntervaloDaoImpl() {

    }

    @Override
    public void insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            // Se a marcação já existir, nós não tentamos inserir novamente e simplemente não fazemos nada. Isso garante
            // um retorno OK para o app e assim a marcação será colocada como sincronizada. É importante tratar esse
            // cenário pois o app pode tentar sincronizar uma marcação, ela ser inserida com sucesso, mas a conexão
            // com o servidor se perder nesse meio tempo, aí o app acha, erroneamente, que a marcação ainda não foi
            // sincronizada.
            if (!marcacaoIntervaloJaExiste(intervaloMarcacao, conn)) {
                internalInsertMarcacaoIntervalo(intervaloMarcacao, conn);
            }
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Nullable
    @Override
    public IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                               @NotNull final Long cpf,
                                                               @NotNull final Long codTipoIntervalo)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  I.CODIGO                          AS CODIGO, " +
                    "  I.CODIGO_MARCACAO_POR_UNIDADE     AS COD_MARCACAO_POR_UNIDADE, " +
                    "  I.COD_UNIDADE                     AS COD_UNIDADE, " +
                    "  I.COD_TIPO_INTERVALO              AS COD_TIPO_INTERVALO, " +
                    "  I.CPF_COLABORADOR                 AS CPF_COLABORADOR, " +
                    "  C.DATA_NASCIMENTO                 AS DATA_NASCIMENTO_COLABORADOR, " +
                    "  I.DATA_HORA AT TIME ZONE ?        AS DATA_HORA, " +
                    "  I.TIPO_MARCACAO                   AS TIPO_MARCACAO, " +
                    "  I.FONTE_DATA_HORA                 AS FONTE_DATA_HORA, " +
                    "  I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO, " +
                    "  I.JUSTIFICATIVA_ESTOURO           AS JUSTIFICATIVA_ESTOURO, " +
                    "  I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO, " +
                    "  I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO " +
                    "FROM VIEW_INTERVALO I " +
                    "JOIN COLABORADOR C ON I.CPF_COLABORADOR = C.CPF " +
                    "WHERE I.COD_UNIDADE = ? " +
                    "      AND I.CPF_COLABORADOR = ? " +
                    "      AND I.COD_TIPO_INTERVALO = ? " +
                    "      AND I.TIPO_MARCACAO = ? " +
                    "      AND DATA_HORA >= (SELECT MAX(DATA_HORA) " +
                    "                        FROM INTERVALO I " +
                    "                        WHERE I.COD_UNIDADE = ? " +
                    "                              AND I.CPF_COLABORADOR = ? " +
                    "                              AND I.COD_TIPO_INTERVALO = ? " +
                    "                              AND I.TIPO_MARCACAO = ?) " +
                    "ORDER BY I.DATA_HORA DESC " +
                    "LIMIT 1;");
            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, cpf);
            stmt.setLong(4, codTipoIntervalo);
            stmt.setString(5, TipoMarcacaoIntervalo.MARCACAO_INICIO.asString());
            stmt.setLong(6, codUnidade);
            stmt.setLong(7, cpf);
            stmt.setLong(8, codTipoIntervalo);
            stmt.setString(9, TipoMarcacaoIntervalo.MARCACAO_FIM.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createIntervaloMarcacao(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @NotNull
    @Override
    public List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                            @NotNull final Long cpf,
                                                            @NotNull final String codTipo,
                                                            final long limit,
                                                            final long offset) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Intervalo> intervalos = new ArrayList<>();
//        try {
//            conn = getConnection();
//            stmt = conn.prepareStatement("SELECT " +
//                    "I.COD_UNIDADE AS COD_UNIDADE, " +
//                    "I.COD_TIPO_INTERVALO AS COD_TIPO_INTERVALO, " +
//                    "I.CPF_COLABORADOR AS CPF_COLABORADOR, " +
//                    "I.DATA_HORA_INICIO AT TIME ZONE ? AS DATA_HORA_INICIO, " +
//                    "I.DATA_HORA_FIM AT TIME ZONE ?  AS DATA_HORA_FIM, " +
//                    "I.FONTE_DATA_HORA_INICIO AS FONTE_DATA_HORA_INICIO, " +
//                    "I.FONTE_DATA_HORA_FIM AS FONTE_DATA_HORA_FIM, " +
//                    "I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO, " +
//                    "I.JUSTIFICATIVA_ESTOURO AS JUSTIFICATIVA_ESTOURO, " +
//                    "I.LATITUDE_MARCACAO_INICIO AS LATITUDE_MARCACAO_INICIO, " +
//                    "I.LATITUDE_MARCACAO_FIM AS LATITUDE_MARCACAO_FIM, " +
//                    "I.LONGITUDE_MARCACAO_INICIO AS LONGITUDE_MARCACAO_INICIO, " +
//                    "I.LONGITUDE_MARCACAO_FIM AS LONGITUDE_MARCACAO_FIM " +
//                    "FROM FUNC_INTERVALOS_AGRUPADOS(NULL, ?, ?) I LIMIT ? OFFSET ?;");
//            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
//            stmt.setString(1, zoneId.getId());
//            stmt.setString(2, zoneId.getId());
//            stmt.setLong(3, cpf);
//            if (codTipo.equals("%")) {
//                stmt.setNull(4, Types.BIGINT);
//            } else {
//                stmt.setLong(4, Long.valueOf(codTipo));
//            }
//            stmt.setLong(5, limit);
//            stmt.setLong(6, offset);
//            rSet = stmt.executeQuery();
//            while (rSet.next()){
//                intervalos.add(createIntervaloAgrupado(rSet));
//            }
//        } finally {
//            closeConnection(conn, stmt, rSet);
//        }
        return intervalos;
    }

    @NotNull
    @Override
    public Long insertTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                                    @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO INTERVALO_TIPO(NOME, ICONE, TEMPO_RECOMENDADO_MINUTOS, " +
                    "TEMPO_ESTOURO_MINUTOS, HORARIO_SUGERIDO, COD_UNIDADE, ATIVO) VALUES (?,?,?,?,?,?,TRUE) RETURNING" +
                    " CODIGO;");
            stmt.setString(1, tipoIntervalo.getNome());
            stmt.setString(2, tipoIntervalo.getIcone().getNomeIcone());
            stmt.setLong(3, tipoIntervalo.getTempoRecomendado().toMinutes());
            stmt.setLong(4, tipoIntervalo.getTempoLimiteEstouro().toMinutes());
            stmt.setTime(5, tipoIntervalo.getHorarioSugerido());
            stmt.setLong(6, tipoIntervalo.getUnidade().getCodigo());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                tipoIntervalo.setCodigo(rSet.getLong("CODIGO"));
                associaCargosTipoIntervalo(tipoIntervalo, conn);
                // Avisamos o listener que um tipo de intervalo FOI INCLUÍDO.
                listener.onTiposIntervaloChanged(conn, tipoIntervalo.getUnidade().getCodigo());
                // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
                conn.commit();
                return tipoIntervalo.getCodigo();
            } else {
                throw new SQLException("Erro ao inserir o Tipo de Intervalo de nome: " + tipoIntervalo.getNome());
            }
        } catch (Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void updateTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                                    @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE INTERVALO_TIPO " +
                    "SET NOME = ?, ICONE = ?, TEMPO_RECOMENDADO_MINUTOS = ?, TEMPO_ESTOURO_MINUTOS = ?, " +
                    "HORARIO_SUGERIDO = ? WHERE COD_UNIDADE = ? AND CODIGO = ?");
            stmt.setString(1, tipoIntervalo.getNome());
            stmt.setString(2, tipoIntervalo.getIcone().getNomeIcone());
            stmt.setLong(3, tipoIntervalo.getTempoRecomendado().toMinutes());
            stmt.setLong(4, tipoIntervalo.getTempoLimiteEstouro().toMinutes());
            stmt.setTime(5, tipoIntervalo.getHorarioSugerido());
            stmt.setLong(6, tipoIntervalo.getUnidade().getCodigo());
            stmt.setLong(7, tipoIntervalo.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar o Tipo de Intervalo de código: " + tipoIntervalo.getCodigo());
            }
            associaCargosTipoIntervalo(tipoIntervalo, conn);
            // Avisamos o listener que um tipo de intervalo mudou.
            listener.onTiposIntervaloChanged(conn, tipoIntervalo.getUnidade().getCodigo());

            // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
            conn.commit();
        } catch (Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @NotNull
    @Override
    public List<TipoIntervalo> getTiposIntervalosByUnidade(@NotNull final Long codUnidade, final boolean withCargos)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<TipoIntervalo> tipos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT " +
                    "IT.CODIGO AS CODIGO_TIPO_INTERVALO, " +
                    "IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE, " +
                    "IT.NOME AS " +
                    "NOME_TIPO_INTERVALO, " +
                    "IT.COD_UNIDADE, " +
                    "IT.ATIVO, " +
                    "IT.HORARIO_SUGERIDO, " +
                    "IT.ICONE, " +
                    "IT.TEMPO_ESTOURO_MINUTOS, " +
                    "IT.TEMPO_RECOMENDADO_MINUTOS " +
                    "FROM INTERVALO_TIPO_CARGO ITC JOIN VIEW_INTERVALO_TIPO IT ON ITC.COD_UNIDADE = IT.COD_UNIDADE AND ITC" +
                    ".COD_TIPO_INTERVALO = IT.CODIGO " +
                    " WHERE IT.COD_UNIDADE = ? AND IT.ATIVO IS TRUE");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tipos.add(createTipoInvervalo(rSet, withCargos, conn));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return tipos;
    }

    @NotNull
    @Override
    public TipoIntervalo getTipoIntervalo(@NotNull final Long codUnidade,
                                          @NotNull final Long codTipoIntervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "IT.CODIGO AS CODIGO_TIPO_INTERVALO, " +
                    "IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE, " +
                    "IT.NOME AS " +
                    "NOME_TIPO_INTERVALO, " +
                    "IT.COD_UNIDADE, " +
                    "IT.ATIVO, " +
                    "IT.HORARIO_SUGERIDO, " +
                    "IT.ICONE, " +
                    "IT.TEMPO_ESTOURO_MINUTOS, " +
                    "IT.TEMPO_RECOMENDADO_MINUTOS " +
                    "FROM INTERVALO_TIPO_CARGO ITC JOIN VIEW_INTERVALO_TIPO IT ON ITC.COD_UNIDADE = IT.COD_UNIDADE AND ITC" +
                    ".COD_TIPO_INTERVALO = IT.CODIGO " +
                    " WHERE IT.COD_UNIDADE = ? AND IT.CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createTipoInvervalo(rSet, true, conn);
            } else {
                throw new SQLException("Nenhum tipo de intervalo encontrado com o código: " + codTipoIntervalo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void inativarTipoIntervalo(@NotNull final Long codUnidade, @NotNull final Long codTipoIntervalo,
                                      @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE INTERVALO_TIPO " +
                    "SET STATUS = FALSE WHERE COD_UNIDADE = ? AND CODIGO = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inativar o Tipo de Intervalo de código: " + codTipoIntervalo);
            }

            // Avisamos o listener que um tipo de intervalo mudou.
            listener.onTiposIntervaloChanged(conn, codUnidade);

            // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
            conn.commit();
        } catch (Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @NotNull
    @Override
    public Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT VERSAO_DADOS FROM INTERVALO_UNIDADE WHERE COD_UNIDADE = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return Optional.of(rSet.getLong("VERSAO_DADOS"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return Optional.empty();
    }

    private boolean marcacaoIntervaloJaExiste(@NotNull final IntervaloMarcacao intervaloMarcacao,
                                              @NotNull final Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT I.CODIGO FROM INTERVALO I WHERE " +
                    "I.COD_UNIDADE = ? AND I.CPF_COLABORADOR = ? AND I.DATA_HORA = ? AND I.TIPO_MARCACAO = ?);");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(intervaloMarcacao.getCodUnidade(), conn);
            stmt.setLong(1, intervaloMarcacao.getCodUnidade());
            stmt.setLong(2, intervaloMarcacao.getCpfColaborador());
            stmt.setObject(3, intervaloMarcacao.getDataHoraMaracao().atZone(zoneId).toOffsetDateTime());
            stmt.setString(4, intervaloMarcacao.getTipoMarcacaoIntervalo().asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return false;
    }

    private void internalInsertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao,
                                                 @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, " +
                    "DATA_HORA, TIPO_MARCACAO, FONTE_DATA_HORA, JUSTIFICATIVA_TEMPO_RECOMENDADO, JUSTIFICATIVA_ESTOURO, " +
                    "LATITUDE_MARCACAO, LONGITUDE_MARCACAO, DATA_HORA_SINCRONIZACAO) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            final Long codUnidade = intervaloMarcacao.getCodUnidade();
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, intervaloMarcacao.getCodTipoIntervalo());
            stmt.setLong(3, intervaloMarcacao.getCpfColaborador());
            stmt.setObject(4, intervaloMarcacao.getDataHoraMaracao().atZone(zoneId).toOffsetDateTime());

            stmt.setString(5, intervaloMarcacao.getTipoMarcacaoIntervalo().asString());
            stmt.setString(6, intervaloMarcacao.getFonteDataHora().asString());
            stmt.setString(7, intervaloMarcacao.getJustificativaTempoRecomendado());
            stmt.setString(8, intervaloMarcacao.getJustificativaEstouro());

            final Localizacao localizacao = intervaloMarcacao.getLocalizacaoMarcacao();
            if (localizacao != null) {
                stmt.setString(9, localizacao.getLatitude());
                stmt.setString(10, localizacao.getLongitude());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.setTimestamp(11, Now.timestampUtc());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir marcação de intervalo");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @NotNull
    private Intervalo createIntervaloAgrupado(@NotNull final ResultSet rSet) throws SQLException {
        final Intervalo intervalo = new Intervalo();

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        intervalo.setColaborador(colaborador);

        // TODO: Recuperar nome do tipo de intervalo.
        final TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervalo.setTipo(tipoIntervalo);

        intervalo.setDataHoraInicio(rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class));
        intervalo.setDataHoraFim(rSet.getObject("DATA_HORA_FIM", LocalDateTime.class));
        final String fonteDataHoraInicio = rSet.getString("FONTE_DATA_HORA_INICIO");
        if (!rSet.wasNull()) {
            intervalo.setFonteDataHoraInicio(FonteDataHora.fromString(fonteDataHoraInicio));
        }
        final String fonteDataHoraFim = rSet.getString("FONTE_DATA_HORA_FIM");
        if (!rSet.wasNull()) {
            intervalo.setFonteDataHoraFim(FonteDataHora.fromString(fonteDataHoraFim));
        }
        intervalo.setJustificativaTempoRecomendado(rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
        intervalo.setJustificativaEstouro(rSet.getString("JUSTIFICATIVA_ESTOURO"));

        final String latitudeInicio = rSet.getString("LATITUDE_MARCACAO_INICIO");
        if (!rSet.wasNull()) {
            final Localizacao localizacao = new Localizacao();
            localizacao.setLatitude(latitudeInicio);
            localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO_INICIO"));
            intervalo.setLocalizacaoInicio(localizacao);
        }

        final String latitudeFim = rSet.getString("LATITUDE_MARCACAO_FIM");
        if (!rSet.wasNull()) {
            final Localizacao localizacao = new Localizacao();
            localizacao.setLatitude(latitudeFim);
            localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO_FIM"));
            intervalo.setLocalizacaoFim(localizacao);
        }

        // Cálculo do tempo decorrido.
        final LocalDateTime dataHoraFim = intervalo.getDataHoraFim();
        final LocalDateTime dataHoraInicio = intervalo.getDataHoraInicio();
        if (dataHoraInicio != null && dataHoraFim != null) {
            intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(dataHoraInicio, dataHoraFim))));
        } else if (dataHoraFim == null) {
            // TODO: Precisamos trocar esse cálculo para contecer no app.
//            intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(dataHoraInicio, dataHoraFim))));
        }

        return intervalo;
    }

    private void associaCargosTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                                            @NotNull final Connection conn) throws SQLException {
        deleteCargosTipoIntervalo(
                tipoIntervalo.getUnidade().getCodigo(),
                tipoIntervalo.getCodigo(),
                conn);
        insertCargosTipoIntervalo(
                tipoIntervalo.getUnidade().getCodigo(),
                tipoIntervalo.getCodigo(),
                tipoIntervalo.getCargos(),
                conn);
    }

    private void deleteCargosTipoIntervalo(@NotNull final Long codUnidade,
                                           @NotNull final Long codTipoIntervalo,
                                           @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM INTERVALO_TIPO_CARGO WHERE COD_UNIDADE = ? AND " +
                    "COD_TIPO_INTERVALO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            stmt.executeUpdate();
            // Não precisamos verificar se o delete afetou alguma linha pois o intervalo pode não ter nenhum cargo
            // vinculado.
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertCargosTipoIntervalo(@NotNull final Long codUnidade,
                                           @NotNull final Long codTipoIntervalo,
                                           @NotNull final List<Cargo> cargos,
                                           @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO_TIPO_CARGO VALUES (?,?,?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            for (final Cargo cargo : cargos) {
                stmt.setLong(3, cargo.getCodigo());
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao vincular cargo ao tipo de intervalo");
                }
            }
        } finally {
            closeStatement(stmt);
        }
    }

    @NotNull
    private TipoIntervalo createTipoInvervalo(@NotNull final ResultSet rSet,
                                              final boolean withCargos,
                                              @NotNull final Connection conn) throws SQLException {
        final TipoIntervalo tipoIntervalo = new TipoIntervalo();
        tipoIntervalo.setCodigo(rSet.getLong("CODIGO_TIPO_INTERVALO"));
        tipoIntervalo.setCodigoPorUnidade(rSet.getLong("CODIGO_TIPO_INTERVALO_POR_UNIDADE"));
        tipoIntervalo.setNome(rSet.getString("NOME_TIPO_INTERVALO"));
        final Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        tipoIntervalo.setUnidade(unidade);
        tipoIntervalo.setAtivo(rSet.getBoolean("ATIVO"));
        tipoIntervalo.setHorarioSugerido(rSet.getTime("HORARIO_SUGERIDO"));
        tipoIntervalo.setIcone(Icone.fromString(rSet.getString("ICONE")));
        tipoIntervalo.setTempoLimiteEstouro(Duration.ofMinutes(rSet.getLong("TEMPO_ESTOURO_MINUTOS")));
        tipoIntervalo.setTempoRecomendado(Duration.ofMinutes(rSet.getLong("TEMPO_RECOMENDADO_MINUTOS")));
        if (withCargos) {
            tipoIntervalo.setCargos(getCargosByTipoIntervalo(tipoIntervalo, conn));
        }
        return tipoIntervalo;
    }

    @NotNull
    private List<Cargo> getCargosByTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                                                 @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Cargo> cargos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT F.* FROM " +
                    "  INTERVALO_TIPO_CARGO ITC JOIN UNIDADE U ON U.CODIGO = ITC.COD_UNIDADE " +
                    "JOIN FUNCAO F ON F.cod_emprESA = U.cod_empresa AND F.codigo = ITC.COD_CARGO " +
                    "WHERE ITC.COD_TIPO_INTERVALO = ? and ITC.COD_UNIDADE = ?;");
            stmt.setLong(1, tipoIntervalo.getCodigo());
            stmt.setLong(2, tipoIntervalo.getUnidade().getCodigo());
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                cargos.add(new Cargo(rSet.getLong("CODIGO"), rSet.getString("NOME")));
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return cargos;
    }

    private IntervaloMarcacao createIntervaloMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        final IntervaloMarcacao intervaloMarcacao = new IntervaloMarcacao();
        intervaloMarcacao.setCodigo(rSet.getLong("CODIGO"));
        intervaloMarcacao.setCodMarcacaoPorUnidade(rSet.getLong("COD_MARCACAO_POR_UNIDADE"));
        intervaloMarcacao.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        intervaloMarcacao.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
        intervaloMarcacao.setDataNascimentoColaborador(rSet.getDate("DATA_NASCIMENTO_COLABORADOR"));
        intervaloMarcacao.setCodTipoIntervalo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervaloMarcacao.setDataHoraMaracao(rSet.getObject("DATA_HORA", LocalDateTime.class));
        intervaloMarcacao.setFonteDataHora(FonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA")));
        intervaloMarcacao.setTipoMarcacaoIntervalo(TipoMarcacaoIntervalo.fromString(rSet.getString("TIPO_MARCACAO")));
        intervaloMarcacao.setJustificativaTempoRecomendado(rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
        intervaloMarcacao.setJustificativaEstouro(rSet.getString("JUSTIFICATIVA_ESTOURO"));

        final String latitudeMarcacao = rSet.getString("LATITUDE_MARCACAO");
        if (!rSet.wasNull()) {
            final Localizacao localizacao = new Localizacao();
            localizacao.setLatitude(latitudeMarcacao);
            localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO"));
            intervaloMarcacao.setLocalizacaoMarcacao(localizacao);
        }
        return intervaloMarcacao;
    }
}