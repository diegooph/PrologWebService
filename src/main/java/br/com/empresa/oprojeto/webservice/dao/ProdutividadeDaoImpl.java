package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.indicador.ItemDevolucaoCx;
import br.com.empresa.oprojeto.models.indicador.ItemDevolucaoNf;
import br.com.empresa.oprojeto.models.indicador.ItemJornadaLiquida;
import br.com.empresa.oprojeto.models.indicador.ItemTempoInterno;
import br.com.empresa.oprojeto.models.indicador.ItemTempoLargada;
import br.com.empresa.oprojeto.models.indicador.ItemTempoRota;
import br.com.empresa.oprojeto.models.indicador.ItemTracking;
import br.com.empresa.oprojeto.models.produtividade.ItemProdutividade;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.empresa.oprojeto.models.util.TimeUtils;
import br.com.empresa.oprojeto.webservice.dao.interfaces.ProdutividadeDao;

public class ProdutividadeDaoImpl extends DataBaseConnection implements ProdutividadeDao {

	private static final String BUSCA_PRODUTIVIDADE="SELECT M.DATA, M.CXCARREG,M.CXENTREG, M.QTNFCARREGADAS, "
			+ "M.QTNFENTREGUES, M.HRSAI, M.HRENTR, M.TEMPOINTERNO, M.HRMATINAL, "
			+ "C.COD_FUNCAO AS FUNCAO_ATUAL, HC.COD_FUNCAO AS FUNCAO_ANTIGA, "
			+ "M.VlBateuJornMot, M.VlNaoBateuJornMot, M.VlRecargaMot, M.VlBateuJornAju, M.VlNaoBateuJornAju, M.VlRecargaAju "
			+ "FROM MAPA_COLABORADOR MC JOIN COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE "
			+ "AND MC.COD_AMBEV= C.MATRICULA_AMBEV"
			+ " JOIN MAPA M ON M.MAPA = MC.MAPA LEFT "
			+ "JOIN HISTORICO_CARGOS HC ON HC.CPF_COLABORADOR = C.CPF AND M.DATA BETWEEN HC.DATA_INICIO "
			+ "AND HC.DATA_FIM WHERE C.CPF = ? AND DATA BETWEEN ? AND ? "
			+ "ORDER BY M.DATA";



	
	@Override
	public List<ItemProdutividade> getProdutividadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, long cpf)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<ItemProdutividade> listItemProdutividade = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_PRODUTIVIDADE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
			rSet = stmt.executeQuery();	

			while(rSet.next()){
				LocalDate data = (DateUtils.toLocalDate(rSet.getDate("DATA")));
				double valor = createValor(rSet);
				ItemDevolucaoNf devolucaoNf = createDevNf(rSet);
				ItemDevolucaoCx devolucaoCx = createDevCx(rSet);
				ItemJornadaLiquida jornadaLiquida = createJornadaLiquida(rSet);
				ItemTempoInterno tempoInterno = createTempoInterno(rSet);
				ItemTempoLargada tempoLargada = createTempoLargada(rSet);
				ItemTempoRota tempoRota = createTempoRota(rSet);
				ItemTracking tracking = createTracking(rSet);

				ItemProdutividade itemProdutividade = new ItemProdutividade(data, valor,
						jornadaLiquida, devolucaoCx, devolucaoNf, tempoLargada, 
						tempoRota, tempoInterno, tracking);

				listItemProdutividade.add(itemProdutividade);
			}
			return listItemProdutividade;
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
	}



	private double createValor(ResultSet rSet) throws NumberFormatException, SQLException {
		
		double valor = 0;
		int funcao = 0;
		
		if(rSet.getString("FUNCAO_ANTIGA") == null){
			funcao = Integer.parseInt(rSet.getString("FUNCAO_ATUAL"));
		} else {funcao = Integer.parseInt(rSet.getString("FUNCAO_ANTIGA"));}

		switch(funcao){
		//caso a função seja cod = 1 = motorista
		case(1):
			valor = rSet.getDouble("VlBateuJornMot");
			valor = valor + rSet.getDouble("VlNaoBateuJornMot");
			valor = valor + rSet.getDouble("VlRecargaMot");
			break;
		// função cod = 2 = ajudante
		case(2):
			valor = rSet.getDouble("VlBateuJornAju");
			valor = valor + rSet.getDouble("VlNaoBateuJornAju");
			valor = valor + rSet.getDouble("VlRecargaAju");
			valor = valor/2;
			break;
		}
		return valor;
	}



	private ItemTracking createTracking(ResultSet rSet) {
		// TODO Implement
		return null;
	}



	private ItemTempoRota createTempoRota(ResultSet rSet) throws SQLException {
		ItemTempoRota itemTempoRota = new ItemTempoRota();
		itemTempoRota.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
		itemTempoRota.setHrEntrada(TimeUtils.toLocalTime((rSet.getTime("HRENTR"))));
		itemTempoRota.setHrSaida(TimeUtils.toLocalTime(rSet.getTimestamp("HRSAI")));
		// saber o tempo que o caminhão ficou na rua, por isso hora de entrada(volta da rota) = hora de saída( saída para rota)
		itemTempoRota.setResultado(TimeUtils.differenceBetween(itemTempoRota.getHrEntrada(), itemTempoRota.getHrSaida()));
		return itemTempoRota;
	}



	private ItemTempoLargada createTempoLargada(ResultSet rSet) throws SQLException {
		ItemTempoLargada itemTempoLargada = new ItemTempoLargada();
		itemTempoLargada.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
		itemTempoLargada.setHrMatinal(TimeUtils.toLocalTime((rSet.getTime("HRMATINAL"))));
		itemTempoLargada.setHrSaida(TimeUtils.toLocalTime(rSet.getTimestamp("HRSAI")));
		itemTempoLargada.setResultado(calculaTempoLargada(itemTempoLargada.getHrSaida(), itemTempoLargada.getHrMatinal()));
		return itemTempoLargada;
	}



	private ItemTempoInterno createTempoInterno(ResultSet rSet) throws SQLException {

		ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
		itemTempoInterno.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
		itemTempoInterno.setHrEntrada(TimeUtils.toLocalTime((rSet.getTime("HRENTR"))));
		LocalTime tempoInterno = TimeUtils.toLocalTime(rSet.getTime("TEMPOINTERNO"));
		// entrada + tempo interno = horario do fechamento
		itemTempoInterno.setHrFechamento( TimeUtils.somaHoras(itemTempoInterno.getHrEntrada(), tempoInterno));
		itemTempoInterno.setResultado(tempoInterno);
		return itemTempoInterno;
	}

	private ItemJornadaLiquida createJornadaLiquida(ResultSet rSet) throws SQLException {

		LocalTime tempoInterno = TimeUtils.toLocalTime(rSet.getTime("TEMPOINTERNO"));
		LocalTime rota = TimeUtils.differenceBetween(TimeUtils.toLocalTime(rSet.getTimestamp("HRENTR")),
				TimeUtils.toLocalTime(rSet.getTimestamp("HRSAI")));
		LocalTime matinal = calculaTempoLargada(TimeUtils.toLocalTime(rSet.getTimestamp("HRSAI")),
				TimeUtils.toLocalTime(rSet.getTime("HRMATINAL")));
		ItemJornadaLiquida itemJornadaLiquida = new ItemJornadaLiquida();
		itemJornadaLiquida.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
		itemJornadaLiquida.setTempoInterno(tempoInterno);
		itemJornadaLiquida.setTempoRota(rota);
		itemJornadaLiquida.setTempoLargada(matinal);
		itemJornadaLiquida.setResultado(TimeUtils.somaHoras(TimeUtils.somaHoras(matinal, rota),tempoInterno));
		return itemJornadaLiquida;
	}

	private ItemDevolucaoCx createDevCx(ResultSet rSet) throws SQLException {
		ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();
		itemDevolucaoCx.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
		itemDevolucaoCx.setCarregadas(rSet.getDouble("CXCARREG"));
		itemDevolucaoCx.setEntregues(rSet.getDouble("CXENTREG"));
		itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
		itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());
		return itemDevolucaoCx;
	}



	private ItemDevolucaoNf createDevNf(ResultSet rSet) throws SQLException {
		ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();
		itemDevolucaoNf.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
		itemDevolucaoNf.setCarregadas(rSet.getDouble("QTNFCARREGADAS"));
		itemDevolucaoNf.setEntregues(rSet.getDouble("QTNFENTREGUES"));
		itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
		itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());
		return itemDevolucaoNf;
	}

	private LocalTime calculaTempoLargada (LocalTime hrSaida, LocalTime hrMatinal){

		LocalTime matinal = LocalTime.of(00, 30, 00);

		if(hrMatinal.isAfter(hrSaida)){
			return matinal;
		} else {
			return hrSaida.minus(hrMatinal.getLong(ChronoField.MILLI_OF_DAY),ChronoUnit.MILLIS);
		}
	}












}
