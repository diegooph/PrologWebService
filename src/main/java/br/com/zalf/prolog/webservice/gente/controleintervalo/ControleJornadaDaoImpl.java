package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ControleJornadaDaoImpl extends DatabaseConnection implements ControleJornadaDao {

    public ControleJornadaDaoImpl() {

    }

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
            close(conn, stmt, rSet);
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(
            @NotNull final Long codUnidade,
            @NotNull final Long cpfColaborador,
            @NotNull final Long codTipoIntervalo) throws SQLException {
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

    @NotNull
    @Override
    public List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                            @NotNull final Long cpfColaborador,
                                                            @NotNull final String codTipoIntevalo,
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
            stmt.setLong(2, cpfColaborador);
            bindValueOrNull(stmt, 3, codTipoIntevalo.equals("%") ? null : codTipoIntevalo, SqlType.BIGINT);
            stmt.setLong(4, limit);
            stmt.setLong(5, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                intervalos.add(ControleJornadaConverter.createIntervalo(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return intervalos;
    }

    @NotNull
    @Override
    public Long insertTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
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
                associaCargosTipoIntervalo(conn, tipoIntervalo);
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
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<TipoMarcacao> getTiposIntervalosByUnidade(@NotNull final Long codUnidade,
                                                          final boolean apenasAtivos,
                                                          final boolean withCargos) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<TipoMarcacao> tipos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CONTROLE_JORNADA_GET_TIPOS_INTERVALOS_UNIDADE(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setBoolean(2, apenasAtivos);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tipos.add(createTipoInvervalo(conn, rSet, withCargos));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return tipos;
    }

    @NotNull
    @Override
    public TipoMarcacao getTipoIntervalo(@NotNull final Long codUnidade,
                                         @NotNull final Long codTipoIntervalo) throws SQLException {
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
                    "IT.TEMPO_RECOMENDADO_MINUTOS " +
                    "FROM INTERVALO_TIPO_CARGO ITC JOIN VIEW_INTERVALO_TIPO IT " +
                    "ON ITC.COD_UNIDADE = IT.COD_UNIDADE AND ITC.COD_TIPO_INTERVALO = IT.CODIGO " +
                    " WHERE IT.COD_UNIDADE = ? AND IT.CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createTipoInvervalo(conn, rSet, true);
            } else {
                throw new SQLException("Nenhum tipo de intervalo encontrado com o código: " + codTipoIntervalo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                                    @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE INTERVALO_TIPO " +
                    "SET NOME = ?, ICONE = ?, TEMPO_RECOMENDADO_MINUTOS = ?, TEMPO_ESTOURO_MINUTOS = ?, " +
                    "HORARIO_SUGERIDO = ? WHERE COD_UNIDADE = ? AND CODIGO = ? AND ATIVO = TRUE;");
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
            associaCargosTipoIntervalo(conn, tipoIntervalo);
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
            close(conn, stmt);
        }
    }

    @Override
    public void updateStatusAtivoTipoIntervalo(@NotNull final Long codUnidade,
                                               @NotNull final Long codTipoIntervalo,
                                               @NotNull final TipoMarcacao tipoIntervalo,
                                               @NotNull final DadosIntervaloChangedListener listener) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE INTERVALO_TIPO " +
                    "SET ATIVO = ? WHERE COD_UNIDADE = ? AND CODIGO = ?;");
            stmt.setBoolean(1, tipoIntervalo.isAtivo());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codTipoIntervalo);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inativar o Tipo de Intervalo de código: " + codTipoIntervalo);
            }
            // Avisamos o listener que um tipo de intervalo mudou.
            listener.onTiposIntervaloChanged(conn, codUnidade);
            // Se nem um erro aconteceu ao informar o listener, podemos commitar a alteração.
            conn.commit();
        } catch (final Throwable e) {
            // Pegamos apenas para fazer o rollback, depois subimos o erro.
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt);
        }
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
                    2.1.3 --> Insere codMarcacao e codMarcacaoVinculada na tabela VINCULO_INICIO_FIM.
                    2.1.4 --> O código de vínculo possuí outra marcação associada
                        2.1.4.1 --> Insere códigos na Tabela Inconsistência.
                        2.1.4.2 --> FIM DO PROCESSO;
                    2.1.5 --> O código de vínculo não possuí outra marcação associada
                        2.1.5.1 --> FIM DO PROCESSO;
                2.2 --> Marcação não possuí código de vínculo
                    2.2.1 --> Busca codMarcacaoInicio com base no algoritmo de matching de marcações
                    2.2.2 --> CodMarcacaoInicio encontrado
                        2.2.2.1 --> Insere marcação na tabela Intervalo.
                        2.2.2.2 --> Insere codMarcacao na tabela MARCACAO_FIM.
                        2.2.2.3 --> Insere codMarcacao e codMarcacaoInicio na tabela VINCULO_INICIO_FIM.
                        2.2.2.4 --> O código de vínculo possuí outra marcação associada
                            2.2.2.4.1 --> Insere códigos na Tabela Inconsistência.
                            2.2.2.4.2 --> FIM DO PROCESSO;
                        2.2.2.5 --> O código de vínculo não possuí outra marcação associada
                            2.2.2.5.1 --> FIM DO PROCESSO;
                    2.2.3 --> CodMarcacaoInicio não encontrado
                        2.2.2.1 --> Insere marcação na tabela Intervalo.
                        2.2.2.2 --> Insere codMarcacao na tabela MARCACAO_FIM.
                        2.2.2.3 --> FIM DO PROCESSO;
                    2.2.4 --> Retorna o código;
                    2.2.5 --> FIM DO PROCESSO;
         */
        if (intervaloMarcacao.getTipoMarcacaoIntervalo().equals(TipoInicioFim.MARCACAO_INICIO)) {
            final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
            insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_INICIO);
            return codMarcacaoInserida;
        } else {
            if (intervaloMarcacao.getCodMarcacaoVinculada() != null) {
                final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
                insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_FIM);
                insereVinculoInicioFim(conn, intervaloMarcacao.getCodMarcacaoVinculada(), codMarcacaoInserida);
                insereMarcacaoInconsistenteSeExistir(conn, intervaloMarcacao.getCodMarcacaoVinculada(), codMarcacaoInserida);
                return codMarcacaoInserida;
            } else {
                final Long codMarcacacaoVinculo = buscaMarcacaoInicioVinculo(
                        conn,
                        intervaloMarcacao.getCodUnidade(),
                        intervaloMarcacao.getCodTipoIntervalo(),
                        intervaloMarcacao.getCpfColaborador());
                if (codMarcacacaoVinculo == null || codMarcacacaoVinculo == 0) {
                    final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
                    insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_FIM);
                    return codMarcacaoInserida;
                } else {
                    final Long codMarcacaoInserida = insertMarcacao(conn, intervaloMarcacao);
                    insereMarcacaoInicioOuFim(conn, codMarcacaoInserida, TipoInicioFim.MARCACAO_FIM);
                    insereVinculoInicioFim(conn, codMarcacacaoVinculo, codMarcacaoInserida);
                    insereMarcacaoInconsistenteSeExistir(conn, codMarcacacaoVinculo, codMarcacaoInserida);
                    return codMarcacaoInserida;
                }
            }
        }
    }

    private void insereMarcacaoInconsistenteSeExistir(@NotNull final Connection conn,
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

    @Nullable
    private Long buscaMarcacaoInicioVinculo(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codTipoIntervalo,
                                            @NotNull final Long cpfColaborador) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_BUSCA_MARCACAO_VICULO_BY_MARCACAO(?, ?, ?) AS CODIGO;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codTipoIntervalo);
            stmt.setLong(3, cpfColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codigo = rSet.getLong("CODIGO");
                // retornamos null caso codigo == 0 pois significa que não existe um código de vínculo
                return codigo != 0 ? codigo : null;
            } else{
                throw new SQLException("Erro ao buscar código de vínculo para a marcação");
            }
        } finally {
            close(rSet, stmt);
        }
    }

    @NotNull
    private Long insertMarcacao(@NotNull final Connection conn,
                                @NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_MARCACAO_INSERT_MARCACAO_JORNADA(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) AS CODIGO;");
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
            stmt.setTimestamp(11, Now.timestampUtc());
            bindValueOrNull(stmt, 12, intervaloMarcacao.getVersaoAppMomentoMarcacao(), SqlType.INTEGER);
            bindValueOrNull(stmt, 13, intervaloMarcacao.getVersaoAppMomentoSincronizacao(), SqlType.INTEGER);
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

    private void associaCargosTipoIntervalo(@NotNull final Connection conn,
                                            @NotNull final TipoMarcacao tipoIntervalo) throws SQLException {
        deleteCargosTipoIntervalo(
                conn,
                tipoIntervalo.getUnidade().getCodigo(),
                tipoIntervalo.getCodigo());
        insertCargosTipoIntervalo(
                conn,
                tipoIntervalo.getUnidade().getCodigo(),
                tipoIntervalo.getCodigo(),
                tipoIntervalo.getCargos());
    }

    private void deleteCargosTipoIntervalo(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codTipoIntervalo) throws SQLException {
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
            close(stmt);
        }
    }

    private void insertCargosTipoIntervalo(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codTipoIntervalo,
                                           @NotNull final List<Cargo> cargos) throws SQLException {
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
            close(stmt);
        }
    }

    @NotNull
    private TipoMarcacao createTipoInvervalo(@NotNull final Connection conn,
                                             @NotNull final ResultSet rSet,
                                             final boolean withCargos) throws SQLException {
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
            tipoIntervalo.setCargos(getCargosByTipoIntervalo(conn, tipoIntervalo));
        }
        return tipoIntervalo;
    }

    @NotNull
    private List<Cargo> getCargosByTipoIntervalo(@NotNull final Connection conn,
                                                 @NotNull final TipoMarcacao tipoIntervalo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Cargo> cargos = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT F.* FROM " +
                    "  INTERVALO_TIPO_CARGO ITC JOIN UNIDADE U ON U.CODIGO = ITC.COD_UNIDADE " +
                    "JOIN FUNCAO F ON F.COD_EMPRESA = U.COD_EMPRESA AND F.CODIGO = ITC.COD_CARGO " +
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
        intervaloMarcacao.setCodMarcacaoPorUnidade(rSet.getLong("COD_MARCACAO_POR_UNIDADE"));
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
}