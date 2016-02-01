package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.gsd.Gsd;
import br.com.zalf.prolog.models.gsd.Gsd.PerguntaRespostasGsd;
import br.com.zalf.prolog.models.gsd.Pdv;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.BaseDao;
import br.com.zalf.prolog.webservice.dao.interfaces.GsdDao;

public class GsdDaoImpl extends DatabaseConnection implements BaseDao<Gsd>, 
		GsdDao {

	@Override
	public boolean insert(Gsd gsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			// Query para inserir um GSD e retornar seu ID AUTO INCREMENTO
			stmt = conn.prepareStatement("INSERT INTO GSD (DATA_HORA, "
					+ "URL_FOTO, CPF_AVALIADOR, CPF_MOTORISTA, "
					+ "CPF_AJUDANTE_1, CPF_AJUDANTE_2, PLACA_VEICULO, LATITUDE, LONGITUDE) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?,?,?) RETURNING CODIGO");
			stmt.setTimestamp(1, DateUtils.toTimestamp(gsd.getDataHora()));
			stmt.setString(2, gsd.getUrlFoto());
			stmt.setLong(3, gsd.getCpfAvaliador());
			stmt.setLong(4, gsd.getCpfMotorista());
			stmt.setLong(5, gsd.getCpfAjudante1());
			stmt.setLong(6, gsd.getCpfAjudante2());
			stmt.setString(7, gsd.getPlacaVeiculo());
			stmt.setString(8, gsd.getLatitude());
			stmt.setString(8, gsd.getLongitude());
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				// Seta o código no objeto GSD para podeR inserir na 
				// tabela GSD_RESPOSTAS e PDV_GSD já que nessas é preciso o
				// código do GSD
				gsd.setCodigo(rSet.getLong("CODIGO"));
				
				// Insere os PDVs agora pois já tem o código do GSD inserido
				PdvDaoImpl pdvDao = new PdvDaoImpl();
				
				// Agora os pdvs foram inseridos e os que já existiam apenas
				// tiveram seu código obtido para poder inserir na tabela
				// PDV_GSD
				List<Pdv> tempListPdv = pdvDao.insertList(gsd.getPdvs());
				insertPdvsGsd(tempListPdv, gsd.getCodigo());
				
				// Insere as respostas desse GSD
				insertRespostas(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return true;
	}
	
	private void insertPdvsGsd(List<Pdv> tempListPdv, Long codGsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO PDV_GSD (COD_GSD, COD_PDV) "
					+ "VALUES (?, ?)");
			stmt.setLong(1, codGsd);
			int count;
			for (Pdv pdv : tempListPdv) {
				stmt.setLong(2, pdv.getCodigo());
				count = stmt.executeUpdate();
				if (count == 0) {
					throw new SQLException("Erro ao inserir na tabela PDV_GSD");
				}
			}
			
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public boolean update(Gsd gsd) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	// TODO: Fazer join token
	@Override
	public Gsd getByCod(Long codigo, String token) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Gsd> getAll() throws SQLException {
		 throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Gsd> getByColaborador(Long cpf, String token) throws SQLException {
		List<Gsd> listGsd = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM GSD G WHERE "
					+ "G.CPF_MOTORISTA = ? OR G.CPF_AJUDANTE_1 = ? OR "
					+ "G.CPF_AJUDANTE_2 = ?;");
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Gsd gsd = createGsd(rSet);
				listGsd.add(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listGsd;
	}

	@Override
	public List<Gsd> getByAvaliador(Long cpf, String token) throws SQLException {
		List<Gsd> listGsd = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT G.CODIGO, G.DATA_HORA, "
					+ "G.URL_FOTO, G.CPF_AVALIADOR, G.CPF_MOTORISTA, "
					+ "G.CPF_AJUDANTE_1, G.CPF_AJUDANTE_2, G.PLACA_VEICULO, "
					+ "C1.NOME AS NOME_AVALIADOR, C2.NOME AS NOME_MOTORISTA, "
					+ "C3.NOME AS NOME_AJUDANTE_1, C4.NOME AS NOME_AJUDANTE_2 "
					+ "FROM GSD G JOIN COLABORADOR C1 ON C1.CPF = "
					+ "G.CPF_AVALIADOR JOIN COLABORADOR C2 ON C2.CPF = "
					+ "G.CPF_MOTORISTA JOIN COLABORADOR C3 ON C3.CPF = "
					+ "G.CPF_AJUDANTE_1 JOIN COLABORADOR C4 ON C4.CPF = "
					+ "G.CPF_AJUDANTE_2 JOIN TOKEN_AUTENTICACAO TA ON ? = "
					+ "TA.CPF_COLABORADOR AND ? = TA.TOKEN WHERE "
					+ "G.CPF_AVALIADOR = ? ORDER BY G.DATA_HORA DESC;");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				// Cria o GSD
				Gsd gsd = createGsd(rSet);
				
				// Cria a lista de PDV's desse GSD
				List<Pdv> pdvs = createPdvs(gsd.getCodigo());
				gsd.setPdvs(pdvs);
				
				// Cria a lista de PerguntasRespostas desse GSD
				List<Gsd.PerguntaRespostasGsd> perguntaRespostasGsds = 
						createPerguntasRespostas(gsd.getCodigo());
				gsd.setPerguntaRespostasList(perguntaRespostasGsds);
				
				listGsd.add(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listGsd;
	}
	
	@Override
	public List<Gsd> getAllExcetoAvaliador(Long cpf, String token) throws SQLException {
		List<Gsd> listGsd = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT G.CODIGO, G.DATA_HORA, "
					+ "G.URL_FOTO, G.CPF_AVALIADOR, G.CPF_MOTORISTA, "
					+ "G.CPF_AJUDANTE_1, G.CPF_AJUDANTE_2, G.PLACA_VEICULO, "
					+ "C1.NOME AS NOME_AVALIADOR, C2.NOME AS NOME_MOTORISTA, "
					+ "C3.NOME AS NOME_AJUDANTE_1, C4.NOME AS NOME_AJUDANTE_2 "
					+ "FROM GSD G JOIN COLABORADOR C1 ON C1.CPF = "
					+ "G.CPF_AVALIADOR JOIN COLABORADOR C2 ON C2.CPF = "
					+ "G.CPF_MOTORISTA JOIN COLABORADOR C3 ON C3.CPF = "
					+ "G.CPF_AJUDANTE_1 JOIN COLABORADOR C4 ON C4.CPF = "
					+ "G.CPF_AJUDANTE_2 JOIN TOKEN_AUTENTICACAO TA ON ? = "
					+ "TA.CPF_COLABORADOR AND ? = TA.TOKEN WHERE "
					+ "G.CPF_AVALIADOR != ?;");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Gsd gsd = createGsd(rSet);
				listGsd.add(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listGsd;
	}
	
	@Override
	public List<Pergunta> getPerguntas() throws SQLException {
		List<Pergunta> perguntas = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM GSD_PERGUNTAS;");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pergunta pergunta = new Pergunta();
				pergunta.setCodigo(rSet.getLong("CODIGO"));
				pergunta.setPergunta(rSet.getString("PERGUNTA"));
				pergunta.setTipo(rSet.getString("TIPO"));
				perguntas.add(pergunta);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return perguntas;
	}
	
	private Gsd createGsd(ResultSet rSet) throws SQLException {
		Gsd gsd = new Gsd();
		gsd.setCodigo(rSet.getLong("CODIGO"));
		gsd.setDataHora(rSet.getTimestamp("DATA_HORA"));
		gsd.setUrlFoto(rSet.getString("URL_FOTO"));
		gsd.setCpfAvaliador(rSet.getLong("CPF_AVALIADOR"));
		gsd.setCpfMotorista(rSet.getLong("CPF_MOTORISTA"));
		gsd.setCpfAjudante1(rSet.getLong("CPF_AJUDANTE_1"));
		gsd.setCpfAjudante2(rSet.getLong("CPF_AJUDANTE_2"));
		gsd.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		gsd.setNomeAvaliador(rSet.getString("NOME_AVALIADOR"));
		gsd.setNomeMotorista(rSet.getString("NOME_MOTORISTA"));
		gsd.setNomeAjudante1(rSet.getString("NOME_AJUDANTE_1"));
		gsd.setNomeAjudante2(rSet.getString("NOME_AJUDANTE_2"));
		gsd.setLatitude(rSet.getString("LATITUDE"));
		gsd.setLongitude(rSet.getString("LONGITUDE"));
		return gsd;
	}
	
	private List<PerguntaRespostasGsd> createPerguntasRespostas(Long codGsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<PerguntaRespostasGsd> list = new ArrayList<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT GR.COD_PERGUNTA, GR.RESPOSTA, "
					+ "GP.TIPO, GP.PERGUNTA FROM GSD_RESPOSTAS GR JOIN "
					+ "GSD_PERGUNTAS GP ON GR.COD_PERGUNTA = GP.CODIGO WHERE "
					+ "GR.COD_GSD = ?;");
		
			stmt.setLong(1, codGsd);
			rSet = stmt.executeQuery();
			
			// tipo da pergunta
			String t;
			Long codPergunta = -1L;
			while (rSet.next()) {
				PerguntaRespostasGsd pGsd = new PerguntaRespostasGsd();
				Pergunta pergunta = new Pergunta();
				pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
				pergunta.setPergunta(rSet.getString("PERGUNTA"));
				t = rSet.getString("TIPO");
				pergunta.setTipo(t);
				pGsd.setPergunta(pergunta);
				
				// Se entrar, significa que a pergunta anterior é diferente
				// da pergunta atual, então preciso setar as coisas.
				// Se for igual eu não preciso fazer nada
				if (codPergunta != pergunta.getCodigo()) {
					
					// Perguntas que os três colaboradores responderam mas o 
					// avaliador não
			        if (t.equals(Gsd.PERGUNTA_ITENS_ROTA)
			                || t.equals(Gsd.PERGUNTA_CONDICOES_EPIS)
			                || t.equals(Gsd.PERGUNTA_CONDICOES_EPIS_OBSERVACOES)) {
			        	pGsd.setRespostaMotorista(rSet.getString("RESPOSTA"));
						pGsd.setRespostaAjudante1(rSet.getString("RESPOSTA"));
						pGsd.setRespostaAjudante2(rSet.getString("RESPOSTA"));
			        } else {
			        	// Perguntas que apenas o avaliador respondeu
			        	pGsd.setRespostaAvaliador(rSet.getString("RESPOSTA"));
			        }
			        
			        list.add(pGsd);
				}
				codPergunta = pergunta.getCodigo();
			}
			//gsd.setColaboradorMap(map);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		
		return list;
	}
	
	private List<Pdv> createPdvs(Long codGsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Pdv> pdvs = new ArrayList<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM PDV P JOIN PDV_GSD PG "
					+ "ON P.CODIGO = PG.COD_PDV AND PG.COD_GSD = ?");
			// Para pegar as respostas de um gsd especifico da pessoa informada
			// pelo cpf também preciso setar o código do gsd
			stmt.setLong(1, codGsd);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pdv pdv = new Pdv();
				pdv.setCodigo(rSet.getLong("CODIGO"));
				pdv.setNome(rSet.getString("NOME"));
				pdvs.add(pdv);
			}
			//gsd.setColaboradorMap(map);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		
		return pdvs;
	}

	private void insertRespostas(Gsd gsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO GSD_RESPOSTAS (COD_GSD, "
					+ "COD_PERGUNTA, CPF_COLABORADOR, RESPOSTA) VALUES (?, ?, ?, ?)");
			for (Gsd.PerguntaRespostasGsd respostasGsd : gsd.getPerguntaRespostasList()) {
				Pergunta pergunta  = respostasGsd.getPergunta();
				String t = pergunta.getTipo();
				stmt.setLong(1, gsd.getCodigo());
				stmt.setLong(2, pergunta.getCodigo());
				// Perguntas que os três colaboradores respondem mas o avaliador
				// não
		        if (t.equals(Gsd.PERGUNTA_ITENS_ROTA)
		                || t.equals(Gsd.PERGUNTA_CONDICOES_EPIS)
		                || t.equals(Gsd.PERGUNTA_CONDICOES_EPIS_OBSERVACOES)) {
		        	insertItemResposta(stmt, gsd.getCpfMotorista(), respostasGsd.getRespostaMotorista());
		        	insertItemResposta(stmt, gsd.getCpfAjudante1(), respostasGsd.getRespostaAjudante1());
		        	insertItemResposta(stmt, gsd.getCpfAjudante2(), respostasGsd.getRespostaAjudante2());
		        } else {
		        	// Perguntas que apenas o avaliador responde
		        	stmt.setLong(3, gsd.getCpfAvaliador());
		        	stmt.setString(4, respostasGsd.getRespostaAvaliador());
		        	stmt.executeUpdate();
		        }
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}
	
	private void insertItemResposta(PreparedStatement stmt, Long cpf, String 
			resposta) throws SQLException {
		stmt.setLong(3, cpf);
		stmt.setString(4, resposta);
		stmt.executeUpdate();
	}
//	
//	private void updateRespostas(Gsd gsd) throws SQLException {
//		Connection conn = null;
//		PreparedStatement stmt = null;
//		try {
//			conn = getConnection();
//			stmt = conn.prepareStatement("UPDATE GSD_RESPOSTAS SET RESPOSTA = ? "
//					+ "WHERE COD_GSD = ? AND CPF_COLABORADOR = ? AND "
//					+ "COD_PERGUNTA = ?");
//			for (Map.Entry<Long, Gsd.PerguntaRespostaHolder> entry : gsd.getColaboradorMap().entrySet()) {
//				Long cpf = entry.getKey();
//				PerguntaRespostaHolder holder = entry.getValue();
//				stmt.setString(1, holder.getResposta().getResposta());
//				stmt.setLong(2, gsd.getCodigo());
//				stmt.setLong(3, cpf);
//				stmt.setLong(4, holder.getPergunta().getCodigo());
//				stmt.executeUpdate();
//			}
//		} finally {
//			closeConnection(conn, stmt, null);
//		}
//	}
//	

}