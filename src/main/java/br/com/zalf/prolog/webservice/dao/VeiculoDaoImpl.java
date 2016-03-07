package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.AutenticacaoDao;
import br.com.zalf.prolog.webservice.dao.interfaces.VeiculoDao;

public class VeiculoDaoImpl extends DatabaseConnection implements VeiculoDao {

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
					+ "COD_UNIDADE = ? AND STATUS_ATIVO = TRUE");
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

	// TODO: Fazer join token
	@Override
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf, String token) throws SQLException {
		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			Colaborador colaborador = new ColaboradorDaoImpl().getByCod(cpf, token); 
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM VEICULO WHERE "
					+ "COD_UNIDADE = ? AND STATUS_ATIVO = TRUE");
			stmt.setLong(1, colaborador.getCodUnidade());
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
			stmt = conn.prepareStatement("SELECT * FROM VEICULO WHERE "
					+ "COD_UNIDADE = ? JOIN TOKEN_AUTENTICACAO TA ON "
					+ "TA.CPF_COLABORADOR = ? AND TA.TOKEN = ?");
			stmt.setLong(1, request.getCodUnidade());
			stmt.setLong(2, request.getCpf());
			stmt.setString(3, request.getToken());
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
						+ "(PLACA, MODELO, STATUS_ATIVO) VALUES "
						+ "(?,?,?)");
				stmt.setString(1, request.getObject().getPlaca());
				stmt.setString(2, request.getObject().getModelo());
				stmt.setBoolean(3, true);
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
	public boolean update(Request<Veiculo> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE VEICULO SET "
					+ "PLACA = ?, MODELO = ?, STATUS_ATIVO = ? "
					+ "FROM TOKEN_AUTENTICACAO TA WHERE PLACA = ? AND "
					+ "TA.CPF_COLABORADOR = ? AND TA.TOKEN = ?");
			stmt.setString(1, request.getObject().getPlaca());
			stmt.setString(2, request.getObject().getModelo());
			stmt.setBoolean(3, request.getObject().isAtivo());
			stmt.setString(4, request.getObject().getPlaca());
			stmt.setLong(5, request.getCpf());
			stmt.setString(6, request.getToken());
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
}
