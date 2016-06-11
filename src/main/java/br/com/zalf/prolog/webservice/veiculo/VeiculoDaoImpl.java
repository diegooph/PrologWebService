package br.com.zalf.prolog.webservice.veiculo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.TipoVeiculo;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.models.Veiculo.Eixos;
import br.com.zalf.prolog.models.pneu.afericao.SelecaoPlacaAfericao;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.PneuDaoImpl;

public class VeiculoDaoImpl extends DatabaseConnection implements VeiculoDao {

	private static final String VEICULOS_BY_PLACA="SELECT V.PLACA, MV.NOME AS MODELO, MAV.NOME AS MARCA,  EV.DIANTEIRO, "
			+ "EV.TRASEIRO, V.KM, V.STATUS_ATIVO "
			+ "FROM VEICULO V "
			+ "JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO "
			+ "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA "
			+ "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS "
			+ "WHERE PLACA = ?";
	
	private static final String BUSCA_SELECAO_AFERICAO = "SELECT V.PLACA, AFERICAO.CONTAGEM "
			+ "FROM VEICULO V "
			+ "LEFT JOIN "
			+ "(SELECT A.PLACA_VEICULO AS PLACA_CONTAGEM, COUNT(A.PLACA_VEICULO) AS CONTAGEM "
			+ "FROM AFERICAO A "
			+ "WHERE A.DATA_HORA::DATE >= ? AND A.DATA_HORA::DATE <= ? "
			+ "GROUP BY 1) AS AFERICAO ON PLACA_CONTAGEM = V.PLACA "
			+ "WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ? "
			+ "ORDER BY CONTAGEM DESC";
	
	@Override
	public List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade) 
			throws SQLException {
		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM VEICULO WHERE "
					+ "COD_UNIDADE = ? AND STATUS_ATIVO = TRUE "
					+ "ORDER BY PLACA");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Veiculo veiculo = createVeiculo(rSet);
				veiculos.add(veiculo);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return veiculos;
	}
	
	public Veiculo getVeiculoByPlaca(String placa) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		PneuDaoImpl pneuDaoImpl = new PneuDaoImpl();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(VEICULOS_BY_PLACA);
			stmt.setString(1, placa);
			rSet = stmt.executeQuery();
			if(rSet.next()) {
				Veiculo veiculo = createVeiculo(rSet);
				veiculo.setListPneus(pneuDaoImpl.getPneusByPlaca(placa));
				return veiculo;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return new Veiculo();
	}

	
	//@Override
	public List<TipoVeiculo> getTipoVeiculosByUnidade(Long codUnidade) throws SQLException {
		List<TipoVeiculo> listTipo = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM VEICULO_TIPO WHERE COD_UNIDADE = ? AND STATUS_ATIVO = TRUE");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				listTipo.add(new TipoVeiculo(rSet.getLong("CODIGO"), rSet.getString("NOME")));
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listTipo;
	}

	// TODO: Fazer join token
	@Override
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) throws SQLException {
		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM VEICULO WHERE "
					+ "COD_UNIDADE = (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF=?) AND STATUS_ATIVO = TRUE");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Veiculo veiculo = createVeiculo(rSet);
				veiculos.add(veiculo);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return veiculos;
	}

	@Override
	public List<Veiculo> getAll(Request<?> request) throws SQLException {
		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM VEICULO V JOIN TOKEN_AUTENTICACAO TA ON "
					+ "TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
					+ "WHERE V.COD_UNIDADE=? ");
			stmt.setLong(1, request.getCpf());
			stmt.setString(2, request.getToken());
			stmt.setLong(3, request.getCodUnidade());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Veiculo veiculo = createVeiculo(rSet);
				veiculos.add(veiculo);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return veiculos;
	}

	private Veiculo createVeiculo(ResultSet rSet) throws SQLException {
		Veiculo veiculo = new Veiculo();
		veiculo.setPlaca(rSet.getString("PLACA"));
		veiculo.setModelo(rSet.getString("MODELO"));
		veiculo.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
		veiculo.setKmAtual(rSet.getLong("KM"));
		Veiculo.Eixos eixos = new Eixos();
		eixos.dianteiro = rSet.getInt("DIANTEIRO");
		eixos.traseiro = rSet.getInt("TRASEIRO");
		veiculo.setMarca(rSet.getString("MARCA"));
		veiculo.setEixos(eixos);
		return veiculo;
	}

	@Override
	public boolean insert(Request<Veiculo> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Autenticacao autenticacao = new Autenticacao("", request.getCpf(), 
				request.getToken());
		AutenticacaoDao dao = new AutenticacaoDaoImpl();
		if (dao.verifyIfExists(autenticacao)) {
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("INSERT INTO VEICULO "
						+ "(PLACA, MODELO, COD_UNIDADE, STATUS_ATIVO) VALUES "
						+ "(?,?,?,?)");
				stmt.setString(1, request.getObject().getPlaca());
				stmt.setString(2, request.getObject().getModelo());
				stmt.setLong(3, request.getCodUnidade());
				stmt.setBoolean(4, true);
				int count = stmt.executeUpdate();
				if(count == 0){
					throw new SQLException("Erro ao inserir o veículo");
				}	
			}
			finally {
				closeConnection(conn, stmt, null);
			}		
			return true;
		}
		return false;
	}

	@Override
	public boolean update(String placa, String placaEditada, String modelo, boolean isAtivo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE VEICULO SET "
					+ "PLACA = ?, MODELO = ?, STATUS_ATIVO = ? "
					+ "WHERE PLACA = ?");
			stmt.setString(1, placaEditada);
			stmt.setString(2, modelo);
			stmt.setBoolean(3, isAtivo);
			stmt.setString(4, placa);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o veículo");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}
	
	public boolean updateKilometragem(String placa, long km) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE VEICULO SET "
					+ "KM = ? "
					+ "WHERE PLACA = ?");
			stmt.setDouble(1, km);
			stmt.setString(2, placa);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar a kilometragem do veículo");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	public void updateKmByPlaca(String placa, long km) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE VEICULO SET "
					+ "KM = ? "
					+ "WHERE PLACA = ?");
			stmt.setLong(1, km);
			stmt.setString(2, placa);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o km do veículo");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
	}
	
	public SelecaoPlacaAfericao getSelecaoPlacaAfericao(LocalDate dataInicial, LocalDate dataFinal, Long codEmpresa, Long codUnidade) throws SQLException{

		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<String, String> mapPlacasAfericao = new LinkedHashMap<>();
		int afericoes = 0;
		int metaAfericao = 20;
		int afericoesRealizadas = 0;
		String status = null;
		Date dataAtual = new Date(System.currentTimeMillis());
		java.util.Date primeiroDiaMes = getPrimeiroDiaMes(dataAtual);
		java.util.Date ultimoDiaMes = getUltimoDiaMes(dataAtual);
		SelecaoPlacaAfericao selecaoPlacaAfericao = new SelecaoPlacaAfericao();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_SELECAO_AFERICAO);
			stmt.setDate(1, DateUtils.toSqlDate(primeiroDiaMes));
			stmt.setDate(2, DateUtils.toSqlDate(ultimoDiaMes));
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				String placa = rSet.getString("PLACA");
				afericoes = rSet.getInt("CONTAGEM");
				if(afericoes > 0){
					status = SelecaoPlacaAfericao.STATUS_REALIZADO;
					afericoesRealizadas += 1;
				}else{
					status = SelecaoPlacaAfericao.STATUS_PENDENTE;
				}				
				mapPlacasAfericao.put(placa, status);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		selecaoPlacaAfericao.setMapPlacasStatus(mapPlacasAfericao);
		selecaoPlacaAfericao.setDataInicial(primeiroDiaMes);
		selecaoPlacaAfericao.setDataFinal(ultimoDiaMes);
		selecaoPlacaAfericao.setAfericoesRealizadas(afericoesRealizadas);
		selecaoPlacaAfericao.setMetaAfericao(metaAfericao);

		return selecaoPlacaAfericao;
	}
	
	public java.util.Date getPrimeiroDiaMes(Date date){

		Calendar first = Calendar.getInstance();
		first.setTime(DateUtils.toSqlDate(date));
		first.set(Calendar.DAY_OF_MONTH, 1);
		return new java.sql.Date(first.getTimeInMillis());
	}

	public java.util.Date getUltimoDiaMes(Date date){

		Calendar last = Calendar.getInstance();
		last.setTime(DateUtils.toSqlDate(date));
		last.set(Calendar.DAY_OF_MONTH, 1);
		last.add(Calendar.MONTH, 1);
		last.add(Calendar.DAY_OF_MONTH, -1);

		return new java.sql.Date(last.getTimeInMillis());
	}
}
