package br.com.zalf.prolog.webservice.seguranca.dashboard;

import br.com.zalf.prolog.webservice.commons.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.seguranca.relato.Local;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DashSegurancaDaoImpl extends DatabaseConnection implements DashSegurancaDao {

	private static final String BUSCA_TOTAIS = "--public int qtRelatosHoje; //total de relatos recebidos hoje \n"
			+ "select ("
			+ "SELECT COUNT(CODIGO) "
			+ "FROM RELATO R JOIN COLABORADOR C ON C.CPF=R.CPF_COLABORADOR "
			+ "WHERE DATA_HORA_DATABASE::DATE = ? AND C.COD_UNIDADE = ?) AS relatos_Hoje, \n "
			
			+ " --public int qtRelatosTotal// total de relatos recebidos no mês \n "
			+ "(SELECT COUNT(CODIGO) "
			+ "FROM RELATO JOIN COLABORADOR ON CPF_COLABORADOR = CPF "
			+ "WHERE COD_UNIDADE = ?) as relatos_Total, \n"
			
			
			+ " --public int qtRelatosMes// total de relatos recebidos no mês \n "
			+ "(SELECT COUNT(CODIGO) "
			+ "FROM RELATO JOIN COLABORADOR ON CPF_COLABORADOR = CPF "
			+ "WHERE DATA_HORA_DATABASE >= ? AND DATA_HORA_DATABASE <= ? AND COD_UNIDADE = ?) as relatos_Mes, \n"
			+ " --public int qtRelatosMesAnterior // total de relatos recebidos M-1 \n "
			+ "(SELECT COUNT(CODIGO) "
			+ "FROM RELATO JOIN COLABORADOR ON CPF_COLABORADOR = CPF "
			+ "WHERE DATA_HORA_DATABASE >= ? AND DATA_HORA_DATABASE <= ? AND COD_UNIDADE = ?) as relatos_Mes_Anterior, \n"
			+ "--public int qtRelatosMesmoPeriodoMesAnterior; // total de relatos recebidos no M-1 até o mesmo dia atual\n "
			+ "(SELECT COUNT(CODIGO) FROM "
			+ "RELATO JOIN COLABORADOR ON CPF_COLABORADOR = CPF	"
			+ "WHERE DATA_HORA_DATABASE >= ? AND DATA_HORA_DATABASE <=  ? AND COD_UNIDADE = ?) as relatos_Mesmo_Periodo_Mes_Anterior";

	private static final String BUSCA_RELATOS_BY_FUNCAO ="SELECT C.COD_FUNCAO as cod_funcao, F.NOME as nome_funcao,  COUNT(R.CODIGO) "
			+ "FROM RELATO R JOIN COLABORADOR C ON C.CPF=R.CPF_COLABORADOR	"
			+ "JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO WHERE DATA_HORA_DATABASE >= ? "
			+ "AND DATA_HORA_DATABASE <=  ? AND C.COD_UNIDADE = ? GROUP BY C.COD_FUNCAO, F.NOME";

	private static final String BUSCA_RELATOS_BY_EQUIPE ="SELECT E.CODIGO as cod_Equipe, E.NOME as nome_Equipe,  COUNT(R.CODIGO)	"
			+ "FROM RELATO R JOIN COLABORADOR C ON C.CPF=R.CPF_COLABORADOR	"
			+ "JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
			+ "WHERE DATA_HORA_DATABASE >= ? AND DATA_HORA_DATABASE <=  ? AND C.COD_UNIDADE = ?	"
			+ "GROUP BY E.CODIGO, E.NOME";

	private static final String BUSCA_RELATOS_BY_MES="SELECT DATE_TRUNC('month', R.DATA_HORA_DATABASE::DATE)::DATE, COUNT(CODIGO) "
			+ "FROM RELATO R JOIN COLABORADOR C ON C.CPF = R.CPF_COLABORADOR "
			+ "WHERE R.DATA_HORA_DATABASE >= ? AND R.DATA_HORA_DATABASE <= ? AND C.COD_UNIDADE = ? "
			+ "GROUP BY DATE_TRUNC('month', R.DATA_HORA_DATABASE::DATE) "
			+ "ORDER BY DATE_TRUNC('month', R.DATA_HORA_DATABASE::DATE)";

	private static final String BUSCA_LOCAL_RELATOS="SELECT R.LATITUDE::TEXT, R.LONGITUDE::TEXT "
			+ "FROM RELATO R JOIN COLABORADOR C ON R.CPF_COLABORADOR = C.CPF "
			+ "WHERE C.COD_UNIDADE = ? ORDER BY R.LATITUDE";

	private static final String BUSCA_RELATOS_BY_COLABORADOR="SELECT C.CPF, C.NOME, COUNT(R.CODIGO) "
			+ "FROM RELATO R JOIN COLABORADOR C ON C.CPF = R.CPF_COLABORADOR "
			+ "WHERE R.DATA_HORA_DATABASE >= ? AND R.DATA_HORA_DATABASE <= ? AND C.COD_UNIDADE = ? "
			+ "GROUP BY 1, 2 "
			+ "ORDER BY COUNT(R.CODIGO) DESC";

	private static final String BUSCA_GSD="SELECT( "
			+ "--Total de GSD realizadas hoje \n"
			+ " SELECT COUNT(G.CODIGO) "
			+ "FROM GSD G JOIN COLABORADOR C ON C.CPF = G.CPF_AVALIADOR "
			+ "WHERE G.DATA_HORA::DATE = ? AND C.COD_UNIDADE = ?) AS TOTAL_HOJE, "
			+ "--Total de GSD no mês \n "
			+ "(SELECT COUNT(G.CODIGO) "
			+ "FROM GSD G JOIN COLABORADOR C ON C.CPF = G.CPF_AVALIADOR "
			+ "WHERE G.DATA_HORA::DATE >= ? AND G.DATA_HORA::DATE <= ? AND C.COD_UNIDADE = ?) AS TOTAL_MES";


	//Aguardando feedback dos relatos:
	/*
	public int metaRelatos; // meta de relatos no mês
    public int qtRelatosResolvidos; // relatos resolvidos no mês
    public int qtRelatosEmAberto; // relatos em aberto no mês
    public double qtPorcentagemRelatosResolvidos; // % de relatos resolvidos no mes
    public double qtPorcentagemRelatosAbertos; // % de relatos em aberto no mês
    public Map<String,Integer > mapRelatosByCategoria; // Escorregão 3
    public Time tempoMedioResolucaoRelato; // tempo médio para resolução de um relato;
    public List<Relato> listRelatosEmAberto; // relatos em aberto, ordenar por mais antigo primeiro
    public List<Relato> listRelatosRecentementeFechados; // relatos fechados ordenados por data de fechamento,
    // de forma que o mais recentemente fechado seja exibido primeiro
	 */
	
	private DashSeguranca dash;

	@Override
	public DashSeguranca getDashSeguranca(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException {
		dash = new DashSeguranca();
		dash.dataInicial = DateUtils.toSqlDate(dataInicial);
		dash.dataFinal = DateUtils.toSqlDate(dataFinal);
		setTotais(dataInicial, dataFinal, codUnidade, equipe);
		setRelatosByFuncao(dataInicial, dataFinal, codUnidade, equipe);
		setRelatosByEquipe(dataInicial, dataFinal, codUnidade, equipe);
		setRelatosByMes(dataInicial, dataFinal, codUnidade, equipe);
		setLocalRelatos(dataInicial, dataFinal, codUnidade, equipe);
		setRelatosByColaborador(dataInicial, dataFinal, codUnidade, equipe);
		setTotaisGSD(dataInicial, dataFinal, codUnidade, equipe);
		System.out.println(dash);
		return dash;
	}

	private void setTotais (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_TOTAIS);
			stmt.setDate(1, DateUtils.toSqlDate(new Date(System.currentTimeMillis()))); //data atual
			stmt.setLong(2, codUnidade); //codUnidade
			stmt.setLong(3, codUnidade); //codUnidade
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial)); //primeiro dia do mes
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal)); //ultimo dia do mes
			stmt.setLong(6, codUnidade);
			stmt.setDate(7, getPrimeiroDiaMesAnterior(dataInicial)); //primeiro dia do mes anterior
			stmt.setDate(8, getUltimoDiaMesAnterior(dataFinal)); //ultimo dia do mes anterior
			stmt.setLong(9, codUnidade);
			stmt.setDate(10, getPrimeiroDiaMesAnterior(dataInicial)); //primeiro dia do mes anterior
			stmt.setDate(11, getMesmoDiaMesAnterior()); //dia atual do mês anterior
			stmt.setLong(12, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				dash.qtRelatosHoje = rSet.getInt("RELATOS_HOJE");
				dash.qtRelatosMes = rSet.getInt("RELATOS_MES");
				dash.qtRelatosTotal = rSet.getInt("RELATOS_TOTAL");
				dash.qtRelatosMesAnterior = rSet.getInt("RELATOS_MES_ANTERIOR");
				dash.qtRelatosMesmoPeriodoMesAnterior = rSet.getInt("RELATOS_MESMO_PERIODO_MES_ANTERIOR");
			}

		}
		finally{
			closeConnection(conn, stmt, rSet);
		}

	}

	private void setTotaisGSD (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_GSD);
			stmt.setDate(1, DateUtils.toSqlDate(new Date(System.currentTimeMillis()))); //data atual
			stmt.setLong(2, codUnidade); //codUnidade
			stmt.setDate(3, DateUtils.toSqlDate(dataInicial)); //primeiro dia do mes
			stmt.setDate(4, DateUtils.toSqlDate(dataFinal)); //ultimo dia do mes
			stmt.setLong(5, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				dash.qtGsdHoje = rSet.getInt("TOTAL_HOJE");
				dash.qtGsdMes = rSet.getInt("TOTAL_MES");
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}

	}

	private void setRelatosByFuncao (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<String, Integer> mapFuncao = new LinkedHashMap<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_RELATOS_BY_FUNCAO);
			stmt.setDate(1, DateUtils.toSqlDate(dataInicial)); //primeiro dia do mes
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal)); //ultimo dia do mes
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				mapFuncao.put(rSet.getString("NOME_FUNCAO"), rSet.getInt("COUNT"));
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		dash.mapRelatosByFuncao = mapFuncao;

	}

	private void setRelatosByEquipe (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<String, Integer> mapEquipe = new LinkedHashMap<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_RELATOS_BY_EQUIPE);
			stmt.setDate(1, DateUtils.toSqlDate(dataInicial)); //primeiro dia do mes
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal)); //ultimo dia do mes
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				mapEquipe.put(rSet.getString("NOME_EQUIPE"), rSet.getInt("COUNT"));
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		dash.mapRelatosByEquipe = mapEquipe;

	}

	private void setRelatosByMes (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<java.util.Date, Integer> mapRelatosByMes = new LinkedHashMap<>();
		Calendar dataInicialMinus12Month = Calendar.getInstance();
		dataInicialMinus12Month.setTime(DateUtils.toSqlDate(dataInicial));
		dataInicialMinus12Month.add(Calendar.MONTH, -12);
		dataInicialMinus12Month.set(Calendar.DAY_OF_MONTH, 1);

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_RELATOS_BY_MES);
			stmt.setDate(1, DateUtils.toSqlDate(new java.util.Date(dataInicialMinus12Month.getTimeInMillis())));
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				mapRelatosByMes.put(rSet.getDate("DATE_TRUNC"), rSet.getInt("COUNT"));
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		dash.mapRelatosByMes = mapRelatosByMes;

	}

	private void setLocalRelatos (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Local> listLocal = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_LOCAL_RELATOS);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				Local local = new Local();
				local.setLatitude(rSet.getString("LATITUDE"));
				local.setLongitude(rSet.getString("LONGITUDE"));
				listLocal.add(local);
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		dash.listLocalRelatos = listLocal;

	}

	private void setRelatosByColaborador (LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<Colaborador, Integer> mapColaborador = new LinkedHashMap<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_RELATOS_BY_COLABORADOR);
			stmt.setDate(1, DateUtils.toSqlDate(dataInicial)); //primeiro dia do mes
			stmt.setDate(2, DateUtils.toSqlDate(dataFinal)); //ultimo dia do mes
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				Colaborador colaborador = new Colaborador();
				colaborador.setCpf(rSet.getLong("CPF"));
				colaborador.setNome(rSet.getString("NOME"));
				mapColaborador.put(colaborador, rSet.getInt("COUNT"));
			}
		}
		finally{
			closeConnection(conn, stmt, rSet);
		}
		dash.mapRelatosByColaborador = mapColaborador;

	}

	private java.sql.Date getPrimeiroDiaMesAnterior(LocalDate date){

		Calendar first = Calendar.getInstance();
		first.setTime(DateUtils.toSqlDate(date));
		first.set(Calendar.DAY_OF_MONTH, 1);
		first.add(Calendar.MONTH, -1);
		return new java.sql.Date(first.getTimeInMillis());
	}

	private java.sql.Date getUltimoDiaMesAnterior(LocalDate date){

		Calendar last = Calendar.getInstance();
		last.setTime(DateUtils.toSqlDate(date));
		last.set(Calendar.DAY_OF_MONTH, 1);
		last.add(Calendar.DAY_OF_MONTH, -1);

		return new java.sql.Date(last.getTimeInMillis());
	}

	private java.sql.Date getMesmoDiaMesAnterior(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, -1);

		return DateUtils.toSqlDate(new Date(calendar.getTimeInMillis()));
	}

}


