package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

    public ChecklistDaoImpl() {

    }

    @NotNull
    @Override
    public Long insert(Checklist checklist) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final OrdemServicoDao osDao = Injection.provideOrdemServicoDao();
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
                    + "(COD_UNIDADE,COD_CHECKLIST_MODELO, DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, " +
                    "KM_VEICULO, TEMPO_REALIZACAO) "
                    + "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?,?,?,?) RETURNING CODIGO, " +
                    "COD_UNIDADE");
            stmt.setString(1, checklist.getPlacaVeiculo());
            stmt.setLong(2, checklist.getCodModelo());
            stmt.setObject(3, checklist.getData().atOffset(ZoneOffset.UTC));
            stmt.setLong(4, checklist.getColaborador().getCpf());
            stmt.setString(5, checklist.getPlacaVeiculo());
            stmt.setString(6, String.valueOf(checklist.getTipo()));
            stmt.setLong(7, checklist.getKmAtualVeiculo());
            stmt.setLong(8, checklist.getTempoRealizacaoCheckInMillis());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                checklist.setCodigo(rSet.getLong("CODIGO"));
                final Long codUnidade = rSet.getLong("cod_unidade");
                insertRespostas(checklist, conn);
                osDao.insertItemOs(checklist, conn, codUnidade);
                veiculoDao.updateKmByPlaca(checklist.getPlacaVeiculo(), checklist.getKmAtualVeiculo(), conn);
                conn.commit();
                return checklist.getCodigo();
            } else {
                throw new SQLException("Erro ao inserir o checklist");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Checklist getByCod(Long codChecklist, String userToken) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  C.CODIGO, " +
                    "  C.COD_CHECKLIST_MODELO, " +
                    "  C.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                    "  C.KM_VEICULO, " +
                    "  C.TEMPO_REALIZACAO, " +
                    "  C.CPF_COLABORADOR, " +
                    "  C.PLACA_VEICULO, " +
                    "  C.TIPO, CO.NOME " +
                    "FROM CHECKLIST C " +
                    "  JOIN COLABORADOR CO " +
                    "    ON CO.CPF = C.CPF_COLABORADOR " +
                    "WHERE C.CODIGO = ?;");
            stmt.setString(1, TimeZoneManager.getZoneIdForToken(userToken, conn).getId());
            stmt.setLong(2, codChecklist);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Checklist checklist = ChecklistConverter.createChecklist(rSet, false);
                checklist.setListRespostas(getPerguntasRespostas(checklist));
                return checklist;
            } else {
                throw new IllegalStateException("Checklist com o código: " + codChecklist + " não encontrado!");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
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
            closeConnection(conn, stmt, rSet);
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa, char
            tipoChecklis) throws SQLException {
        final NovoChecklistHolder holder = new NovoChecklistHolder();
        final ChecklistModeloDao checklistModeloDaoImpl = Injection.provideChecklistModeloDao();
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        holder.setCodigoModeloChecklist(codModelo);
        holder.setListPerguntas(checklistModeloDaoImpl.getPerguntas(codUnidade, codModelo));
        holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, false));
        return holder;
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<ModeloChecklist, List<String>> modeloPlaca = new LinkedHashMap<>();
        ModeloChecklist modelo = null;
        List<String> placas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CM.CODIGO, CM.NOME, V.PLACA, V.KM FROM "
                            + "CHECKLIST_MODELO CM "
                            + "JOIN CHECKLIST_MODELO_FUNCAO CMF ON CMF.COD_CHECKLIST_MODELO = CM.CODIGO AND CM" +
                            ".COD_UNIDADE = CMF.COD_UNIDADE "
                            + "JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CMVT.COD_MODELO = CM.CODIGO AND CMVT" +
                            ".COD_UNIDADE = CM.COD_UNIDADE "
                            + "JOIN VEICULO_TIPO VT ON VT.CODIGO = CMVT.COD_TIPO_VEICULO "
                            + "JOIN VEICULO V ON V.COD_TIPO = VT.CODIGO "
                            + "WHERE CM.COD_UNIDADE = ? AND CMF.COD_FUNCAO = ? AND CM.STATUS_ATIVO = TRUE AND V" +
                            ".STATUS_ATIVO = TRUE "
                            + "ORDER BY CM.CODIGO, V.PLACA;",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codFuncao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                // Primeira linha do Rset, cria o modelo, add a primeira placa.
                if (modelo == null) {
                    modelo = new ModeloChecklist();
                    modelo.setCodigo(rSet.getLong("CODIGO"));
                    modelo.setNome(rSet.getString("NOME"));
                    placas.add(rSet.getString("PLACA"));
                } else {
                    // Verificar se o prox modelo é igual ao ja criado.
                    if (modelo.getCodigo().equals(rSet.getLong("CODIGO"))) {
                        placas.add(rSet.getString("PLACA"));
                    } else {
                        // Modelo diferente, deve setar adicionar tudo ao map e zerar os valores.
                        modeloPlaca.put(modelo, placas);
                        modelo = new ModeloChecklist();
                        placas = new ArrayList<>();
                        modelo.setCodigo(rSet.getLong("CODIGO"));
                        modelo.setNome(rSet.getString("NOME"));
                        placas.add(rSet.getString("PLACA"));
                    }
                }
            }
            if (modelo != null) {
                modeloPlaca.put(modelo, placas);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modeloPlaca;
    }

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
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setObject(1, dataInicial);
            stmt.setObject(2, dataFinal);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, zoneId);
            rSet = stmt.executeQuery();
            return ChecklistConverter.createFarolChecklist(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private void insertRespostas(Checklist checklist, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS "
                    + "(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA, RESPOSTA) "
                    + "VALUES ((SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA=?), ?, ?, ?, ?, ?)");
            for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
                stmt.setString(1, checklist.getPlacaVeiculo());
                stmt.setLong(2, checklist.getCodModelo());
                stmt.setLong(3, checklist.getCodigo());
                stmt.setLong(4, resposta.getCodigo());
                for (AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                    stmt.setLong(5, alternativa.codigo);
                    //se a alternativa esta selecionada
                    if (alternativa.selected) {
                        // se a alternativa é do tipo Outros
                        if (alternativa.tipo == AlternativaChecklist.TIPO_OUTROS) {
                            // salva a resposta escrita do usuário
                            stmt.setString(6, alternativa.respostaOutros);
                        } else {
                            // se a alternativa esta MARCADA e não é do tipo Outros
                            stmt.setString(6, "NOK");
                        }
                        // alternativa esta desmarcada
                    } else {
                        // salva OK, indicando que o item NÃO tem problema
                        stmt.setString(6, "OK");
                    }
                    int count = stmt.executeUpdate();
                    if (count == 0) {
                        throw new SQLException("Erro ao inserir resposta");
                    }
                }
            }
        } finally {
            closeStatement(stmt);
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
                            "  CP.CODIGO AS COD_PERGUNTA, " +
                            "  CP.ORDEM AS ORDEM_PERGUNTA, " +
                            "  CP.PERGUNTA, " +
                            "  CP.SINGLE_CHOICE, " +
                            "  CAP.CODIGO AS COD_ALTERNATIVA, " +
                            "  CP.PRIORIDADE, " +
                            "  CAP.ORDEM, " +
                            "  CGI.COD_IMAGEM, " +
                            "  CGI.URL_IMAGEM, " +
                            "  CAP.ALTERNATIVA, " +
                            "  CR.RESPOSTA " +
                            "FROM CHECKLIST C " +
                            "  JOIN CHECKLIST_RESPOSTAS CR " +
                            "    ON C.CODIGO = CR.COD_CHECKLIST " +
                            "       AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO " +
                            "       AND C.COD_UNIDADE = CR.COD_UNIDADE " +
                            "  JOIN CHECKLIST_PERGUNTAS CP " +
                            "    ON CP.CODIGO = CR.COD_PERGUNTA " +
                            "       AND CP.COD_UNIDADE = CR.COD_UNIDADE " +
                            "       AND CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO " +
                            "       AND CP.CODIGO = CR.COD_PERGUNTA " +
                            "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP " +
                            "    ON CAP.CODIGO = CR.COD_ALTERNATIVA " +
                            "       AND CAP.COD_UNIDADE = CR.COD_UNIDADE " +
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
            closeConnection(conn, stmt, rSet);
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