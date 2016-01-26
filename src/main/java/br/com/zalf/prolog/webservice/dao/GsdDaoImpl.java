package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Resposta;
import br.com.zalf.prolog.models.gsd.Gsd;
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
					+ "CPF_AJUDANTE_1, CPF_AJUDANTE_2, PLACA_VEICULO) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO");
			stmt.setTimestamp(1, DateUtils.toTimestamp(gsd.getDataHora()));
			stmt.setString(2, gsd.getUrlFoto());
			stmt.setLong(3, gsd.getCpfAvaliador());
			stmt.setLong(4, gsd.getCpfMotorista());
			stmt.setLong(5, gsd.getCpfAjudante1());
			stmt.setLong(6, gsd.getCpfAjudante2());
			stmt.setString(7, gsd.getPlacaVeiculo());
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
					+ "G.CPF_AJUDANTE_2 = ?");
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Gsd gsd = createGsd(rSet);
//				createPerguntasRespostas(gsd.getCpfMotorista(), gsd);
//				createPerguntasRespostas(gsd.getCpfAjudante1(), gsd);
//				createPerguntasRespostas(gsd.getCpfAjudante2(), gsd);
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
			stmt = conn.prepareStatement("SELECT * FROM GSD G WHERE "
					+ "G.CPF_AVALIADOR = ?");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Gsd gsd = createGsd(rSet);
//				createPerguntasRespostas(gsd.getCpfMotorista(), gsd);
//				createPerguntasRespostas(gsd.getCpfAjudante1(), gsd);
//				createPerguntasRespostas(gsd.getCpfAjudante2(), gsd);
				listGsd.add(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listGsd;
	}
	
	//@Override
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
		gsd.setUrlFoto(rSet.getString("URL_ASSINATURA"));
		gsd.setCpfAvaliador(rSet.getLong("CPF_AVALIADOR"));
		gsd.setCpfMotorista(rSet.getLong("CPF_MOTORISTA"));
		gsd.setCpfAjudante1(rSet.getLong("CPF_AJUDANTE_1"));
		gsd.setCpfAjudante2(rSet.getLong("CPF_AJUDANTE_2"));
		gsd.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		return gsd;
	}
	
	private void createPerguntasRespostas(Long cpf, Gsd gsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT GR.COD_PERGUNTA, GR.RESPOSTA, GP.TIPO "
					+ "FROM GSD_RESPOSTAS GR JOIN GSD_PERGUNTAS GP ON "
					+ "GR.COD_PERGUNTA = GP.CODIGO WHERE GR.CPF_COLABORADOR = ? "
					+ "AND GR.COD_GSD = ?");
			// Para pegar as respostas de um gsd especifico da pessoa informada
			// pelo cpf também preciso setar o código do gsd
			stmt.setLong(1, cpf);
			stmt.setLong(2, gsd.getCodigo());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pergunta pergunta = new Pergunta();
				Resposta resposta = new Resposta();
				pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
				resposta.setResposta(rSet.getString("RESPOSTA"));				
				String tipoPergunta = rSet.getString("TIPO");
			}
			// Seta o novo map no gsd
			//gsd.setColaboradorMap(map);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
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