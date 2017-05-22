package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.commons.util.Android;
import br.com.zalf.prolog.webservice.commons.util.L;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VeiculoDaoImpl extends DatabaseConnection implements VeiculoDao {

	private static final String VEICULOS_BY_PLACA="SELECT V.*, MV.NOME AS MODELO, EV.NOME AS EIXOS, EV.DIANTEIRO, EV.TRASEIRO, EV.CODIGO AS COD_EIXOS, "
			+ "tv.nome AS TIPO, MAV.NOME AS MARCA, MAV.CODIGO AS COD_MARCA  "
			+ "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO "
			+ "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS "
			+ "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO AND TV.COD_UNIDADE = V.COD_UNIDADE "
			+ "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA "
			+ "WHERE V.PLACA = ?";


	@Override
	public boolean insert(Veiculo veiculo, Long codUnidade) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		veiculo.setPlaca(veiculo.getPlaca().replaceAll("\\-|\\s+", "").trim().toUpperCase());
		if (veiculo.getPlaca().trim().isEmpty() || veiculo.getPlaca().length() != 7) {
			throw new SQLException("Campo placa esta errado.");
		}
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO VEICULO VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, veiculo.getPlaca().toUpperCase());
			stmt.setLong(2, codUnidade);
			stmt.setLong(3, veiculo.getKmAtual());
			stmt.setBoolean(4, true);
			stmt.setLong(5, veiculo.getTipo().getCodigo());
			stmt.setLong(6, veiculo.getModelo().getCodigo());
			stmt.setLong(7, veiculo.getEixos().codigo);
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

	@Override
	public boolean update(Veiculo veiculo, String placaOriginal) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE VEICULO SET "
					+ "PLACA = ?, KM = ?,  COD_TIPO = ?, COD_MODELO = ?, "
					+ "COD_EIXOS = ? "
					+ "WHERE PLACA = ?");
			stmt.setString(1, veiculo.getPlaca());
			stmt.setLong(2, veiculo.getKmAtual());
			stmt.setLong(3, veiculo.getTipo().getCodigo());
			stmt.setLong(4, veiculo.getModelo().getCodigo());
			stmt.setLong(5, veiculo.getEixos().codigo);
			stmt.setString(6, placaOriginal);
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

	@Override
	public boolean delete(String placa) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		L.d("delete", placa);
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE VEICULO SET STATUS_ATIVO = ? "
					+ "WHERE PLACA = ?");
			stmt.setBoolean(1, false);
			stmt.setString(2, placa);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao deletar o veículo");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	@Override
	public List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade)
			throws SQLException {
		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.*, MV.NOME AS MODELO, EV.NOME AS EIXOS, EV.DIANTEIRO, EV.TRASEIRO,EV.CODIGO AS COD_EIXOS, "
					+ "tv.nome AS TIPO, MAV.NOME AS MARCA, MAV.CODIGO AS COD_MARCA  "
					+ "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO "
					+ "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS "
					+ "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO "
					+ "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA "
					+ "WHERE V.COD_UNIDADE = ? AND V.STATUS_ATIVO = TRUE "
					+ "ORDER BY V.PLACA");
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

	@Override
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) throws SQLException {
		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.*, MV.NOME AS MODELO, EV.NOME AS EIXOS, EV.DIANTEIRO, EV.TRASEIRO, EV.CODIGO AS COD_EIXOS, "
					+ "tv.nome AS TIPO, MAV.NOME AS MARCA, MAV.CODIGO AS COD_MARCA  "
					+ "FROM VEICULO V JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO "
					+ "JOIN EIXOS_VEICULO EV ON EV.CODIGO = V.COD_EIXOS "
					+ "JOIN VEICULO_TIPO TV ON TV.CODIGO = V.COD_TIPO "
					+ "JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA "
					+ "WHERE V.COD_UNIDADE = (SELECT COD_UNIDADE FROM COLABORADOR C WHERE C.CPF = ?) "
					+ "ORDER BY V.PLACA");
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
	public Veiculo getVeiculoByPlaca(String placa, boolean withPneus) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		PneuDao pneuDao = new PneuDaoImpl();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(VEICULOS_BY_PLACA);
			stmt.setString(1, placa);
			rSet = stmt.executeQuery();
			if(rSet.next()) {
				Veiculo veiculo = createVeiculo(rSet);
				if (withPneus) {
					veiculo.setListPneus(pneuDao.getPneusByPlaca(placa));
				}
				return veiculo;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return new Veiculo();
	}

	@Override
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

	@Override
	public boolean insertTipoVeiculo(TipoVeiculo tipoVeiculo, Long codUnidade) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO VEICULO_TIPO(COD_UNIDADE, NOME, STATUS_ATIVO) VALUES (?,?,?)");
			stmt.setLong(1, codUnidade);
			stmt.setString(2, tipoVeiculo.getNome());
			stmt.setBoolean(3, true);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao cadastrar o tipo de veículo");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	@Override
	public List<Eixos> getEixos() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Eixos> eixos = new ArrayList<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM EIXOS_VEICULO");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Eixos eixo = new Eixos();
				eixo.codigo = rSet.getLong("CODIGO");
				eixo.nome = rSet.getString("NOME");
				eixo.dianteiro = rSet.getInt("DIANTEIRO");
				eixo.traseiro = rSet.getInt("TRASEIRO");
				eixos.add(eixo);
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return eixos;
	}

	@Override
	public void updateKmByPlaca(String placa, long km, Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		try {
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
			closeConnection(null, stmt, null);
		}		
	}

	@Override
	public List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		List<Marca> marcas = new ArrayList<>();
		List<Modelo> modelos = new ArrayList<>();
		Marca marca = new Marca();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT MO.CODIGO AS COD_MODELO, MO.NOME AS MODELO, MA.CODIGO AS COD_MARCA, MA.NOME AS MARCA"
					+ " FROM MARCA_VEICULO MA left JOIN MODELO_VEICULO MO ON MA.CODIGO = MO.COD_MARCA "
					+ "WHERE MO.COD_EMPRESA = ? OR MO.COD_EMPRESA IS NULL "
					+ "ORDER BY COD_MARCA, COD_MODELO");
			stmt.setLong(1, codEmpresa);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				if(marcas.size() == 0 && modelos.size() == 0){ //primeiro resultado do rset
					L.d("metodo", "marcas.size == 0");
					marca.setCodigo(rSet.getLong("COD_MARCA"));
					marca.setNome(rSet.getString("MARCA"));
					if(rSet.getString("MODELO") != null) {
						modelos.add(createModelo(rSet));
					}
				}else{
					L.d("metodo", "marcas.size > 0");
					if(marca.getCodigo() == rSet.getLong("COD_MARCA")){ // se o modelo atual pertence a mesma marca do modelo anterior
						if(rSet.getString("MODELO") != null) {
							modelos.add(createModelo(rSet));
						}
					}else{ // modelo diferente, deve encerrar a marca e criar uma nova
						marca.setModelos(modelos);
						marcas.add(marca);
						marca = new Marca();
						modelos = new ArrayList<>();
						marca.setCodigo(rSet.getLong("COD_MARCA"));
						marca.setNome(rSet.getString("MARCA"));
						if(rSet.getString("MODELO") != null) {
							modelos.add(createModelo(rSet));
						}
					}
				}
			}
			marca.setModelos(modelos);
			marcas.add(marca);
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return marcas;
	}

	private Modelo createModelo(ResultSet rSet) throws SQLException{
		Modelo modelo = new Modelo();
		modelo.setCodigo(rSet.getLong("COD_MODELO"));
		modelo.setNome(rSet.getString("MODELO"));
		return modelo;
	}

	@Override
	public boolean insertModeloVeiculo(Modelo modelo, long codEmpresa, long codMarca) throws SQLException, NullPointerException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			if(modelo.getNome().trim().isEmpty()){
				throw new NullPointerException("Modelo sem nome!");
			}else {
				conn = getConnection();
				stmt = conn.prepareStatement("INSERT INTO MODELO_VEICULO(NOME, COD_MARCA, COD_EMPRESA) VALUES (?,?,?)");
				stmt.setString(1, modelo.getNome());
				stmt.setLong(2, codMarca);
				stmt.setLong(3, codEmpresa);
				int count = stmt.executeUpdate();
				if (count == 0) {
					throw new SQLException("Erro ao cadastrar o modelo do veículo");
				}
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	@Override
	public int getTotalVeiculosByUnidade(Long codUnidade, Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		int total = 0;
		try{
			stmt = conn.prepareStatement("SELECT COUNT(PLACA) FROM VEICULO WHERE STATUS_ATIVO = TRUE AND COD_UNIDADE = ?");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				total = rSet.getInt("COUNT");				
			}
		}finally{
			closeConnection(null, stmt, rSet);
		}
		return total;
	}

	@Android
	@Override
	public List<String> getVeiculosByTipo(Long codUnidade, String codTipo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<String> placas = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA FROM veiculo V JOIN veiculo_tipo VT ON V.cod_unidade = VT.cod_unidade\n" +
					"AND V.cod_tipo = VT.codigo\n" +
					"WHERE VT.cod_unidade = ? AND VT.codigo::TEXT LIKE ? " +
					"ORDER BY V.PLACA");
			stmt.setLong(1, codUnidade);
			stmt.setString(2, codTipo);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				placas.add(rSet.getString("placa"));
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return placas;
	}

	@Override
	public Set<DiagramaVeiculo> getDiagramasVeiculos() throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	private Veiculo createVeiculo(ResultSet rSet) throws SQLException {
		Veiculo veiculo = new Veiculo();
		veiculo.setPlaca(rSet.getString("PLACA"));
		veiculo.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
		veiculo.setKmAtual(rSet.getLong("KM"));
		Eixos eixos = new Eixos();
		eixos.codigo = rSet.getLong("COD_EIXOS");
		eixos.dianteiro = rSet.getInt("DIANTEIRO");
		eixos.traseiro = rSet.getInt("TRASEIRO");
		veiculo.setEixos(eixos);
		TipoVeiculo tipo = new TipoVeiculo();
		tipo.setCodigo(rSet.getLong("COD_TIPO"));
		tipo.setNome(rSet.getString("TIPO"));
		veiculo.setTipo(tipo);
		Marca marca = new Marca();
		marca.setCodigo(rSet.getLong("COD_MARCA"));
		marca.setNome(rSet.getString("MARCA"));
		veiculo.setMarca(marca);
		Modelo modelo = new Modelo();
		modelo.setCodigo(rSet.getLong("COD_MODELO"));
		modelo.setNome(rSet.getString("MODELO"));
		veiculo.setModelo(modelo);
		return veiculo;
	}

	public List<Veiculo> getVeiculoKm(Long codUnidade, String placa, String codTipo)throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Veiculo> veiculos = new ArrayList<>();
		Veiculo v = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT PLACA, KM FROM VEICULO V WHERE cod_unidade = ? AND " +
					"cod_tipo::TEXT LIKE ? AND PLACA LIKE ? ORDER BY PLACA");
			stmt.setLong(1, codUnidade);
			stmt.setString(2, codTipo);
			stmt.setString(3, placa);
			rSet = stmt.executeQuery();
			while (rSet.next()){
				v = new Veiculo();
				v.setPlaca(rSet.getString("placa"));
				v.setKmAtual(rSet.getLong("km"));
				veiculos.add(v);
			}
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		return veiculos;
	}

}
