package br.com.zalf.prolog.webservice.pneu.pneu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Marca;
import br.com.zalf.prolog.models.Modelo;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Pneu.Dimensao;
import br.com.zalf.prolog.models.pneu.Sulco;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.L;

public class PneuDaoImpl extends DatabaseConnection implements PneuDao{


	private static final String BUSCA_PNEUS_BY_PLACA="SELECT substring(VP.posicao::text FROM 1 for 3) as POSICAO, "
			+ "MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, "
			+ "P.altura_sulcos_novos,P.altura_sulco_CENTRAL, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status "
			+ "FROM VEICULO_PNEU VP JOIN PNEU P ON P.CODIGO = VP.COD_PNEU "
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
			+ "WHERE PLACA = ? "
			+ "ORDER BY Substring(VP.posicao::text FROM 2 for 1) ASC, "
			+ "substring(VP.posicao::text FROM 1 for 1) ASC, "
			+ "substring(VP.posicao::text FROM 3 for 1) ASC";

	private static final String BUSCA_PNEUS_BY_COD="SELECT substring(VP.posicao::text FROM 1 for 3) as POSICAO, "
			+ "MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, "
			+ "P.altura_sulcos_novos,P.altura_sulco_CENTRAL, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status "
			+ "FROM VEICULO_PNEU VP JOIN PNEU P ON P.CODIGO = VP.COD_PNEU "
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
			+ "WHERE P.CODIGO = ? ";
	
	private static final String BUSCA_PNEUS_BY_COD_UNIDADE="SELECT MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, "
			+ "MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, P.VIDA_ATUAL, P.VIDA_TOTAL, PD.ARO, P.PRESSAO_RECOMENDADA,P.altura_sulcos_novos,P.altura_sulco_CENTRAL, "
			+ "P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status	FROM PNEU P "
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
			+ "WHERE P.COD_UNIDADE = ? AND P.STATUS LIKE ?";
	
	
	public boolean insert(Pneu pneu, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO PNEU(?, ?, ?, ?,?, "
					+ "?, ?, ?,?, ?, ?, ?, ?)");
			stmt.setLong(1, pneu.getCodigo());
			stmt.setLong(2, pneu.getModelo().getCodigo());
			stmt.setLong(3, pneu.getDimensao().codigo);
			stmt.setDouble(4, pneu.getPressaoCorreta());
			stmt.setDouble(5, 0L);
			stmt.setDouble(6, pneu.getSulcoPneuNovo().getCentral());
			stmt.setDouble(7, pneu.getSulcoAtual().getInterno());
			stmt.setDouble(8, pneu.getSulcoAtual().getCentral());
			stmt.setDouble(9, pneu.getSulcoAtual().getExterno());
			stmt.setLong(10, codUnidade);
			stmt.setString(11, pneu.getStatus());
			stmt.setInt(12, pneu.getVidaAtual());
			stmt.setInt(13, pneu.getVidasTotal());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o pneu");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}
	
	public boolean updateSulcos (Pneu pneu, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE PNEU SET "
					+ "PRESSAO_ATUAL = ?, ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, ALTURA_SULCO_CENTRAL = ? "
					+ "WHERE CODIGO = ? AND COD_UNIDADE = ?");
			stmt.setDouble(1, pneu.getPressaoAtual());
			stmt.setDouble(2, pneu.getSulcoAtual().getInterno());
			stmt.setDouble(3, pneu.getSulcoAtual().getExterno());
			stmt.setDouble(4, pneu.getSulcoAtual().getCentral());
			stmt.setLong(5, pneu.getCodigo());
			stmt.setLong(6, codUnidade);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar os dados do Pneu");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	public boolean update (Pneu pneu, Long codUnidade, Connection conn) throws SQLException{

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("UPDATE PNEU SET "
					+ "PRESSAO_ATUAL = ?, ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_EXTERNO = ?, ALTURA_SULCO_CENTRAL = ? "
					+ "WHERE CODIGO = ? AND COD_UNIDADE = ?");
			stmt.setDouble(1, pneu.getPressaoAtual());
			stmt.setDouble(2, pneu.getSulcoAtual().getInterno());
			stmt.setDouble(3, pneu.getSulcoAtual().getExterno());
			stmt.setDouble(4, pneu.getSulcoAtual().getCentral());
			stmt.setLong(5, pneu.getCodigo());
			stmt.setLong(6, codUnidade);
			stmt.executeUpdate();
		}
		finally {
			closeConnection(null, stmt, null);
		}		
		return true;
	}

	public void updateCalibragem (Pneu pneu, Long codUnidade, Connection conn) throws SQLException{

		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("UPDATE PNEU SET "
				+ "PRESSAO_ATUAL = ? "
				+ "WHERE CODIGO = ? AND COD_UNIDADE = ?");
		stmt.setDouble(1, pneu.getPressaoAtual());
		stmt.setLong(2, pneu.getCodigo());
		stmt.setLong(3, codUnidade);
		stmt.executeUpdate();
	}
	
	public boolean updateStatus (Pneu pneu, Long codUnidade, String status, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("UPDATE PNEU SET "
				+ "STATUS = ? "
				+ "WHERE CODIGO = ? AND COD_UNIDADE = ?");
		stmt.setString(1, status);
		stmt.setLong(2, pneu.getCodigo());
		stmt.setLong(3, codUnidade);
		stmt.executeUpdate();
		return true;
	}

	@Override
	public List<Pneu> getPneusByPlaca(String placa) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Pneu> listPneu = new ArrayList<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_PNEUS_BY_PLACA);
			stmt.setString(1, placa);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pneu pneu = createPneu(rSet);
				pneu.setPosicao(rSet.getInt("POSICAO"));
				listPneu.add(pneu);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		listPneu = ordenaLista(listPneu);
		return listPneu;
	}
	
	public void updateVeiculoPneu (String placa, Pneu pneu, Pneu pneuNovo, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		
			stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET COD_PNEU = ? WHERE PLACA = ? AND COD_PNEU = ?");
			stmt.setLong(1, pneu.getCodigo());
			stmt.setString(2, placa);
			stmt.setLong(3, pneuNovo.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao substituir o pneu vinculado a placa");
			}
			closeConnection(null, stmt, null);
	}

	//TODO refatorar! apenas o codPneu não é suficiente, precisamos inserir o codUnidade na busca
	public Pneu getPneuByCod(long codPneu) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Pneu pneu = new Pneu();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_PNEUS_BY_COD);
			stmt.setLong(1, codPneu);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				pneu = createPneu(rSet);
				pneu.setPosicao(rSet.getInt("POSICAO"));
			}else{
				throw new SQLException("Não foi possível buscar o pneu solicitado");
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return pneu;
	}
	
	public List<Pneu> getPneuByCodUnidadeByStatus(Long codUnidade, String status) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Pneu> pneus = new ArrayList<>();
		L.d("aa", "Buscando pneus disponiveis com o codUnidade = "+codUnidade + " e o status = " + status);
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_PNEUS_BY_COD_UNIDADE);
			stmt.setLong(1, codUnidade);
			stmt.setString(2, status);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				Pneu pneu = createPneu(rSet);
				pneus.add(pneu);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		System.out.println(pneus);
		return pneus;
	}

	public Pneu createPneu(ResultSet rSet) throws SQLException{
		Pneu pneu = new Pneu();
		pneu.setCodigo(rSet.getInt("CODIGO"));
		Marca marca = new Marca();
		marca.setCodigo(rSet.getLong("COD_MARCA"));
		marca.setNome(rSet.getString("MARCA"));
		pneu.setMarca(marca);
		Modelo modelo = new Modelo();
		modelo.setCodigo(rSet.getLong("COD_MODELO"));
		modelo.setNome(rSet.getString("MODELO"));
		pneu.setModelo(modelo);

		Pneu.Dimensao dimensao = new Pneu.Dimensao();
		dimensao.altura = rSet.getInt("ALTURA");
		dimensao.aro = rSet.getInt("ARO");
		dimensao.largura = rSet.getInt("LARGURA");

		pneu.setDimensao(dimensao);
		pneu.setPressaoCorreta(rSet.getInt("PRESSAO_RECOMENDADA"));

		Sulco sulcoAtual = new Sulco();
		sulcoAtual.setCentral(rSet.getDouble("ALTURA_SULCO_CENTRAL"));
		sulcoAtual.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
		sulcoAtual.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
		pneu.setSulcoAtual(sulcoAtual);

		Sulco sulcoNovo = new Sulco();
		sulcoNovo.setCentral(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
		sulcoNovo.setExterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
		sulcoNovo.setInterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
		pneu.setSulcoPneuNovo(sulcoNovo);

		pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
		pneu.setPressaoAtual(rSet.getDouble("PRESSAO_ATUAL"));
		pneu.setStatus(rSet.getString("STATUS"));
		
		pneu.setVidaAtual(rSet.getInt("VIDA_ATUAL"));
		pneu.setVidasTotal(rSet.getInt("VIDA_TOTAL"));

		return pneu;
	}

	private List<Pneu> ordenaLista(List<Pneu> listPneu){
		int sizeListaOriginal = listPneu.size();
		List<Pneu> copiaOriginal = new ArrayList();
		for(Pneu pneu : listPneu){
			copiaOriginal.add(pneu);
		}
		int halfSizeListaOriginal = listPneu.size() / 2;
		for(int i = sizeListaOriginal; i > sizeListaOriginal/2; i--){
			copiaOriginal.set(halfSizeListaOriginal, listPneu.get(i-1));
			halfSizeListaOriginal ++;
		}
		return copiaOriginal;			
	}
	
	public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		List<Marca> marcas = new ArrayList<>();
		List<Modelo> modelos = new ArrayList<>();
		Marca marca = new Marca();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT MP.NOME AS MODELO, MP.CODIGO AS COD_MODELO, MA.NOME AS MARCA, MA.CODIGO AS COD_MARCA "
					+ "FROM MODELO_PNEU MP JOIN MARCA_PNEU MA ON MA.CODIGO = MP.COD_MARCA "
					+ "WHERE MP.COD_EMPRESA = ? "
					+ "ORDER BY COD_MARCA, COD_MODELO");
			stmt.setLong(1, codEmpresa);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				if(marcas.size() == 0 && modelos.size() == 0){ //primeiro resultado do rset
					L.d("metodo", "marcas.size == 0");
					marca.setCodigo(rSet.getLong("COD_MARCA"));
					marca.setNome(rSet.getString("MARCA"));
					Modelo modelo = new Modelo();
					modelo.setCodigo(rSet.getLong("COD_MODELO"));
					modelo.setNome(rSet.getString("MODELO"));
					modelos.add(modelo);
				}else{
					L.d("metodo", "marcas.size > 0");
					if(marca.getCodigo() == rSet.getLong("COD_MARCA")){ // se o modelo atual pertence a mesma marca do modelo anterior
						Modelo modelo = new Modelo();
						modelo.setCodigo(rSet.getLong("COD_MODELO"));
						modelo.setNome(rSet.getString("MODELO"));
						modelos.add(modelo);
					}else{ // modelo diferente, deve encerrar a marca e criar uma nova
						marca.setModelos(modelos);
						marcas.add(marca);
						marca = new Marca();
						modelos = new ArrayList<>();
						marca.setCodigo(rSet.getLong("COD_MARCA"));
						marca.setNome(rSet.getString("MARCA"));
						Modelo modelo = new Modelo();
						modelo.setCodigo(rSet.getLong("COD_MODELO"));
						modelo.setNome(rSet.getString("MODELO"));
						modelos.add(modelo);						
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
	
	public List<Dimensao> getDimensoes() throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Dimensao> dimensoes = new ArrayList<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM DIMENSAO_PNEU");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Dimensao dimensao = new Dimensao();
				dimensao.codigo = rSet.getLong("CODIGO");
				dimensao.altura = rSet.getInt("ALTURA");
				dimensao.aro = rSet.getInt("ARO");
				dimensao.largura = rSet.getInt("LARGURA");				
				dimensoes.add(dimensao);
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return dimensoes;
	}

}
