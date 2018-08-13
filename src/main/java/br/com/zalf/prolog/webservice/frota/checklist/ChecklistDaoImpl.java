package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

    public ChecklistDaoImpl() {

    }

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
                final Checklist checklist = ChecklistConverter.createChecklist(rSet);
                checklist.setListRespostas(getPerguntasRespostas(checklist));
                return checklist;
            } else {
                throw new IllegalStateException("Checklist com o código: " + codChecklist + " não encontrado!");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Nonnull
    @Override
    public List<Checklist> getAll(@Nonnull Long codUnidade,
                                  @Nullable Long codEquipe,
                                  @Nullable Long codTipoVeiculo,
                                  @Nullable String placaVeiculo,
                                  long dataInicial,
                                  long dataFinal,
                                  int limit,
                                  long offset,
                                  boolean resumido) throws SQLException {
        final List<Checklist> checklists = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA AT TIME ZONE ? AS DATA_HORA, "
                    + "C.cod_checklist_modelo, C.KM_VEICULO, "
                    + "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
                    + "C.TIPO, CO.NOME FROM CHECKLIST C "
                    + "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
                    + "JOIN EQUIPE E ON E.CODIGO = CO.COD_EQUIPE "
                    + "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO "
                    + "WHERE (C.DATA_HORA AT TIME ZONE ?)::DATE >= ? "
                    + "AND (C.DATA_HORA AT TIME ZONE ?)::DATE <= ? "
                    + "AND C.COD_UNIDADE = ? "
                    + "AND (? = 1 OR E.CODIGO = ?) "
                    + "AND (? = 1 OR V.COD_TIPO = ?) "
                    + "AND (? = 1 OR C.PLACA_VEICULO = ?)"
                    + "ORDER BY DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setString(2, zoneId);
            stmt.setDate(3, new java.sql.Date(dataInicial));
            stmt.setString(4, zoneId);
            stmt.setDate(5, new java.sql.Date(dataFinal));
            stmt.setLong(6, codUnidade);
            if (codEquipe == null) {
                stmt.setInt(7, 1);
                stmt.setInt(8, 1);
            } else {
                stmt.setInt(7, 0);
                stmt.setLong(8, codEquipe);
            }

            if (codTipoVeiculo == null) {
                stmt.setInt(9, 1);
                stmt.setInt(10, 1);
            } else {
                stmt.setInt(9, 0);
                stmt.setLong(10, codTipoVeiculo);
            }

            if (placaVeiculo == null) {
                stmt.setInt(11, 1);
                stmt.setString(12, "");
            } else {
                stmt.setInt(11, 0);
                stmt.setString(12, placaVeiculo);
            }
            stmt.setInt(13, limit);
            stmt.setLong(14, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Checklist checklist = ChecklistConverter.createChecklist(rSet);
                // TODO: Deve setar apenas se não for resumido, por enquanto está assim para podermos também setar
                // o total de OK e NOK.
//                if (!resumido) {
                    checklist.setListRespostas(getPerguntasRespostas(checklist));
//                }
                checklists.add(checklist);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return checklists;
    }

    @Override
    public List<Checklist> getByColaborador(Long cpf, @Nullable Long dataInicial, @Nullable Long dataFinal, int limit,
                                            long offset, boolean resumido) throws SQLException {
        List<Checklist> checklists = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  C.CODIGO, " +
                    "  C.COD_CHECKLIST_MODELO, " +
                    "  C.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                    "  C.CPF_COLABORADOR, " +
                    "  C.PLACA_VEICULO, " +
                    "  C.KM_VEICULO, " +
                    "  C.TIPO, " +
                    "  C.TEMPO_REALIZACAO, " +
                    "  CO.NOME " +
                    "FROM CHECKLIST C " +
                    "  JOIN COLABORADOR CO " +
                    "    ON CO.CPF = C.CPF_COLABORADOR " +
                    "WHERE C.CPF_COLABORADOR = ? " +
                    "      AND (? = 1 OR C.DATA_HORA::DATE >= (? AT TIME ZONE ?)) " +
                    "      AND (? = 1 OR C.DATA_HORA::DATE <= (? AT TIME ZONE ?)) " +
                    "ORDER BY C.DATA_HORA DESC " +
                    "LIMIT ? OFFSET ?");
            final String zoneId = TimeZoneManager.getZoneIdForCpf(cpf, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setLong(2, cpf);
            if (dataInicial == null || dataFinal == null) {
                stmt.setInt(3, 1);
                stmt.setNull(4, Types.DATE);
                stmt.setNull(5, Types.CHAR);
                stmt.setInt(6, 1);
                stmt.setNull(7, Types.DATE);
                stmt.setNull(8, Types.CHAR);
            } else {
                stmt.setInt(3, 0);
                stmt.setDate(4, new java.sql.Date(dataInicial));
                stmt.setString(5, zoneId);
                stmt.setInt(6, 0);
                stmt.setDate(7, new java.sql.Date(dataFinal));
                stmt.setString(8, zoneId);
            }
            stmt.setInt(9, limit);
            stmt.setLong(10, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Checklist checklist = ChecklistConverter.createChecklist(rSet);
                // TODO: Deve setar apenas se não for resumido, por enquanto está assim para podermos também setar
                // o total de OK e NOK.
//                if (!resumido) {
                    checklist.setListRespostas(getPerguntasRespostas(checklist));
//                }
                checklists.add(checklist);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return checklists;
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
    private List<PerguntaRespostaChecklist> getPerguntasRespostas(
            @NotNull final Checklist checklist) throws SQLException {
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
}