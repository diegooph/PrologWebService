package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.RegionalSelecaoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.alteracao_logica.ChecklistsAlteracaoAcaoData;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura.ChecklistMigracaoEstruturaSuporte;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

public final class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

    private static final String TAG = ChecklistDaoImpl.class.getSimpleName();

    public ChecklistDaoImpl() {

    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final ChecklistInsercao checklist,
                       final boolean foiOffline,
                       final boolean deveAbrirOs) throws Throwable {
        return internalInsertChecklist(conn, checklist, foiOffline, deveAbrirOs).getCodChecklist();
    }

    @NotNull
    @Override
    public Long insert(@NotNull final ChecklistInsercao checklist,
                       final boolean foiOffline,
                       final boolean deveAbrirOs) throws Throwable {
        return insertChecklist(checklist, foiOffline, deveAbrirOs).getCodChecklist();
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklist(@NotNull final Connection conn,
                                                  @NotNull final ChecklistInsercao checklist,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        return internalInsertChecklist(conn, checklist, foiOffline, deveAbrirOs);
    }

    @NotNull
    @Override
    public InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklist,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final InfosChecklistInserido infosChecklistInserido
                    = internalInsertChecklist(conn, checklist, foiOffline, deveAbrirOs);
            conn.commit();
            return infosChecklistInserido;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }

            throw t;
        } finally {
            close(conn);
        }
    }

    @Override
    public void insertMidiaPerguntaChecklistRealizado(@NotNull final String uuidMidia,
                                                      @NotNull final Long codChecklist,
                                                      @NotNull final Long codPergunta,
                                                      @NotNull final String urlMidia) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{call func_checklist_insert_midia_pergunta(" +
                    "f_uuid_midia => ?::uuid, " +
                    "f_cod_checklist => ?, " +
                    "f_cod_pergunta => ?, " +
                    "f_url_midia => ?)}");
            stmt.setString(1, uuidMidia);
            stmt.setLong(2, codChecklist);
            stmt.setLong(3, codPergunta);
            stmt.setString(4, urlMidia);
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void insertMidiaAlternativaChecklistRealizado(@NotNull final String uuidMidia,
                                                         @NotNull final Long codChecklist,
                                                         @NotNull final Long codAlternativa,
                                                         @NotNull final String urlMidia) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{call func_checklist_insert_midia_alternativa(" +
                    "f_uuid_midia => ?::uuid, " +
                    "f_cod_checklist => ?, " +
                    "f_cod_alternativa => ?, " +
                    "f_url_midia => ?)}");
            stmt.setString(1, uuidMidia);
            stmt.setLong(2, codChecklist);
            stmt.setLong(3, codAlternativa);
            stmt.setString(4, urlMidia);
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }

    @SuppressWarnings("WrapperTypeMayBePrimitive")
    @NotNull
    @Override
    public Checklist getByCod(@NotNull final Long codChecklist) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST => ?);");
            stmt.setLong(1, codChecklist);
            rSet = stmt.executeQuery();
            PerguntaRespostaChecklist pergunta = null;
            // Utilizamos objetos ao invés de tipos primitivos por ser o padrão Prolog.
            Long codChecklistAntigo = null, codChecklistAtual;
            Long codPerguntaAntigo = null, codPerguntaAtual;
            Long codAlternativaAntigo = null, codAlternativaAtual;
            Checklist checklist = null;
            boolean isFirstLine = true;
            while (rSet.next()) {
                codChecklistAtual = rSet.getLong("COD_CHECKLIST");
                if (codChecklistAntigo == null) {
                    codChecklistAntigo = codChecklistAtual;
                }

                codPerguntaAtual = rSet.getLong("COD_PERGUNTA");
                if (codPerguntaAntigo == null) {
                    codPerguntaAntigo = codPerguntaAtual;
                }

                codAlternativaAtual = rSet.getLong("COD_ALTERNATIVA");
                if (codAlternativaAntigo == null) {
                    codAlternativaAntigo = codAlternativaAtual;
                }

                if (isFirstLine) {
                    checklist = ChecklistConverter.createChecklist(rSet, false);
                    pergunta = ChecklistConverter.createPergunta(rSet);
                    pergunta.setAlternativasResposta(new ArrayList<>());
                    pergunta.setMidias(new ArrayList<>());
                    checklist.setListRespostas(new ArrayList<>());
                    checklist.getListRespostas().add(pergunta);
                    isFirstLine = false;
                }

                if (codChecklistAntigo.equals(codChecklistAtual)) {
                    if (codPerguntaAntigo.equals(codPerguntaAtual)) {
                        if (rSet.getBoolean("TEM_MIDIA_PERGUNTA_OK")) {
                            final String uuidMidiaPerguntaOk = rSet.getString("UUID_MIDIA_PERGUNTA_OK");
                            if (!pergunta.temMidia(uuidMidiaPerguntaOk)) {
                                pergunta.addMidia(ChecklistConverter.createMidiaPergunta(rSet));
                            }
                        }

                        if (codAlternativaAntigo.equals(codAlternativaAtual)) {
                            final AlternativaChecklist alternativa = pergunta.getUltimaAlternativa();
                            if (alternativa == null) {
                                final AlternativaChecklist criada = pergunta
                                        .addAlternativa(ChecklistConverter.createAlternativaComResposta(rSet));
                                addMidiaAlternativaSeExistir(rSet, criada);
                            } else if (alternativa.getCodigo().equals(codAlternativaAtual)) {
                                addMidiaAlternativaSeExistir(rSet, alternativa);
                            }
                        } else {
                            // Cria mais uma alternativa na pergunta atual.
                            final AlternativaChecklist criada = pergunta
                                    .addAlternativa(ChecklistConverter.createAlternativaComResposta(rSet));
                            addMidiaAlternativaSeExistir(rSet, criada);
                        }

                    } else {
                        // Cria nova pergunta.
                        pergunta = ChecklistConverter.createPergunta(rSet);
                        pergunta.setAlternativasResposta(new ArrayList<>());
                        pergunta.setMidias(new ArrayList<>());

                        if (rSet.getBoolean("TEM_MIDIA_PERGUNTA_OK")) {
                            pergunta.addMidia(ChecklistConverter.createMidiaPergunta(rSet));
                        }

                        checklist.getListRespostas().add(pergunta);

                        // Cria primeira alternativa da nova pergunta.
                        final AlternativaChecklist criada = pergunta
                                .addAlternativa(ChecklistConverter.createAlternativaComResposta(rSet));
                        addMidiaAlternativaSeExistir(rSet, criada);
                    }
                } else {
                    throw new IllegalStateException(
                            "Esse método só está preparado para lidar com o retorno de um único checklist!");
                }
                codChecklistAntigo = codChecklistAtual;
                codPerguntaAntigo = codPerguntaAtual;
                codAlternativaAntigo = codAlternativaAtual;
            }

            if (checklist == null) {
                throw new IllegalStateException("Nenhum checklist encontrado com o código: " + codChecklist);
            }

            // Agora que já acabamos de criar, podemos forçar a contagem de itens OK/NOK a acontecer.
            checklist.calculaQtdOkOrNok();

            // Como a busca é feita ordenando pelo código, antes de retornar para o front nós ordenamos pela ordem de
            // exibição das perguntas. Ignoramos a ordem de exibição das alternativas, não vale o overhead pelo que se
            // ganha, atualmente, em exibição no front.
            // O motivo de ordenarmos a busca pelo código ao invés de já direto pela ordem de exibição, é que atualmente
            // a tabela de perguntas e alternativas não possuem nenhuma constraint que impeça a ordem de exibição de se
            // repetir.
            checklist
                    .getListRespostas()
                    .sort(Comparator.comparing(PerguntaRespostaChecklist::getOrdemExibicao));

            return checklist;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ChecklistListagem> getListagemByColaborador(@NotNull final Long codColaborador,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal,
                                                            final int limit,
                                                            final long offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(F_COD_COLABORADOR := ?, F_DATA_INICIAL := ?," +
                    "F_DATA_FINAL := ?, F_TIMEZONE := ?, F_LIMIT := ?, F_OFFSET := ?);");
            stmt.setLong(1, codColaborador);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setString(4, TimeZoneManager.getZoneIdForCodColaborador(codColaborador, conn).getId());
            stmt.setInt(5, limit);
            stmt.setLong(6, offset);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<ChecklistListagem> checklists = new ArrayList<>();
                do {
                    checklists.add(ChecklistConverter.createChecklistListagem(rSet));
                } while (rSet.next());
                return checklists;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ChecklistListagem> getListagem(@NotNull final Long codUnidade,
                                               @Nullable final Long codEquipe,
                                               @Nullable final Long codTipoVeiculo,
                                               @Nullable final Long codVeiculo,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final int limit,
                                               final long offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, codUnidade);
            bindValueOrNull(stmt, 2, codEquipe, SqlType.BIGINT);
            bindValueOrNull(stmt, 3, codTipoVeiculo, SqlType.BIGINT);
            bindValueOrNull(stmt, 4, codVeiculo, SqlType.BIGINT);
            stmt.setObject(5, dataInicial);
            stmt.setObject(6, dataFinal);
            stmt.setString(7, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setInt(8, limit);
            stmt.setLong(9, offset);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<ChecklistListagem> checklists = new ArrayList<>();
                do {
                    checklists.add(ChecklistConverter.createChecklistListagem(rSet));
                } while (rSet.next());
                return checklists;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public FiltroRegionalUnidadeChecklist getRegionaisUnidadesSelecao(@NotNull final Long codColaborador)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_GET_REGIONAIS_UNIDADES_SELECAO(F_COD_COLABORADOR := ?);");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            FiltroRegionalUnidadeChecklist filtro = null;
            RegionalSelecaoChecklist regional = null;
            Long codRegionalAntiga = null, codRegionalAtual;
            boolean isFirstLine = true;
            while (rSet.next()) {
                codRegionalAtual = rSet.getLong("CODIGO_REGIONAL");
                if (codRegionalAntiga == null) {
                    codRegionalAntiga = codRegionalAtual;
                }

                if (isFirstLine) {
                    filtro = new FiltroRegionalUnidadeChecklist(
                            codColaborador,
                            new ArrayList<>(),
                            !rSet.getBoolean("REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA"));
                    regional = ChecklistConverter.createRegionalSelecao(rSet, new ArrayList<>());
                    filtro.getRegionaisSelecao().add(regional);
                    isFirstLine = false;
                }

                if (codRegionalAntiga.equals(codRegionalAtual)) {
                    regional.getUnidadesVinculadas().add(ChecklistConverter.createUnidadeSelecao(rSet));
                } else {
                    regional = ChecklistConverter.createRegionalSelecao(rSet, new ArrayList<>());
                    regional.getUnidadesVinculadas().add(ChecklistConverter.createUnidadeSelecao(rSet));
                    filtro.getRegionaisSelecao().add(regional);
                }
                codRegionalAntiga = codRegionalAtual;
            }

            if (filtro == null) {
                throw new IllegalStateException(
                        "Dados de filtro não encontrados para o colaborador: " + codColaborador);
            }

            return filtro;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_FAROL_CHECKLIST(?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setBoolean(4, itensCriticosRetroativos);
            rSet = stmt.executeQuery();
            return ChecklistConverter.createFarolChecklist(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean getChecklistDiferentesUnidadesAtivoEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_REALIZACAO_DIFERENTES_UNIDADES_EMPRESA_BLOQUEADA(F_COD_EMPRESA := ?);");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return !rSet.getBoolean("REALIZACAO_CHECKLIST_DIFERENTES_UNIDADES_BLOQUEADO_EMPRESA");
            } else {
                throw new SQLException("Erro ao verificar se a empresa está bloqueada para realizar checklist de " +
                        "diferentes unidades");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @Deprecated
    public List<Checklist> getByColaborador(@NotNull final Long cpf,
                                            @NotNull final Long dataInicial,
                                            @NotNull final Long dataFinal,
                                            final int limit,
                                            final long offset,
                                            final boolean resumido) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR_DEPRECATED(?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, cpf);
            stmt.setDate(2, new java.sql.Date(dataInicial));
            stmt.setDate(3, new java.sql.Date(dataFinal));
            stmt.setString(4, TimeZoneManager.getZoneIdForCpf(cpf, conn).getId());
            stmt.setInt(5, limit);
            stmt.setLong(6, offset);
            rSet = stmt.executeQuery();
            final List<Checklist> checklists = new ArrayList<>();
            while (rSet.next()) {
                checklists.add(createChecklist(rSet, resumido));
            }
            return checklists;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void deleteLogicoChecklistsAndOs(@NotNull final ChecklistsAlteracaoAcaoData checkListsDelecao,
                                            @NotNull final Long codigoColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall(" {call func_checklist_deleta_checklist_e_os(" +
                    "f_cod_checklists => ?," +
                    "f_cod_colaborador => ?," +
                    "f_acao_executada => ?," +
                    "f_origem_delecao => ?," +
                    "f_observacao => ?," +
                    "f_data_hora_atual => ?)}");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, checkListsDelecao.getCodigos()));
            stmt.setLong(2, codigoColaborador);
            stmt.setString(3, checkListsDelecao.getAcaoExecutada().getValue());
            stmt.setString(4, OrigemAcaoEnum.PROLOG_WEB.toString());
            stmt.setString(5, StringUtils.trimToNull(checkListsDelecao.getObservacao()));
            stmt.setObject(6, Now.getOffsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }

    }

    @NotNull
    private InfosChecklistInserido internalInsertChecklist(@NotNull final Connection conn,
                                                           @NotNull final ChecklistInsercao checklist,
                                                           final boolean foiOffline,
                                                           final boolean deveAbrirOs) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {

            // Isso é necessário pois apps antigos não tem a versão do modelo de checklist e portanto nós não recebemos
            // ela. Nessa etapa buscamos o código da versão do modelo com base no código de uma alternativa
            if (!ChecklistMigracaoEstruturaSuporte.isAppNovaEstruturaChecklist(checklist)) {
                checklist.setCodVersaoModeloChecklist(ChecklistMigracaoEstruturaSuporte
                        .encontraCodVersaoModeloChecklist(conn, checklist));
            }

            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(" +
                    "F_COD_UNIDADE_CHECKLIST                   => ?," +
                    "F_COD_MODELO_CHECKLIST                    => ?," +
                    "F_COD_VERSAO_MODELO_CHECKLIST             => ?," +
                    "F_DATA_HORA_REALIZACAO                    => ?," +
                    "F_COD_COLABORADOR                         => ?," +
                    "F_COD_VEICULO                             => ?," +
                    "F_TIPO_CHECKLIST                          => ?," +
                    "F_KM_COLETADO                             => ?," +
                    "F_OBSERVACAO                              => ?," +
                    "F_TEMPO_REALIZACAO                        => ?," +
                    "F_DATA_HORA_SINCRONIZACAO                 => ?," +
                    "F_FONTE_DATA_HORA_REALIZACAO              => ?," +
                    "F_VERSAO_APP_MOMENTO_REALIZACAO           => ?," +
                    "F_VERSAO_APP_MOMENTO_SINCRONIZACAO        => ?," +
                    "F_DEVICE_ID                               => ?," +
                    "F_DEVICE_IMEI                             => ?," +
                    "F_DEVICE_UPTIME_REALIZACAO_MILLIS         => ?," +
                    "F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS      => ?," +
                    "F_FOI_OFFLINE                             => ?," +
                    "F_TOTAL_PERGUNTAS_OK                      => ?," +
                    "F_TOTAL_PERGUNTAS_NOK                     => ?," +
                    "F_TOTAL_ALTERNATIVAS_OK                   => ?," +
                    "F_TOTAL_ALTERNATIVAS_NOK                  => ?," +
                    "F_TOTAL_MIDIAS_PERGUNTAS_OK               => ?," +
                    "F_TOTAL_MIDIAS_ALTERNATIVAS_NOK           => ?) " +
                    "AS CODIGO;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(checklist.getCodUnidade(), conn);
            stmt.setLong(1, checklist.getCodUnidade());
            stmt.setLong(2, checklist.getCodModelo());
            stmt.setLong(3, checklist.getCodVersaoModeloChecklist());
            // Se foi um checklist offline salvamos a data/hora realização que recebemos do App. Senão, salvamos a
            // data/hora atual do servidor.
            // TODO: Se a data/hora do WS estiver incorreta, iremos salvar informação errada no check off.
            final OffsetDateTime dataHoraRealizacao = foiOffline
                    ? checklist.getDataHoraRealizacao().atZone(zoneId).toOffsetDateTime()
                    : Now.getOffsetDateTimeUtc();
            stmt.setObject(4, dataHoraRealizacao);
            stmt.setLong(5, checklist.getCodColaborador());
            stmt.setLong(6, checklist.getCodVeiculo());
            stmt.setString(7, String.valueOf(checklist.getTipo().asChar()));
            stmt.setLong(8, checklist.getKmColetadoVeiculo());
            stmt.setString(9, StringUtils.trimToNull(checklist.getObservacao()));
            stmt.setLong(10, checklist.getTempoRealizacaoCheckInMillis());
            stmt.setObject(11, Now.getOffsetDateTimeUtc());
            stmt.setString(12, checklist.getFonteDataHoraRealizacao().asString());
            stmt.setInt(13, checklist.getVersaoAppMomentoRealizacao());
            stmt.setInt(14, checklist.getVersaoAppMomentoSincronizacao());
            stmt.setString(15, checklist.getDeviceId());
            stmt.setString(16, checklist.getDeviceImei());
            stmt.setLong(17, checklist.getDeviceUptimeRealizacaoMillis());
            stmt.setLong(18, checklist.getDeviceUptimeSincronizacaoMillis());
            stmt.setBoolean(19, foiOffline);
            stmt.setInt(20, checklist.getQtdPerguntasOk());
            stmt.setInt(21, checklist.getQtdPerguntasNok());
            stmt.setInt(22, checklist.getQtdAlternativasOk());
            stmt.setInt(23, checklist.getQtdAlternativasNok());
            stmt.setInt(24, checklist.getQtdMidiasPerguntasOk());
            stmt.setInt(25, checklist.getQtdMidiasAlternativasNok());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codChecklistInserido = rSet.getLong("COD_CHECKLIST_INSERIDO");
                final boolean checklistJaExistia = rSet.getBoolean("CHECKLIST_JA_EXISTIA");

                if (checklistJaExistia) {
                    Log.d(TAG, "Checklist já existia, retornando apenas o código: " + codChecklistInserido);
                    // Possivelmente o último insert falhou a resposta para o app, então foi tentado inserir novamente.
                    return new InfosChecklistInserido(codChecklistInserido, checklistJaExistia, null);
                }

                // Só precisamos inserir as respostas se houver alguma NOK.
                if (checklist.getQtdAlternativasNok() > 0) {
                    insertChecklistRespostasNok(
                            conn,
                            checklist.getCodUnidade(),
                            checklist.getCodModelo(),
                            checklist.getCodVersaoModeloChecklist(),
                            codChecklistInserido,
                            checklist.getRespostas());
                }

                // Após inserir o checklist devemos abrir as Ordens de Serviços, caso necessário.
                Long codOs = null;
                if (deveAbrirOs) {
                    codOs = Injection
                            .provideOrdemServicoDao()
                            .processaChecklistRealizado(conn, codChecklistInserido, checklist);
                }

                return new InfosChecklistInserido(codChecklistInserido, checklistJaExistia, codOs);
            } else {
                throw new SQLException("Erro ao salvar checklist");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void addMidiaAlternativaSeExistir(@NotNull final ResultSet rSet,
                                              @NotNull final AlternativaChecklist alternativa) throws SQLException {
        if (rSet.getBoolean("TEM_MIDIA_ALTERNATIVA")) {
            alternativa.addMidia(ChecklistConverter.createMidiaAlternativa(rSet));
        }
    }

    private void insertChecklistRespostasNok(@NotNull final Connection conn,
                                             @NotNull final Long codUnidadeChecklist,
                                             @NotNull final Long codModeloChecklist,
                                             @NotNull final Long codVersaoModeloChecklist,
                                             @NotNull final Long codChecklistInserido,
                                             @NotNull final List<ChecklistResposta> respostas) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_INSERT_RESPOSTAS_CHECKLIST(" +
                            "F_COD_UNIDADE_CHECKLIST       := ?," +
                            "F_COD_MODELO_CHECKLIST        := ?," +
                            "F_COD_VERSAO_MODELO_CHECKLIST := ?," +
                            "F_COD_CHECKLIST               := ?," +
                            "F_COD_PERGUNTA                := ?," +
                            "F_COD_ALTERNATIVA             := ?," +
                            "F_RESPOSTA_OUTROS             := ?);");
            stmt.setLong(1, codUnidadeChecklist);
            stmt.setLong(2, codModeloChecklist);
            stmt.setLong(3, codVersaoModeloChecklist);
            stmt.setLong(4, codChecklistInserido);
            int linhasParaExecutar = 0;
            for (final ChecklistResposta resposta : respostas) {
                for (final ChecklistAlternativaResposta alternativa : resposta.getAlternativasRespostas()) {
                    if (alternativa.isAlternativaSelecionada()) {
                        stmt.setLong(5, resposta.getCodPergunta());
                        stmt.setLong(6, alternativa.getCodAlternativa());
                        if (alternativa.isTipoOutros()) {
                            stmt.setString(7, StringUtils.trimToNull(alternativa.getRespostaTipoOutros()));
                        } else {
                            stmt.setNull(7, SqlType.TEXT.asIntTypeJava());
                        }
                        stmt.addBatch();
                        linhasParaExecutar++;
                    }
                }
            }
            if (stmt.executeBatch().length != linhasParaExecutar) {
                throw new SQLException("Não foi possível salvar todas as respostas do checklist");
            }
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private List<PerguntaRespostaChecklist> getPerguntasRespostas(@NotNull final Checklist checklist)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                            "       CP.CODIGO                   AS COD_PERGUNTA," +
                            "       CP.ORDEM                    AS ORDEM_PERGUNTA," +
                            "       CP.PERGUNTA                 AS DESCRICAO_PERGUNTA," +
                            "       CP.SINGLE_CHOICE            AS PERGUNTA_SINGLE_CHOICE," +
                            "       CAP.CODIGO                  AS COD_ALTERNATIVA," +
                            "       CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA," +
                            "       CAP.ORDEM                   AS ORDEM_ALTERNATIVA," +
                            "       CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA," +
                            "       CAP.ALTERNATIVA_TIPO_OUTROS AS ALTERNATIVA_TIPO_OUTROS," +
                            "       CGI.COD_IMAGEM              AS COD_IMAGEM," +
                            "       CGI.URL_IMAGEM              AS URL_IMAGEM," +
                            "       CRN.CODIGO IS NOT NULL      AS ALTERNATIVA_SELECIONADA," +
                            "       CRN.RESPOSTA_OUTROS         AS RESPOSTA_OUTROS " +
                            "FROM CHECKLIST C " +
                            "         JOIN CHECKLIST_PERGUNTAS CP" +
                            "              ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO" +
                            "         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP" +
                            "              ON CAP.COD_PERGUNTA = CP.CODIGO" +
                            "         LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN" +
                            "                   ON C.CODIGO = CRN.COD_CHECKLIST" +
                            "                       AND CAP.CODIGO = CRN.COD_ALTERNATIVA" +
                            "         LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI" +
                            "                   ON CP.COD_IMAGEM = CGI.COD_IMAGEM " +
                            "WHERE C.CODIGO = ? " +
                            "  AND C.CPF_COLABORADOR = ? " +
                            "ORDER BY CP.ORDEM, CAP.ORDEM;",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, checklist.getCodigo());
            stmt.setLong(2, checklist.getColaborador().getCpf());
            rSet = stmt.executeQuery();
            return ChecklistConverter.createPerguntasRespostasChecklist(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private Checklist createChecklist(final ResultSet rSet, final boolean resumido) throws SQLException {
        final Checklist checklist = ChecklistConverter.createChecklist(rSet, true);
        if (!resumido) {
            checklist.setListRespostas(getPerguntasRespostas(checklist));
        }
        return checklist;
    }

}