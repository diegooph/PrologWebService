package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklistStatus;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.RegionalSelecaoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura.ChecklistMigracaoEstruturaSuporte;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

public final class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

    public ChecklistDaoImpl() {

    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final ChecklistInsercao checklist,
                       final boolean foiOffline,
                       final boolean deveAbrirOs) throws Throwable {
        return internalInsertChecklist(conn, checklist, foiOffline, deveAbrirOs);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final ChecklistInsercao checklist,
                       final boolean foiOffline,
                       final boolean deveAbrirOs) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codChecklist = internalInsertChecklist(conn, checklist, foiOffline, deveAbrirOs);
            conn.commit();
            return codChecklist;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }

            throw t;
        } finally {
            close(conn);
        }
    }

    @NotNull
    private Long internalInsertChecklist(@NotNull final Connection conn,
                                         @NotNull final ChecklistInsercao checklist,
                                         final boolean foiOffline,
                                         final boolean deveAbrirOs) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {

            // Isso é necessário pois apps antigos não tem a versão do modelo de checklist e portanto nós não recebemos
            // ela. Nessa etapa, com base nas perguntas e alternativas recebidas, iremos tentar adivinhar qual a versão
            // com fall back para 1 se não for encontrada.
            if (!ChecklistMigracaoEstruturaSuporte.isAppNovaEstruturaChecklist(checklist)) {
                checklist.setCodVersaoModeloChecklist(ChecklistMigracaoEstruturaSuporte
                        .encontraCodVersaoModeloChecklist(conn, checklist));
            }

            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(" +
                    "F_COD_UNIDADE_CHECKLIST              := ?," +
                    "F_COD_MODELO_CHECKLIST               := ?," +
                    "F_COD_VERSAO_MODELO_CHECKLIST        := ?," +
                    "F_DATA_HORA_REALIZACAO               := ?," +
                    "F_COD_COLABORADOR                    := ?," +
                    "F_COD_VEICULO                        := ?," +
                    "F_PLACA_VEICULO                      := ?," +
                    "F_TIPO_CHECKLIST                     := ?," +
                    "F_KM_COLETADO                        := ?," +
                    "F_TEMPO_REALIZACAO                   := ?," +
                    "F_DATA_HORA_SINCRONIZACAO            := ?," +
                    "F_FONTE_DATA_HORA_REALIZACAO         := ?," +
                    "F_VERSAO_APP_MOMENTO_REALIZACAO      := ?," +
                    "F_VERSAO_APP_MOMENTO_SINCRONIZACAO   := ?," +
                    "F_DEVICE_ID                          := ?," +
                    "F_DEVICE_IMEI                        := ?," +
                    "F_DEVICE_UPTIME_REALIZACAO_MILLIS    := ?," +
                    "F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS := ?," +
                    "F_FOI_OFFLINE                        := ?," +
                    "F_TOTAL_PERGUNTAS_OK                 := ?," +
                    "F_TOTAL_PERGUNTAS_NOK                := ?," +
                    "F_TOTAL_ALTERNATIVAS_OK              := ?," +
                    "F_TOTAL_ALTERNATIVAS_NOK             := ?) " +
                    "AS CODIGO;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(checklist.getCodUnidade(), conn);
            stmt.setLong(1, checklist.getCodUnidade());
            stmt.setLong(2, checklist.getCodModelo());
            stmt.setLong(3, checklist.getCodVersaoModeloChecklist());
            stmt.setObject(4, checklist.getDataHoraRealizacao().atZone(zoneId).toOffsetDateTime());
            stmt.setLong(5, checklist.getCodColaborador());
            stmt.setLong(6, checklist.getCodVeiculo());
            stmt.setString(7, checklist.getPlacaVeiculo());
            stmt.setString(8, String.valueOf(checklist.getTipo().asChar()));
            stmt.setLong(9, checklist.getKmColetadoVeiculo());
            stmt.setLong(10, checklist.getTempoRealizacaoCheckInMillis());
            stmt.setObject(11, Now.offsetDateTimeUtc());
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
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codChecklistInserido = rSet.getLong("CODIGO");

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
                if (deveAbrirOs) {
                    Injection
                            .provideOrdemServicoDao()
                            .processaChecklistRealizado(conn, codChecklistInserido, checklist);
                }

                return codChecklistInserido;
            } else {
                throw new SQLException("Erro ao salvar checklist");
            }
        } finally {
            close(stmt, rSet);
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
    @Override
    public Checklist getByCod(@NotNull final Long codChecklist) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_BY_CODIGO(F_COD_CHECKLIST := ?);");
            stmt.setLong(1, codChecklist);
            rSet = stmt.executeQuery();
            PerguntaRespostaChecklist pergunta = null;
            Long codChecklistAntigo = null, codChecklistAtual;
            Long codPerguntaAntigo = null, codPerguntaAtual;
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

                if (isFirstLine) {
                    checklist = ChecklistConverter.createChecklist(rSet, false);
                    pergunta = ChecklistConverter.createPergunta(rSet);
                    pergunta.setAlternativasResposta(new ArrayList<>());
                    checklist.setListRespostas(new ArrayList<>());
                    checklist.getListRespostas().add(pergunta);
                    isFirstLine = false;
                }

                if (codChecklistAntigo.equals(codChecklistAtual)) {
                    if (codPerguntaAntigo.equals(codPerguntaAtual)) {
                        // Cria mais uma alternativa na pergunta atual.
                        pergunta.getAlternativasResposta().add(ChecklistConverter.createAlternativaComResposta(rSet));
                    } else {
                        // Cria nova pergunta.
                        pergunta = ChecklistConverter.createPergunta(rSet);
                        pergunta.setAlternativasResposta(new ArrayList<>());
                        checklist.getListRespostas().add(pergunta);

                        // Cria primeira alternativa da nova pergunta.
                        pergunta.getAlternativasResposta().add(ChecklistConverter.createAlternativaComResposta(rSet));
                    }
                } else {
                    throw new IllegalStateException(
                            "Esse método só está preparado para lidar com o retorno de um único checklist!");
                }
                codChecklistAntigo = codChecklistAtual;
                codPerguntaAntigo = codPerguntaAtual;
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
    public List<Checklist> getAll(@NotNull final Long codUnidade,
                                  @Nullable final Long codEquipe,
                                  @Nullable final Long codTipoVeiculo,
                                  @Nullable final String placaVeiculo,
                                  final long dataInicial,
                                  final long dataFinal,
                                  final int limit,
                                  final long offset,
                                  final boolean resumido) throws SQLException {
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
            bindValueOrNull(stmt, 4, placaVeiculo, SqlType.VARCHAR);
            stmt.setDate(5, new java.sql.Date(dataInicial));
            stmt.setDate(6, new java.sql.Date(dataFinal));
            stmt.setString(7, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setInt(8, limit);
            stmt.setLong(9, offset);
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
                    "FROM FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(?, ?, ?, ?, ?, ?);");
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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_FAROL_CHECKLIST(?, ?, ?, ?, ?);");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setBoolean(4, itensCriticosRetroativos);
            stmt.setString(5, zoneId);
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

    @NotNull
    @Override
    public Map<Long, AlternativaChecklistStatus> getItensStatus(@NotNull final Connection conn,
                                                                @NotNull final Long codModelo,
                                                                @NotNull final String placaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS(?, ?);");
            stmt.setLong(1, codModelo);
            stmt.setString(2, placaVeiculo);
            rSet = stmt.executeQuery();
            final Map<Long, AlternativaChecklistStatus> alternativas = new HashMap<>();
            while (rSet.next()) {
                alternativas.put(
                        rSet.getLong("COD_ALTERNATIVA"),
                        ChecklistConverter.createAlternativaChecklistStatus(rSet));
            }
            return alternativas;
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertRespostasNok(@NotNull final Connection conn,
                                    @NotNull final Long codUnidade,
                                    @NotNull final Checklist checklist) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS_NOK (COD_UNIDADE, COD_CHECKLIST_MODELO, " +
                    "COD_VERSAO_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA, RESPOSTA_OUTROS)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");
            for (final PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
                stmt.setLong(1, codUnidade);
                stmt.setLong(2, checklist.getCodModelo());
                stmt.setLong(2, checklist.getCodVersaoModeloChecklist());
                stmt.setLong(3, checklist.getCodigo());
                stmt.setLong(4, resposta.getCodigo());
                for (final AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                    if (alternativa.isSelected()) {
                        stmt.setLong(5, alternativa.getCodigo());
                        // Se a alternativa é do tipo Outros.
                        if (alternativa.getTipo() == AlternativaChecklist.TIPO_OUTROS) {
                            // Salva a resposta escrita do usuário.
                            stmt.setString(6, StringUtils.trimToNull(alternativa.respostaOutros));
                        }
                        if (stmt.executeUpdate() == 0) {
                            throw new SQLException("Erro ao inserir resposta.");
                        }
                    }
                }
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
                            "CP.CODIGO              AS COD_PERGUNTA," +
                            "CP.ORDEM               AS ORDEM_PERGUNTA," +
                            "CP.PERGUNTA            AS DESCRICAO_PERGUNTA," +
                            "CP.SINGLE_CHOICE       AS PERGUNTA_SINGLE_CHOICE," +
                            "CAP.CODIGO             AS COD_ALTERNATIVA," +
                            "CAP.PRIORIDADE :: TEXT AS PRIORIDADE_ALTERNATIVA," +
                            "CAP.ORDEM              AS ORDEM_ALTERNATIVA," +
                            "CAP.ALTERNATIVA        AS DESCRICAO_ALTERNATIVA," +
                            "CGI.COD_IMAGEM         AS COD_IMAGEM," +
                            "CGI.URL_IMAGEM         AS URL_IMAGEM," +
                            "CR.RESPOSTA            AS RESPOSTA " +
                            "FROM CHECKLIST C " +
                            "  JOIN CHECKLIST_RESPOSTAS CR " +
                            "    ON C.CODIGO = CR.COD_CHECKLIST " +
                            "       AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO " +
                            "       AND C.COD_UNIDADE = CR.COD_UNIDADE " +
                            "  JOIN CHECKLIST_PERGUNTAS CP " +
                            "    ON CP.CODIGO = CR.COD_PERGUNTA " +
                            "       AND CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO " +
                            "       AND CP.CODIGO = CR.COD_PERGUNTA " +
                            "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP " +
                            "    ON CAP.CODIGO = CR.COD_ALTERNATIVA " +
                            "       AND CAP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO " +
                            "       AND CAP.COD_PERGUNTA = CR.COD_PERGUNTA " +
                            "  LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
                            "    ON CP.COD_IMAGEM = CGI.COD_IMAGEM " +
                            "WHERE C.CODIGO = ? AND C.CPF_COLABORADOR = ? " +
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