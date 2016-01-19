package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.webservice.DataBaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.VeiculoDao;

public class VeiculoDaoImpl extends DataBaseConnection implements VeiculoDao {

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
	
	
	private Veiculo createVeiculo(ResultSet rSet) throws SQLException {
		Veiculo veiculo = new Veiculo();
		veiculo.setPlaca(rSet.getString("PLACA"));
		veiculo.setModelo(rSet.getString("MODELO"));
		veiculo.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
		return veiculo;
	}

}
