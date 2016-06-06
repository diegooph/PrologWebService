package br.com.zalf.prolog.webservice.veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.TipoVeiculo;
import br.com.zalf.prolog.models.Veiculo;
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
					+ "KILOMETRAGEM = ? "
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
}
