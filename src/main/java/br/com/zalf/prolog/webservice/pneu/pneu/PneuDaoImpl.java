package br.com.zalf.prolog.webservice.pneu.pneu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Marca;
import br.com.zalf.prolog.models.Modelo;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Pneu.Dimensao;
import br.com.zalf.prolog.models.pneu.Sulco;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.L;

public class PneuDaoImpl extends DatabaseConnection implements PneuDao{


	private static final String BUSCA_PNEUS_BY_PLACA="( SELECT substring(VP.posicao::text FROM 1 for 3) as POSICAO, ntile(2) over(order by POSICAO), "
			+ "MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, " 
+ "			P.altura_sulcos_novos,P.altura_sulco_CENTRAL, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status " 
			+ "FROM VEICULO_PNEU VP JOIN PNEU P ON P.CODIGO = VP.COD_PNEU " 
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO " 
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA " 
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO " 
			+ "WHERE PLACA =  ? "
			+ "ORDER BY Substring(VP.posicao::text FROM 2 for 1) ASC, " 
			+ "substring(VP.posicao::text FROM 1 for 1) ASC, " 
			+ "substring(VP.posicao::text FROM 3 for 1) ASC "
			+ "LIMIT (SELECT COUNT(PLACA) FROM VEICULO_PNEU WHERE PLACA = ?)/2 "
			+ "OFFSET 0 "
			+ ") "
			+ "UNION ALL ( "
			+ "SELECT substring(VP.posicao::text FROM 1 for 3) as POSICAO, ntile(2) over(order by POSICAO), "
			+ "MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, " 
			+ "P.altura_sulcos_novos,P.altura_sulco_CENTRAL, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status " 
			+ "FROM VEICULO_PNEU VP JOIN PNEU P ON P.CODIGO = VP.COD_PNEU " 
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO " 
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA " 
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO " 
			+ "WHERE PLACA =  ? "
			+ "ORDER BY Substring(VP.posicao::text FROM 2 for 1) ASC, " 
			+ "substring(VP.posicao::text FROM 1 for 1) ASC, " 
			+ "substring(VP.posicao::text FROM 3 for 1) DESC "
			+ "LIMIT (SELECT COUNT(PLACA) FROM VEICULO_PNEU WHERE PLACA = ?)/2 "
			+ "OFFSET (SELECT COUNT(PLACA) FROM VEICULO_PNEU WHERE PLACA = ?)/2 )";

	private static final String BUSCA_PNEUS_BY_COD="SELECT substring(VP.posicao::text FROM 1 for 3) as POSICAO, "
			+ "MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, "
			+ "PD.ALTURA, PD.LARGURA, PD.ARO, PD.CODIGO AS COD_DIMENSAO, P.PRESSAO_RECOMENDADA, "
			+ "P.altura_sulcos_novos,P.altura_sulco_CENTRAL, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status "
			+ "FROM VEICULO_PNEU VP JOIN PNEU P ON P.CODIGO = VP.COD_PNEU "
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
			+ "WHERE P.CODIGO = ? ";

	private static final String BUSCA_PNEUS_BY_COD_UNIDADE="SELECT MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, "
			+ "MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, P.VIDA_ATUAL, P.VIDA_TOTAL, PD.ARO,PD.CODIGO AS COD_DIMENSAO, P.PRESSAO_RECOMENDADA,P.altura_sulcos_novos,P.altura_sulco_CENTRAL, "
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
			stmt = conn.prepareStatement("INSERT INTO PNEU VALUES(?, ?, ?, ?,?,?, ?, ?,?, ?, ?, ?, ?)");
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
			System.out.println(stmt.toString());
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

	public boolean updateMedicoes (Pneu pneu, Long codUnidade, Connection conn) throws SQLException{

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

	public boolean update (Pneu pneu, Long codUnidade, Long codOriginal) throws SQLException{

		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE PNEU SET CODIGO = ?, COD_MODELO = ?, COD_DIMENSAO = ?, PRESSAO_RECOMENDADA = ?, "
					+ "PRESSAO_ATUAL = ?,ALTURA_SULCOS_NOVOS = ?, ALTURA_SULCO_INTERNO = ?, ALTURA_SULCO_CENTRAL = ?,"
					+ " ALTURA_SULCO_EXTERNO = ?, STATUS = ?, VIDA_ATUAL = ?, VIDA_TOTAL = ?"
					+ "WHERE CODIGO = ? AND COD_UNIDADE = ?");
			stmt.setLong(1, pneu.getCodigo());
			stmt.setLong(2, pneu.getModelo().getCodigo());
			stmt.setLong(3, pneu.getDimensao().codigo);
			stmt.setDouble(4, pneu.getPressaoCorreta());
			stmt.setDouble(5, pneu.getPressaoAtual());
			stmt.setDouble(6, pneu.getSulcoPneuNovo().getCentral());
			stmt.setDouble(7, pneu.getSulcoAtual().getInterno());
			stmt.setDouble(8, pneu.getSulcoAtual().getCentral());
			stmt.setDouble(9, pneu.getSulcoAtual().getExterno());
			stmt.setString(10, pneu.getStatus());
			stmt.setInt(11, pneu.getVidaAtual());
			stmt.setInt(12, pneu.getVidasTotal());
			stmt.setLong(13, codOriginal);
			stmt.setLong(14, codUnidade);
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao atualizar as informações do pneu");				
			}			
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

	public boolean registraMovimentacaoHistorico (Pneu pneu, Long codUnidade, String statusDestino, long kmVeiculo,String placaVeiculo, Connection conn, String token) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("INSERT INTO MOVIMENTACAO_PNEU VALUES (?,?,?,?,?,?,?, (SELECT CPF_COLABORADOR FROM TOKEN_AUTENTICACAO WHERE TOKEN = ?))");
		stmt.setTimestamp(1, br.com.zalf.prolog.models.util.DateUtils.toTimestamp(new Time(System.currentTimeMillis())));		
		stmt.setLong(2, pneu.getCodigo());
		stmt.setLong(3, codUnidade);
		stmt.setString(4, pneu.getStatus());
		stmt.setString(5, statusDestino);
		stmt.setString(6, placaVeiculo);
		stmt.setLong(7, kmVeiculo);
		stmt.setString(8, token);		
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
			stmt.setString(2, placa);
			stmt.setString(3, placa);
			stmt.setString(4, placa);
			stmt.setString(5, placa);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pneu pneu = createPneu(rSet);
				pneu.setPosicao(rSet.getInt("POSICAO"));
				listPneu.add(pneu);
			}
			listPneu = ordenaLista(listPneu);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listPneu;
	}

	public void updateVeiculoPneu (String placa, Pneu pneu, Pneu pneuNovo, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		L.d(PneuDaoImpl.class.getSimpleName(), pneu.getCodigo() + " " + pneuNovo.getCodigo() + " " + placa);
		stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET COD_PNEU = ? WHERE PLACA = ? AND COD_PNEU = ?");
		stmt.setLong(1, pneuNovo.getCodigo());
		stmt.setString(2, placa);
		stmt.setLong(3, pneu.getCodigo());
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
		dimensao.codigo = rSet.getLong("COD_DIMENSAO");
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
		// cria uma cópia da lista original de pneus
		for(Pneu pneu : listPneu){
			copiaOriginal.add(pneu);
		}
		
		// metade da lista
		int halfSizeListaOriginal = listPneu.size() / 2;
		
		//itera size/2, começando do ultimo elemento da lista original
		for(int i = sizeListaOriginal; i > sizeListaOriginal/2; i--){
			// seta na lista cópia, posição 5 - listaOriginal(ultima pos)
			copiaOriginal.set(halfSizeListaOriginal, listPneu.get(i-1));
			halfSizeListaOriginal ++;
		}
		System.out.println(copiaOriginal);
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

	public boolean insertModeloPneu(Modelo modelo, long codEmpresa, long codMarca) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO MODELO_PNEU(NOME, COD_MARCA, COD_EMPRESA) VALUES (?,?,?)");
			stmt.setString(1, modelo.getNome());
			stmt.setLong(2, codMarca);
			stmt.setLong(3, codEmpresa);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao cadastrar o modelo do pneu");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	public boolean vinculaPneuVeiculo(Veiculo veiculo) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		long codUnidade = 0L;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			for(Pneu pneu : veiculo.getListPneus()){
				stmt = conn.prepareStatement("INSERT INTO VEICULO_PNEU VALUES(?,?,(SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?) RETURNING COD_UNIDADE");
				stmt.setString(1, veiculo.getPlaca());
				stmt.setLong(2, pneu.getCodigo());
				stmt.setString(3, veiculo.getPlaca());
				stmt.setInt(4, pneu.getPosicao());				
				rSet = stmt.executeQuery();
				if(rSet.next()){
					codUnidade = rSet.getLong("COD_UNIDADE");
				}else{
					throw new SQLException("Erro ao vincular o pneu ao veículo");
				}
				updateStatus(pneu, codUnidade, Pneu.EM_USO, conn);
			}
			conn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	private void updatePosicao(String placa, Pneu pneu, int posicao, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET POSICAO = ? WHERE PLACA = ? AND COD_PNEU = ?");
			stmt.setInt(1, posicao);
			stmt.setString(2, placa);
			stmt.setLong(3, pneu.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar a posicao do pneu");
			}
		}finally{
			closeConnection(null, stmt, null);
		}
	}

	public boolean updatePosicaoPneuVeiculo(Veiculo veiculo) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;

		long codUnidade = 0L;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			// primeiramente é alterado a posicao de todos os pneus para uma posicao ficticia, 
			//após isso é feito o update para inserrir a nova posicao do pneu, esse processo é realizado pois existe a clausula unique(placa, posicao)
			//se fosse realizado direto o update pneu a pneu iria violar o unique.
			for(Pneu pneu : veiculo.getListPneus()){
				updatePosicao(veiculo.getPlaca(), pneu, pneu.getPosicao()+5, conn);
			}
			for(Pneu pneu : veiculo.getListPneus()){
				updatePosicao(veiculo.getPlaca(), pneu, pneu.getPosicao(), conn);
			}
			conn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

}
