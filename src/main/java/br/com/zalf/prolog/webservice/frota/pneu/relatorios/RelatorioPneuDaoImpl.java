package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Classe responsável por estratificar os dados dos pneus.
 *
 * @author jean
 */
public class RelatorioPneuDaoImpl extends DatabaseConnection implements RelatorioPneuDao {

    private static final String TAG = RelatorioPneuDaoImpl.class.getSimpleName();

    private static final String PNEUS_RESUMO_SULCOS = "SELECT COALESCE(ALTURA_SULCO_CENTRAL_INTERNO, ALTURA_SULCO_CENTRAL_INTERNO, -1) AS ALTURA_SULCO_CENTRAL FROM PNEU WHERE "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?])  ORDER BY 1 DESC";

    private static final String RESUMO_SERVICOS = "SELECT AD.DATA, CAL.CAL_ABERTAS, INSP.INSP_ABERTAS, MOV.MOV_ABERTAS, CAL_FECHADAS.CAL_FECHADAS, INSP_FECHADAS.INSP_FECHADAS, MOV_FECHADAS.MOV_FECHADAS FROM AUX_DATA AD LEFT JOIN "
            + "-- BUSCA AS CALIBRAGENS ABERTAS \n "
            + "(SELECT A.DATA_HORA::DATE AS DATA, COUNT(A.CODIGO) AS CAL_ABERTAS FROM "
            + "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
            + "WHERE "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
            + "GROUP BY A.DATA_HORA::DATE "
            + "ORDER BY A.DATA_HORA::DATE DESC) AS CAL ON CAL.DATA = AD.DATA "
            + "LEFT JOIN "
            + "-- BUSCA AS INSPEÇÕES ABERTAS \n "
            + "(SELECT A.DATA_HORA::DATE AS DATA, COUNT(A.CODIGO) AS INSP_ABERTAS FROM "
            + "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
            + "WHERE  "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
            + "GROUP BY A.DATA_HORA::DATE "
            + "ORDER BY A.DATA_HORA::DATE DESC) AS INSP ON INSP.DATA = AD.DATA "
            + "LEFT JOIN "
            + "-- BUSCA AS MOVIMENTAÇÕES ABERTAS \n "
            + "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS MOV_ABERTAS FROM "
            + "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
            + "WHERE  "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
            + "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
            + "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS MOV ON MOV.DATA = AD.DATA "
            + "LEFT JOIN "
            + "-- BUSCA AS CALIBRAGENS FECHADAS \n "
            + "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS CAL_FECHADAS FROM "
            + "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
            + "WHERE AM.DATA_HORA_RESOLUCAO IS NOT NULL AND "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
            + "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
            + "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS CAL_FECHADAS ON CAL_FECHADAS.DATA = AD.DATA "
            + "LEFT JOIN "
            + "-- BUSCA AS INSPEÇÕES FECHADAS \n "
            + "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS INSP_FECHADAS FROM "
            + "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
            + "WHERE AM.DATA_HORA_RESOLUCAO IS NOT NULL AND "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
            + "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
            + "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS INSP_FECHADAS ON INSP_FECHADAS.DATA = AD.DATA "
            + "LEFT JOIN "
            + "-- BUSCA AS MOVIMENTAÇÕES FECHADAS \n "
            + "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS MOV_FECHADAS FROM "
            + "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
            + "WHERE AM.DATA_HORA_RESOLUCAO IS NOT NULL AND "
            + "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
            + "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
            + "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS MOV_FECHADAS ON MOV_FECHADAS.DATA = AD.DATA "
            + "WHERE AD.DATA BETWEEN ? AND ? ";

    public RelatorioPneuDaoImpl() {

    }

    @Override
    public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status) throws SQLException {
        final List<Double> valores = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(PNEUS_RESUMO_SULCOS);
            stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                valores.add(rSet.getDouble("ALTURA_SULCO_CENTRAL"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        if (valores.isEmpty()) {
            return new ArrayList<>();

        } else {
            return getFaixas(valores);
        }
    }

    @Override
    public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        final List<Aderencia> aderencias = new ArrayList<>();
        Aderencia aderencia = null;
        final AfericaoDao afericaoDao = Injection.provideAfericaoDao();
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final Restricao restricao = afericaoDao.getRestricaoByCodUnidade(codUnidade);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.MONTH, mes - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        final Date dataInicial = new Date(calendar.getTimeInMillis());

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final Date dataFinal  = new Date(calendar.getTimeInMillis());

        final int ultimoDia;
        final int calendarYear = calendar.get(Calendar.YEAR);
        final int calendarMonth = calendar.get(Calendar.MONTH);
        /* verifica se o mes procurado é o mesmo mes corrente, se for, pega o dia atual, caso contrário
		 pega o ultimo dia do mês */
        if (calendarYear == ano && calendarMonth + 1 == mes) {
            ultimoDia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        } else {
            ultimoDia = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        try {
            conn = getConnection();
            final int totalVeiculos = veiculoDao.getTotalVeiculosByUnidade(codUnidade, conn);
            final double meta = totalVeiculos / restricao.getPeriodoDiasAfericaoPressao();
            stmt = conn.prepareStatement("SELECT EXTRACT(DAY from A.DATA_HORA) AS DIA, COUNT(EXTRACT(DAY from A.DATA_HORA)) AS REALIZADAS "
                    + "FROM AFERICAO A JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "WHERE A.DATA_HORA >=? AND A.DATA_HORA <= ? AND "
                    + "V.COD_UNIDADE = ? "
                    + "GROUP BY 1 "
                    + "ORDER BY 1");
            stmt.setDate(1, dataInicial);
            stmt.setDate(2, dataFinal);
            stmt.setLong(3, codUnidade);
            rSet = stmt.executeQuery();
            int dia = 1;
            while (rSet.next()) {
                while (dia < rSet.getInt("DIA")) {
                    aderencias.add(createAderencia(meta, dia));
                    dia++;
                }
                aderencia = new Aderencia();
                aderencia.setDia(rSet.getInt("DIA"));
                aderencia.setRealizadas(rSet.getInt("REALIZADAS"));
                aderencia.setMeta(meta);
                aderencias.add(aderencia);
                dia++;

                if (rSet.isLast()) {
                    while (dia <= ultimoDia) {
                        aderencias.add(createAderencia(meta, dia));
                        dia++;
                    }
                }
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return aderencias;
    }

    @Override
    public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Faixa> faixas = null;
        AfericaoDao afericaoDao = Injection.provideAfericaoDao();
        if (!codUnidades.get(0).equals("%")) {
            Restricao restricao = afericaoDao.getRestricaoByCodUnidade(Long.parseLong(codUnidades.get(0)));
            Integer base = (int) Math.round(restricao.getToleranciaCalibragem() * 100);
            faixas = criaFaixas(base, 30);
        } else {
            faixas = criaFaixas(0, 30);
        }
        List<Integer> valores = new ArrayList<>();
        int naoAferidos = 0;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COALESCE((((PRESSAO_ATUAL - PRESSAO_RECOMENDADA)/ PRESSAO_RECOMENDADA) *100)::TEXT, "
                    + "(((PRESSAO_ATUAL - PRESSAO_RECOMENDADA)/ PRESSAO_RECOMENDADA) *100)::TEXT, 'N') AS PORC  "
                    + "FROM PNEU  "
                    + "WHERE COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?]) "
                    + "ORDER BY 1 asc");
            stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                if (rSet.getString("PORC").equals("N")) {
                    naoAferidos++;
                } else {
                    valores.add((int) rSet.getDouble("PORC"));
                }
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        int totalValores = valores.size() + naoAferidos;
        populaFaixas(faixas, valores);
        setPorcentagemFaixas(faixas, totalValores);
        Faixa faixa = new Faixa();
        faixa.setNaoAferidos(true);
        faixa.setTotalPneus(naoAferidos);
        faixa.setPorcentagem((double) naoAferidos / (double) totalValores);
        faixas.add(faixa);
        return faixas;
    }

    @Override
    public List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        List<ResumoServicos> servicos = new ArrayList<>();
        ResumoServicos resumoDia = null;
        ResumoServicos.Servicos abertos = null;
        ResumoServicos.Servicos fechados = null;
        ResumoServicos.Servicos fechadosAc = null;
        ResumoServicos.Servicos abertosAc = null;

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.MONTH, mes - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        final Date dataInicial = new Date(calendar.getTimeInMillis());

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(RESUMO_SERVICOS);

            stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setString(2, TipoServico.CALIBRAGEM.asString());
            stmt.setArray(3, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setString(4, TipoServico.INSPECAO.asString());
            stmt.setArray(5, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setString(6, TipoServico.MOVIMENTACAO.asString());
            stmt.setArray(7, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setString(8, TipoServico.CALIBRAGEM.asString());
            stmt.setArray(9, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setString(10, TipoServico.INSPECAO.asString());
            stmt.setArray(11, PostgresUtil.ListToArray(conn, codUnidades));
            stmt.setString(12, TipoServico.MOVIMENTACAO.asString());
            stmt.setDate(13, dataInicial);
            stmt.setDate(14, DateUtils.toSqlDate(DateUtils.getUltimoDiaMes(dataInicial)));
            rSet = stmt.executeQuery();
            int tempCalFechadas = 0;
            int tempInspFechadas = 0;
            int tempMovFechadas = 0;
            int tempCalAbertas = 0;
            int tempInspAbertas = 0;
            int tempMovAbertas = 0;
            while (rSet.next()) {
                resumoDia = new ResumoServicos();
                abertos = new ResumoServicos.Servicos();
                fechados = new ResumoServicos.Servicos();
                fechadosAc = new ResumoServicos.Servicos();
                abertosAc = new ResumoServicos.Servicos();
                final Date data = rSet.getDate("DATA");
                calendar.setTime(data);
                resumoDia.setDia(calendar.get(Calendar.DAY_OF_MONTH));
                // quantidade de serviços abertos no dia
                abertos.calibragem = rSet.getInt("CAL_ABERTAS");
                abertos.inspecao = rSet.getInt("INSP_ABERTAS");
                abertos.movimentacao = rSet.getInt("MOV_ABERTAS");
                // quantidade de serviços fechados no dia
                fechados.calibragem = rSet.getInt("CAL_FECHADAS");
                fechados.inspecao = rSet.getInt("INSP_FECHADAS");
                fechados.movimentacao = rSet.getInt("MOV_FECHADAS");
                // contador do acumulado, conta quantos serviços no total ja foram fechados
                tempCalFechadas += rSet.getInt("CAL_FECHADAS");
                tempInspFechadas += rSet.getInt("INSP_FECHADAS");
                tempMovFechadas += rSet.getInt("MOV_FECHADAS");
                //contador do acumulado, conta quantos serviços estão em aberto no dia
                // leva em consideração o total aberto - o arrumado no dia
                tempCalAbertas = tempCalAbertas + abertos.calibragem - fechados.calibragem;
                tempInspAbertas = tempInspAbertas + abertos.inspecao - fechados.inspecao;
                tempMovAbertas = tempMovAbertas + abertos.movimentacao - fechados.movimentacao;
                // associa os valores com os temporários
                fechadosAc.calibragem = tempCalFechadas;
                fechadosAc.inspecao = tempInspFechadas;
                fechadosAc.movimentacao = tempMovFechadas;
                // associa os valores com os temporários
                abertosAc.calibragem = tempCalAbertas;
                abertosAc.inspecao = tempInspAbertas;
                abertosAc.movimentacao = tempMovAbertas;
                // seta os objetos internos do Resumo
                resumoDia.setAbertos(abertos);
                resumoDia.setFechados(fechados);
                resumoDia.setAcumuladoFechados(fechadosAc);
                resumoDia.setAcumuladoAbertos(abertosAc);
                servicos.add(resumoDia);
            }

        } finally {
            closeConnection(conn, stmt, rSet);
        }
        Log.d(TAG, servicos.toString());
        return servicos;
    }

    @Override
    public void getPrevisaoTrocaCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getPrevisaoTrocaConsolidadoCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
            throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaConsolidadoStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getPrevisaoTrocaConsolidadoReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaConsolidadoStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getPrevisaoTrocaStatement(Connection conn, long codUnidade, long dataInicial, Long dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("  SELECT * FROM func_relatorio_previsao_troca(?,?,?,?);");
        stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
        stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
        stmt.setLong(3, codUnidade);
        stmt.setString(4, Pneu.EM_USO);
        return stmt;
    }

    private PreparedStatement getPrevisaoTrocaConsolidadoStatement(Connection conn, long codUnidade, long dataInicial, Long dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
                "  to_char(VAP.\"PREVISÃO DE TROCA\", 'DD/MM/YYYY') AS \"DATA\",\n" +
                "  VAP.\"MARCA\",\n" +
                "  VAP.\"MODELO\",\n" +
                "  VAP.\"MEDIDAS\",\n" +
                "  COUNT(VAP.\"MODELO\") as \"QUANTIDADE\"\n" +
                "FROM\n" +
                "    -- Dentro dessa view uso as variáveis criadas no select interno para fazer as contas e formatação dos valores\n" +
                "    VIEW_ANALISE_PNEUS VAP\n" +
                "WHERE VAP.cod_unidade = ? and VAP.\"PREVISÃO DE TROCA\" BETWEEN ? AND ? AND VAP.\"STATUS PNEU\" = ?\n" +
                "GROUP BY VAP.\"PREVISÃO DE TROCA\", VAP.\"MARCA\",  VAP.\"MODELO\",  VAP.\"MEDIDAS\"\n" +
                "ORDER BY VAP.\"PREVISÃO DE TROCA\" ASC, 5 DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
        stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
        stmt.setString(4, Pneu.EM_USO);
        return stmt;
    }

    @Override
    public void getAderenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
            throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaPlacasStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaPlacasStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getAderenciaPlacasStatement(Connection conn, long codUnidade, long dataInicial, Long dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_pneu_aderencia_afericao(?,?,?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
        stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
        return stmt;
    }

    private List<Faixa> populaFaixas(List<Faixa> faixas, List<Integer> valores) {
        Collections.sort(valores);
        int integer = 0;
        // percorre todas as faixas
        for (Faixa faixa : faixas) {
            // percorre todos os valores
            for (int i = 0; i < valores.size(); i++) {
                integer = valores.get(i);
                // se a faixa começa com 0, veirica se é >= inicio e <= fim
                if (faixa.getInicio() == 0) {
                    if (integer >= faixa.getInicio() && integer <= faixa.getFim()) {
                        faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                        valores.remove(i);
                        i--;
                    }
                }
                // se a faixa for do lado negativo, a comparação se da de forma diferente >= inicio <fim
                else if (faixa.getInicio() < 0) {
                    // verifica se o valor esta apto a entrar na faixa
                    if (integer >= faixa.getInicio() && integer < faixa.getFim()) {
                        faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                        valores.remove(i);
                        i--;
                    }
                    // > inicio <= fim
                } else if (integer > faixa.getInicio() && integer <= faixa.getFim()) {
                    faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                    valores.remove(i);
                    i--;
                }
            }
        }
        Log.d(TAG, "Populadas: " + faixas.toString());
        return faixas;
    }

    private List<Faixa> criaFaixas(int base, int escala) {
        List<Faixa> faixas = new ArrayList<>();
        // cria a primeira faixa de 0 até a restrição imposta pela empresa (3% por exemplo)
        Faixa faixa = new Faixa();
        faixa.setInicio(0);
        faixa.setFim(base);
        faixas.add(faixa);
        // cria a primeira faixas negativa, que vai de -3 a 0
        faixa = new Faixa();
        faixa.setInicio(base * -1);
        faixa.setFim(0);
        faixas.add(faixa);

        int inicio = base;
        int fim = base;

        // 1- verificar o próximo multiplo de 10 a partir da base(restricao)
        while (fim % 10 != 0) {
            fim++;
        }
        // cria a segunda faixa positiva, que vai de 3 a 10(calculado com o while acima)
        faixa = new Faixa();
        faixa.setInicio(inicio);
        faixa.setFim(fim);
        faixas.add(faixa);
        // cria a segunda faixa negativa, que vai de -10 a -3
        faixa = new Faixa();
        faixa.setInicio(fim * -1);
        faixa.setFim(inicio * -1);
        faixas.add(faixa);

        while (fim < 100) {
            inicio = fim;
            fim = inicio + escala;
            faixa = new Faixa();
            faixa.setInicio(inicio);
            faixa.setFim(fim);
            faixas.add(faixa);

            faixa = new Faixa();
            faixa.setInicio(fim * -1);
            faixa.setFim(inicio * -1);
            faixas.add(faixa);
        }

        Collections.sort(faixas, new CustomComparatorFaixas());
        return faixas;
    }

    private List<Faixa> getFaixas(List<Double> valores) {
        Double minimo = (double) 0;
        Double cota = (valores.get(0) / 5) + 1;
        Double maximo = cota;
        int totalPneus = valores.size();
        List<Faixa> faixas = new ArrayList<>();
        //cria as faixas
        Log.d("kk", valores.toString());
        while (minimo < valores.get(0)) {
            Faixa faixa = new Faixa();
            faixa.setInicio(minimo);
            faixa.setFim(maximo);
            minimo = maximo;
            maximo = maximo + cota;
            faixas.add(faixa);
        }
        //soma cada sulco para a sua devida faixa
        for (Faixa faixa : faixas) {
            for (int i = 0; i < valores.size(); i++) {
                if (valores.get(i) >= faixa.getInicio() && valores.get(i) < faixa.getFim()) {
                    faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                    valores.remove(i);
                    i--;
                }
            }
            faixa.setPorcentagem((double) faixa.getTotalPneus() / totalPneus);
        }
        // cria a faixa de itens não aferidos, com o que sobrou da lista valores
        Faixa faixa = new Faixa();
        faixa.setNaoAferidos(true);
        faixa.setTotalPneus(valores.size());
        faixa.setPorcentagem((double) valores.size() / totalPneus);
        faixas.add(faixa);

        return faixas;
    }

    private Aderencia createAderencia(double meta, int dia) {
        Aderencia aderencia = new Aderencia();
        aderencia.setDia(dia);
        aderencia.setMeta(meta);
        return aderencia;
    }

    private void setPorcentagemFaixas(List<Faixa> faixas, int total) {
        for (Faixa faixa : faixas) {
            if (faixa.getTotalPneus() == 0) {
                faixa.setPorcentagem(0);
            } else {
                double porcentagem = (double) faixa.getTotalPneus() / total;
                faixa.setPorcentagem(porcentagem);
            }
        }
    }

    //ordena as faixas pelo inicio de cada uma
    private class CustomComparatorFaixas implements Comparator<Faixa> {
        /**
         * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
         */
        @Override
        public int compare(Faixa o1, Faixa o2) {
            return Double.compare(o1.getInicio(), o2.getInicio());
        }
    }

    @Override
    public void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream)
            throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosUltimaAfericaoStatement(conn, codUnidade);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getDadosUltimaAfericaoReport(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosUltimaAfericaoStatement(conn, codUnidade);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getDadosUltimaAfericaoStatement(Connection conn, long codUnidade)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
                "  P.codigo                                                                                   AS \"PNEU\",\n" +
                "  map.nome                                                                                   AS \"MARCA\",\n" +
                "  mp.nome                                                                                    AS \"MODELO\",\n" +
                "  ((((dp.largura || '/' :: TEXT) || dp.altura) || ' R' :: TEXT) || dp.aro)                   AS \"MEDIDAS\",\n" +
                "  coalesce(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                                     AS \"PLACA\",\n" +
                "  coalesce(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                           AS \"TIPO\",\n" +
                "  coalesce(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                           AS \"POSIÇÃO\",\n" +
                "  coalesce(trunc(P.altura_sulco_interno :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO INTERNO\",\n" +
                "  coalesce(trunc(P.altura_sulco_central_interno :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO CENTRAL INTERNO\",\n" +
                "  coalesce(trunc(P.altura_sulco_central_externo :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO CENTRAL EXTERNO\",\n" +
                "  coalesce(trunc(P.altura_sulco_externo :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO EXTERNO\",\n" +
                "  coalesce(trunc(P.pressao_atual) :: TEXT, '-')                                              AS \"PRESSÃO (PSI)\",\n" +
                "  P.vida_atual                                                                               AS \"VIDA\",\n" +
                "  coalesce(to_char(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO, 'DD/MM/YYYY HH:MM'), 'não aferido') AS \"ÚLTIMA AFERIÇÃO\"\n" +
                "FROM PNEU P\n" +
                "  JOIN dimensao_pneu dp ON dp.codigo = p.cod_dimensao\n" +
                "  JOIN unidade u ON u.codigo = p.cod_unidade\n" +
                "  JOIN modelo_pneu mp ON mp.codigo = p.cod_modelo AND mp.cod_empresa = u.cod_empresa\n" +
                "  JOIN marca_pneu map ON map.codigo = mp.cod_marca\n" +
                "  LEFT JOIN\n" +
                "  (SELECT\n" +
                "     PON.nomenclatura AS POSICAO_PNEU,\n" +
                "     VP.cod_pneu      AS CODIGO_PNEU,\n" +
                "     VP.placa         AS PLACA_VEICULO_PNEU,\n" +
                "     VP.cod_unidade   AS COD_UNIDADE_PNEU,\n" +
                "     VT.nome          AS VEICULO_TIPO\n" +
                "   FROM veiculo V\n" +
                "     JOIN veiculo_pneu VP ON VP.placa = V.placa AND VP.cod_unidade = V.cod_unidade\n" +
                "     JOIN veiculo_tipo vt ON v.cod_unidade = vt.cod_unidade AND v.cod_tipo = vt.codigo\n" +
                "     JOIN pneu_ordem_nomenclatura_unidade pon ON pon.cod_unidade = v.cod_unidade AND pon.cod_tipo_veiculo = v.cod_tipo\n" +
                "                                                 AND vp.posicao = pon.posicao_prolog\n" +
                "   WHERE V.cod_unidade = ?\n" +
                "   ORDER BY VP.cod_pneu) AS POSICAO_PNEU_VEICULO\n" +
                "    ON P.codigo = POSICAO_PNEU_VEICULO.CODIGO_PNEU AND P.cod_unidade = POSICAO_PNEU_VEICULO.COD_UNIDADE_PNEU\n" +
                "  LEFT JOIN\n" +
                "  (SELECT\n" +
                "     AV.cod_pneu,\n" +
                "     AV.cod_unidade   AS COD_UNIDADE_DATA,\n" +
                "     MAX(A.data_hora) AS ULTIMA_AFERICAO\n" +
                "   FROM AFERICAO A\n" +
                "     JOIN afericao_valores AV ON A.codigo = AV.cod_afericao\n" +
                "   GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO\n" +
                "    ON DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA = P.cod_unidade AND DATA_ULTIMA_AFERICAO.cod_pneu = P.codigo\n" +
                "WHERE P.cod_unidade = ?\n" +
                "ORDER BY \"PNEU\"");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codUnidade);
        return stmt;
    }

    private PreparedStatement getEstratificacaoServicosFechadosStatement(Connection conn, long codUnidade, Date dataInicial,
                                                                         Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_pneu_extrato_servicos_fechados(?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }

    @Override
    public Report getEstratificacaoServicosFechadosReport(Long codUnidade, Date dataInicial,
                                                          Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosFechadosStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoServicosFechadosCsv(Long codUnidade, OutputStream outputStream, Date dataInicial,
                                                     Date dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosFechadosStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Map<StatusPneu, Integer> getQtPneusByStatus(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<StatusPneu, Integer> statusPneus = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT P.status, COUNT(P.CODIGO)\n" +
                    "FROM PNEU P\n" +
                    "WHERE P.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?])\n" +
                    "GROUP BY P.status\n" +
                    "ORDER BY 1");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                statusPneus.put(
                        StatusPneu.fromString(rSet.getString("status")),
                        rSet.getInt("count"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return statusPneus;
    }

    @Override
    public List<QuantidadeAfericao> getQtAfericoesByTipoByData(Date dataInicial, Date dataFinal, List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<QuantidadeAfericao> qtAfericoes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT a.data_hora::date as data,\n" +
                    "  sum(case when a.tipo_afericao = ? THEN 1 ELSE 0 END) AS qt_afericao_pressao,\n" +
                    "  sum(case when a.tipo_afericao = ? THEN 1 ELSE 0 END) AS qt_afericao_sulco,\n" +
                    "  sum(case when a.tipo_afericao = ? THEN 1 ELSE 0 END) AS qt_afericao_sulco_pressao\n" +
                    "FROM afericao a " +
                    "WHERE (SELECT AV.COD_UNIDADE FROM AFERICAO_VALORES AV WHERE AV.COD_AFERICAO = A.CODIGO LIMIT 1)::text " +
                    "like any (ARRAY[?]) and a.data_hora::date BETWEEN ? and ? \n" +
                    "GROUP BY a.data_hora::DATE\n" +
                    "ORDER BY a.data_hora::DATE ASC;");
            stmt.setString(1, TipoAfericao.PRESSAO.asString());
            stmt.setString(2, TipoAfericao.SULCO.asString());
            stmt.setString(3, TipoAfericao.SULCO_PRESSAO.asString());
            stmt.setArray(4, PostgresUtil.ListLongToArray(conn, codUnidades));
            stmt.setDate(5, dataInicial);
            stmt.setDate(6, dataFinal);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                qtAfericoes.add(
                        new QuantidadeAfericao(rSet.getDate("data"),
                                rSet.getInt("qt_afericao_pressao"),
                                rSet.getInt("qt_afericao_sulco"),
                                rSet.getInt("qt_afericao_sulco_pressao")));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return qtAfericoes;
    }

    @Override
    public Map<TipoServico, Integer> getServicosEmAbertoByTipo(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<TipoServico, Integer> servicosAbertos = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT am.tipo_servico, count(am.tipo_servico)\n" +
                    "FROM afericao_manutencao am\n" +
                    "WHERE am.cpf_mecanico IS NULL AND am.cod_unidade::TEXT LIKE ANY(ARRAY[?])\n" +
                    "GROUP BY am.tipo_servico;");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                servicosAbertos.put(
                        TipoServico.fromString(rSet.getString("tipo_servico")),
                        rSet.getInt("count"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return servicosAbertos;
    }

    @Override
    public StatusPlacasAfericao getStatusPlacasAfericao(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT sum(\n" +
                    "  case when (dados.intervalo_pressao > dados.periodo_afericao_pressao or dados.intervalo_pressao < 0) AND\n" +
                    "    (dados.intervalo_sulco > dados.periodo_afericao_sulco or dados.intervalo_sulco < 0) then 1 else 0 end\n" +
                    ") as total_vencidas,\n" +
                    "count (dados.placa) as total_placas\n" +
                    "FROM\n" +
                    "  (SELECT\n" +
                    "  V.placa,\n" +
                    "  coalesce(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER AS INTERVALO_PRESSAO,\n" +
                    "  coalesce(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER   AS INTERVALO_SULCO,\n" +
                    "  erp.periodo_afericao_pressao,\n" +
                    "  erp.periodo_afericao_sulco\n" +
                    "FROM VEICULO V\n" +
                    "  JOIN empresa_restricao_pneu erp ON erp.cod_unidade = v.cod_unidade\n" +
                    "  LEFT JOIN\n" +
                    "  (SELECT\n" +
                    "     PLACA_VEICULO                             AS PLACA_INTERVALO,\n" +
                    "     EXTRACT(DAYS FROM now() - MAX(DATA_HORA)) AS INTERVALO\n" +
                    "   FROM AFERICAO\n" +
                    "   WHERE tipo_afericao = ? OR tipo_afericao = ?\n" +
                    "   GROUP BY PLACA_VEICULO) AS INTERVALO_PRESSAO ON INTERVALO_PRESSAO.PLACA_INTERVALO = V.PLACA\n" +
                    "  LEFT JOIN\n" +
                    "  (SELECT\n" +
                    "     PLACA_VEICULO                             AS PLACA_INTERVALO,\n" +
                    "     EXTRACT(DAYS FROM now() - MAX(DATA_HORA)) AS INTERVALO\n" +
                    "   FROM AFERICAO\n" +
                    "   WHERE tipo_afericao = ? OR tipo_afericao = ?\n" +
                    "   GROUP BY PLACA_VEICULO) AS INTERVALO_SULCO ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA\n" +
                    "WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?])) AS dados;");
            stmt.setString(1, TipoAfericao.SULCO_PRESSAO.asString());
            stmt.setString(2, TipoAfericao.SULCO.asString());
            stmt.setString(3, TipoAfericao.SULCO_PRESSAO.asString());
            stmt.setString(4, TipoAfericao.PRESSAO.asString());
            stmt.setArray(5, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new StatusPlacasAfericao(
                        rSet.getInt("total_vencidas"),
                        rSet.getInt("total_placas") - rSet.getInt("total_vencidas"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    public Map<String, Integer> getMdTempoConsertoServicoPorTipo(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Map<String, Integer> resultados = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT am.tipo_servico, avg(extract(epoch from am.data_hora_resolucao - a.data_hora) / 3600)::INT as md_tempo_conserto_horas\n" +
                    "FROM afericao_manutencao am JOIN afericao a ON a.codigo = am.codigo\n" +
                    "WHERE am.cod_unidade::TEXT LIKE ANY (ARRAY[?]) AND am.cpf_mecanico IS NOT NULL\n" +
                    "GROUP BY am.tipo_servico;");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                resultados.put(rSet.getString("tipo_servico"),
                        rSet.getInt("md_tempo_conserto_horas"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return resultados;
    }

    @Override
    public Map<String, Integer> getQtKmRodadoServicoAberto(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Map<String, Integer> resultados = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT\n" +
                    "  a.placa_veiculo, sum(am.km_momento_conserto - a.km_veiculo)::INT as total_km \n" +
                    "FROM afericao_manutencao am JOIN afericao a ON a.codigo = am.cod_afericao\n" +
                    "  JOIN veiculo_pneu vp ON vp.placa = a.placa_veiculo and am.cod_pneu = vp.cod_pneu and am.cod_unidade = vp.cod_unidade\n" +
                    "WHERE am.cod_unidade::TEXT LIKE ANY (ARRAY[?]) AND am.cpf_mecanico IS NOT NULL AND (am.tipo_servico LIKE 'calibragem' OR am.tipo_servico like 'inspecao')\n" +
                    "GROUP BY a.placa_veiculo\n" +
                    "ORDER BY 2 DESC;");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                resultados.put(rSet.getString("placa_veiculo"),
                        rSet.getInt("total_km"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return resultados;
    }

    public Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<String, Integer> resultados = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM (SELECT vp.placa as placa_veiculo, " +
                    "  sum(case when least(p.altura_sulco_interno,p.altura_sulco_externo, p.altura_sulco_central_externo, p.altura_sulco_central_interno) < erp.sulco_minimo_descarte " +
                    "    then 1 else 0 end) as qt_pneus_abaixo_limite " +
                    "FROM veiculo_pneu vp JOIN pneu p ON p.codigo = vp.cod_pneu AND vp.cod_unidade = p.cod_unidade " +
                    "  JOIN empresa_restricao_pneu erp ON erp.cod_unidade = vp.cod_unidade " +
                    "WHERE vp.cod_unidade::TEXT LIKE ANY (ARRAY[?]) " +
                    "GROUP BY vp.placa " +
                    "ORDER BY 2 DESC) AS PLACA_PNEUS WHERE qt_pneus_abaixo_limite > 0;");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                resultados.put(rSet.getString("placa_veiculo"),
                        rSet.getInt("qt_pneus_abaixo_limite"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return resultados;
    }

    public int getQtdPneusPressaoIncorreta(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        int total = 0;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT count(am.cod_pneu) as total \n" +
                    "FROM afericao_manutencao am JOIN veiculo_pneu vp on vp.cod_unidade = am.cod_unidade and am.cod_pneu = vp.cod_pneu\n" +
                    "WHERE am.cod_unidade::TEXT LIKE ANY (ARRAY[?]) and (am.tipo_servico = ? or am.tipo_servico = ?) and am.cpf_mecanico is null;");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            stmt.setString(2, TipoServico.CALIBRAGEM.asString());
            stmt.setString(3, TipoServico.INSPECAO.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("total");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return total;
    }

    public Map<String, Double> getMenorSulcoPneu(List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Map<String, Double> resultados = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT p.codigo as cod_pneu, " +
                    "trunc(least(p.altura_sulco_interno,p.altura_sulco_externo, p.altura_sulco_central_externo, p.altura_sulco_central_interno)::numeric, 2) as menor_sulco\n" +
                    "FROM pneu p\n" +
                    "WHERE p.cod_unidade::TEXT LIKE ANY (ARRAY[?])\n" +
                    "ORDER BY menor_sulco ASC");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                resultados.put(rSet.getString("cod_pneu"),
                        rSet.getDouble("menor_sulco"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return resultados;
    }
}