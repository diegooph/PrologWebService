package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ControleJornadaDaoImpl extends DatabaseConnection implements ControleJornadaDao {

    @NotNull
    @Override
    public Long insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // Se a marcação já existir, nós não tentamos inserir novamente e simplemente não fazemos nada.
            // Isso garante um retorno OK para o app e assim a marcação será colocada como sincronizada.
            // É importante tratar esse cenário pois o app pode tentar sincronizar uma marcação,
            // ela ser inserida com sucesso, mas a conexão com o servidor se perder nesse meio tempo,
            // e o app acaba assumindo, erroneamente, que a marcação ainda não foi sincronizada.
            final Long codMarcacaoExistente = marcacaoIntervaloJaExiste(conn, intervaloMarcacao);
            // O codMarcacaoExistente será <= 0, se e somente se, não existir uma marcação equivalente
            // no Banco de Dados.
            if (codMarcacaoExistente <= 0) {
                final Long codMarcacaoInserida = internalInsertMarcacaoIntervalo(conn, intervaloMarcacao);
                conn.commit();
                return codMarcacaoInserida;
            } else {
                conn.commit();
                return codMarcacaoExistente;
            }
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @Nullable
    @Override
    public IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(
            @NotNull final Long codUnidade,
            @NotNull final Long cpfColaborador,
            @NotNull final Long codTipoIntervalo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_BUSCA_MARCACAO_EM_ANDAMENTO(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            stmt.setLong(3, cpfColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createIntervaloMarcacao(rSet);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public void insereMarcacaoInicioOuFim(@NotNull final Connection conn,
                                          @NotNull final Long codMarcacaoInserida,
                                          @NotNull final TipoInicioFim tipoInicioFim) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            if (tipoInicioFim.equals(TipoInicioFim.MARCACAO_INICIO)) {
                stmt = conn.prepareStatement("INSERT INTO MARCACAO_INICIO(COD_MARCACAO_INICIO) " +
                        "VALUES (?) RETURNING COD_MARCACAO_INICIO AS CODIGO");
            } else {
                stmt = conn.prepareStatement("INSERT INTO MARCACAO_FIM(COD_MARCACAO_FIM) " +
                        "VALUES (?) RETURNING COD_MARCACAO_FIM AS CODIGO");
            }
            stmt.setLong(1, codMarcacaoInserida);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO") <= 0) {
                throw new SQLException("Não foi possível inserir o código da marcação na tabela: "
                        + tipoInicioFim.asString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public void insereVinculoInicioFim(@NotNull final Connection conn,
                                       @NotNull final Long codMarcacaoInicio,
                                       @NotNull final Long codMarcacaoFim) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "MARCACAO_VINCULO_INICIO_FIM(COD_MARCACAO_INICIO, COD_MARCACAO_FIM) " +
                    "VALUES (?, ?) " +
                    "RETURNING CODIGO AS CODIGO_VINCULO");
            stmt.setLong(1, codMarcacaoInicio);
            stmt.setLong(2, codMarcacaoFim);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getLong("CODIGO_VINCULO") <= 0) {
                throw new SQLException("Não foi possível inserir o vinculo entre as marcações");
            }
        } finally {
            close(stmt, rSet);
        }
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
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_INTERVALOS_GET_MARCACOES_COLABORADOR(?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, cpf);
            if (codTipo.equals("%")) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, Long.valueOf(codTipo));
            }
            stmt.setLong(4, limit);
            stmt.setLong(5, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                intervalos.add(createIntervaloAgrupado(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return intervalos;
    }

    @NotNull
    @Override
    public List<MarcacaoListagem> getMarcacoesColaboradorPorData(@NotNull final Long codUnidade,
                                                                 @Nullable final Long cpf,
                                                                 @Nullable final Long codTipo,
                                                                 @NotNull final LocalDate dataInicial,
                                                                 @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_LISTAGEM_MARCACOES(" +
                    "F_COD_UNIDADE        := ?," +
                    "F_CPF_COLABORADOR    := ?," +
                    "F_COD_TIPO_INTERVALO := ?," +
                    "F_DATA_INICIAL       := ?," +
                    "F_DATA_FINAL         := ?);");
            stmt.setLong(1, codUnidade);
            StatementUtils.bindValueOrNull(stmt, 2, cpf, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 3, codTipo, SqlType.BIGINT);
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            rSet = stmt.executeQuery();
            final List<MarcacaoListagem> marcacoes = new ArrayList<>();
            while (rSet.next()) {
                marcacoes.add(ControleJornadaConverter.createMarcacaoListagem(rSet));
            }
            return marcacoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean verifyIfTokenMarcacaoExists(@NotNull final String tokenMarcacao) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT TOKEN_SINCRONIZACAO_MARCACAO " +
                    " FROM INTERVALO_UNIDADE WHERE TOKEN_SINCRONIZACAO_MARCACAO = ?) AS EXISTE_TOKEN;");
            stmt.setString(1, tokenMarcacao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTE_TOKEN");
            } else {
                throw new SQLException(
                        "Não foi possível verifica a existencia do token de sincronia de marcação: " + tokenMarcacao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Optional<DadosMarcacaoUnidade> getDadosMarcacaoUnidade(
            @NotNull final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT VERSAO_DADOS, TOKEN_SINCRONIZACAO_MARCACAO " +
                    "FROM INTERVALO_UNIDADE WHERE COD_UNIDADE = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return Optional.of(createVersaoDadosMarcacao(rSet));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private Long marcacaoIntervaloJaExiste(@NotNull final Connection conn,
                                           @NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_BUSCA_COD_MARCACAO(?, ?, ?, ?) AS CODIGO;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(intervaloMarcacao.getCodUnidade(), conn);
            stmt.setLong(1, intervaloMarcacao.getCodUnidade());
            stmt.setLong(2, intervaloMarcacao.getCpfColaborador());
            stmt.setString(3, intervaloMarcacao.getTipoMarcacaoIntervalo().asString());
            stmt.setObject(4, intervaloMarcacao.getDataHoraMaracao().atZone(zoneId).toOffsetDateTime());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível consultar a existência de uma duplicata desta marcação");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Long internalInsertMarcacaoIntervalo(@NotNull final Connection conn,
                                                 @NotNull final IntervaloMarcacao intervaloMarcacao) throws Throwable {
        /*
        Temos um fluxo já definido de caminhos a serem seguidos para cada caso que poderemos ter na marcação.
            1 --> intervaloMarcacao é de INICIO
                1.1 --> Insere marcacao na tabela Intervalo.
                1.2 --> Insere codMarcacao na tabela de MARCACAO_INICIO.
                1.3 --> Retorna o código;
                1.4 --> FIM DO PROCESSO;
            2 --> intervaloMarcacao é de FIM
                2.1 --> Marcação possuí código de vínculo
                    2.1.1 --> Insere marcação na tabela Intervalo.
                    2.1.2 --> Insere codMarcacao na tabela MARCACAO_FIM.
                    2.1.3 --> O código de vínculo possuí outra marcação associada
                        2.1.3.1 --> Insere códigos na Tabela Inconsistência.
                        2.1.3.2 --> FIM DO PROCESSO;
                    2.1.4 --> O código de vínculo não possuí outra marcação associada
                        2.1.4.1 --> Insere codMarcacao e codMarcacaoVinculada na tabela VINCULO_INICIO_FIM.
                        2.1.4.2 --> FIM DO PROCESSO;
                2.2 --> Marcação não possuí código de vínculo
                    2.2.1 --> Busca codMarcacaoInicio com base no algoritmo de matching de marcações
                    2.2.2 --> CodMarcacaoInicio encontrado
                        2.2.2.1 --> Insere marcação na tabela Intervalo.
                        2.2.2.2 --> Insere codMarcacao na tabela MARCACAO_FIM.
                        2.2.2.3 --> O código de vínculo possuí outra marcação associada
                            2.2.2.3.1 --> Insere códigos na Tabela Inconsistência.
                            2.2.2.3.2 --> FIM DO PROCESSO;
                        2.2.2.4 --> O código de vínculo não possuí outra marcação associada
                            2.2.2.4.1 --> Insere codMarcacao e codMarcacaoVinculada na tabela VINCULO_INICIO_FIM.
                            2.2.2.4.2 --> FIM DO PROCESSO;
                    2.2.3 --> CodMarcacaoInicio não encontrado
                        2.2.2.1 --> Insere marcação na tabela Intervalo.
                        2.2.2.2 --> Insere codMarcacao na tabela MARCACAO_FIM.
                        2.2.2.3 --> FIM DO PROCESSO;
                    2.2.4 --> Retorna o código;
                    2.2.5 --> FIM DO PROCESSO;
         */
        if (intervaloMarcacao.isInicio()) {
            final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
            insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_INICIO);
            return codMarcacaoInserida;
        } else {
            final Long codMarcacaoVinculada = intervaloMarcacao.getCodMarcacaoVinculada() != null
                    ? intervaloMarcacao.getCodMarcacaoVinculada()
                    : buscaMarcacaoInicioVinculo(
                    conn,
                    intervaloMarcacao.getCodUnidade(),
                    intervaloMarcacao.getCodTipoIntervalo(),
                    intervaloMarcacao.getCpfColaborador());
            if (codMarcacaoVinculada != null && codMarcacaoVinculada > 0) {
                final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
                insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_FIM);
                insertInconsistenciaOuVinculo(conn, codMarcacaoVinculada, codMarcacaoInserida);
                return codMarcacaoInserida;
            } else {
                final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
                insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_FIM);
                return codMarcacaoInserida;
            }
        }
    }

    @NotNull
    private Long insertMarcacao(@NotNull final Connection conn,
                                @NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_JORNADA(" +
                    "F_COD_UNIDADE                        := ?, " +
                    "F_COD_TIPO_INTERVALO                 := ?, " +
                    "F_CPF_COLABORADOR                    := ?, " +
                    "F_DATA_HORA                          := ?, " +
                    "F_TIPO_MARCACAO                      := ?, " +
                    "F_FONTE_DATA_HORA                    := ?, " +
                    "F_JUSTIFICATIVA_TEMPO_RECOMENDADO    := ?, " +
                    "F_JUSTIFICATIVA_ESTOURO              := ?, " +
                    "F_LATITUDE_MARCACAO                  := ?, " +
                    "F_LONGITUDE_MARCACAO                 := ?, " +
                    "F_DATA_HORA_SINCRONIZACAO            := ?, " +
                    "F_VERSAO_APP_MOMENTO_MARCACAO        := ?, " +
                    "F_VERSAO_APP_MOMENTO_SINCRONIZACAO   := ?, " +
                    "F_DEVICE_ID                          := ?, " +
                    "F_DEVICE_IMEI                        := ?, " +
                    "F_DEVICE_UPTIME_REALIZACAO_MILLIS    := ?, " +
                    "F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS := ?, " +
                    "F_ANDROID_API_VERSION                := ?, " +
                    "F_MARCA_DEVICE                       := ?, " +
                    "F_MODELO_DEVICE                      := ?) AS CODIGO;");
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
            bindValueOrNull(stmt, 9, localizacao != null ? localizacao.getLatitude() : null, SqlType.VARCHAR);
            bindValueOrNull(stmt, 10, localizacao != null ? localizacao.getLongitude() : null, SqlType.VARCHAR);
            stmt.setObject(11, Now.getOffsetDateTimeUtc());
            bindValueOrNull(stmt, 12, intervaloMarcacao.getVersaoAppMomentoMarcacao(), SqlType.INTEGER);
            bindValueOrNull(stmt, 13, intervaloMarcacao.getVersaoAppMomentoSincronizacao(), SqlType.INTEGER);
            stmt.setString(14, intervaloMarcacao.getDeviceId());
            stmt.setString(15, intervaloMarcacao.getDeviceImei());
            stmt.setLong(16, intervaloMarcacao.getDeviceUptimeRealizacaoMarcacaoMillis());
            stmt.setLong(17, intervaloMarcacao.getDeviceUptimeSincronizacaoMarcacaoMillis());
            stmt.setInt(18, intervaloMarcacao.getAndroidApiVersion());
            stmt.setString(19, intervaloMarcacao.getMarcaDevice());
            stmt.setString(20, intervaloMarcacao.getModeloDevice());
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("CODIGO") > 0) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir marcação de intervalo");
            }
        } finally {
            close(rSet, stmt);
        }
    }

    @Nullable
    private Long buscaMarcacaoInicioVinculo(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codTipoIntervalo,
                                            @NotNull final Long cpfColaborador) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_BUSCA_MARCACAO_VINCULO_BY_MARCACAO(?, ?, ?) AS CODIGO;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            stmt.setLong(3, cpfColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codigo = rSet.getLong("CODIGO");
                // retornamos null caso codigo > 0 pois significa que não existe um código de vínculo
                return codigo > 0 ? codigo : null;
            } else {
                throw new SQLException("Erro ao buscar código de vínculo para a marcação");
            }
        } finally {
            close(rSet, stmt);
        }
    }

    private void insertInconsistenciaOuVinculo(@NotNull final Connection conn,
                                               @NotNull final Long codMarcacaoVinculada,
                                               @NotNull final Long codMarcacaoInserida) throws Throwable {
        if (marcacaoInicioJaPossuiVinculo(conn, codMarcacaoVinculada)) {
            insereMarcacaoInconsistente(conn, codMarcacaoVinculada, codMarcacaoInserida);
        } else {
            insereVinculoInicioFim(conn, codMarcacaoVinculada, codMarcacaoInserida);
        }
    }

    private boolean marcacaoInicioJaPossuiVinculo(@NotNull final Connection conn,
                                                  @NotNull final Long codMarcacaoInicio) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT CODIGO FROM MARCACAO_VINCULO_INICIO_FIM " +
                    "WHERE COD_MARCACAO_INICIO = ?) AS EXISTE_INCONSISTENCIA;");
            stmt.setLong(1, codMarcacaoInicio);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                // Se o CODIGO <= 0 então não existe inconsistência para a marcação.
                return rSet.getBoolean("EXISTE_INCONSISTENCIA");
            } else {
                throw new SQLException("Erro ao verificar se a marcação " + codMarcacaoInicio + " possui inconsistência");
            }
        } finally {
            close(rSet, stmt);
        }
    }

    private void insereMarcacaoInconsistente(@NotNull final Connection conn,
                                             @NotNull final Long codMarcacaoVinculada,
                                             @NotNull final Long codMarcacaoInserida) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_INCONSISTENCIA(?, ?) AS RESULT;");
            stmt.setLong(1, codMarcacaoVinculada);
            stmt.setLong(2, codMarcacaoInserida);
            rSet = stmt.executeQuery();
            if (!rSet.next() || !rSet.getBoolean("RESULT")) {
                throw new SQLException("Erro ao inserir as inconsistências das marcações");
            }
        } finally {
            close(rSet, stmt);
        }
    }

    @NotNull
    private TipoMarcacao createTipoInvervalo(@NotNull final ResultSet rSet,
                                             final boolean withCargos,
                                             @NotNull final Connection conn) throws SQLException {
        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
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
    private List<Cargo> getCargosByTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                                                 @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Cargo> cargos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT F.* " +
                    "FROM INTERVALO_TIPO_CARGO ITC " +
                    "  JOIN UNIDADE U ON U.CODIGO = ITC.COD_UNIDADE " +
                    "  JOIN FUNCAO F ON F.COD_EMPRESA = U.COD_EMPRESA AND F.CODIGO = ITC.COD_CARGO " +
                    "WHERE ITC.COD_TIPO_INTERVALO = ? AND ITC.COD_UNIDADE = ?;");
            stmt.setLong(1, tipoIntervalo.getCodigo());
            stmt.setLong(2, tipoIntervalo.getUnidade().getCodigo());
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                cargos.add(new Cargo(rSet.getLong("CODIGO"), rSet.getString("NOME")));
            }
        } finally {
            close(stmt, rSet);
        }
        return cargos;
    }

    @NotNull
    private IntervaloMarcacao createIntervaloMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        final IntervaloMarcacao intervaloMarcacao = new IntervaloMarcacao();
        intervaloMarcacao.setCodigo(rSet.getLong("CODIGO"));
        intervaloMarcacao.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        intervaloMarcacao.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
        intervaloMarcacao.setDataNascimentoColaborador(rSet.getDate("DATA_NASCIMENTO_COLABORADOR"));
        intervaloMarcacao.setCodTipoIntervalo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervaloMarcacao.setDataHoraMaracao(rSet.getObject("DATA_HORA", LocalDateTime.class));
        intervaloMarcacao.setFonteDataHora(FonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA")));
        intervaloMarcacao.setTipoMarcacaoIntervalo(TipoInicioFim.fromString(rSet.getString("TIPO_MARCACAO")));
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

    @NotNull
    private Intervalo createIntervaloAgrupado(@NotNull final ResultSet rSet) throws SQLException {
        final Intervalo intervalo = new Intervalo();

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        intervalo.setColaborador(colaborador);

        // TODO: Recuperar nome do tipo de intervalo.
        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        tipoIntervalo.setNome(rSet.getString("NOME_TIPO_INTERVALO"));
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

    @NotNull
    private DadosMarcacaoUnidade createVersaoDadosMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        final DadosMarcacaoUnidade versaoDados = new DadosMarcacaoUnidade();
        versaoDados.setVersaoDadosBanco(rSet.getLong("VERSAO_DADOS"));
        versaoDados.setTokenSincronizacaoMarcacao(rSet.getString("TOKEN_SINCRONIZACAO_MARCACAO"));
        return versaoDados;
    }
}