package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.empresa.oprojeto.models.Pergunta;
import br.com.empresa.oprojeto.models.Resposta;
import br.com.empresa.oprojeto.models.checklist.Checklist;
import br.com.empresa.oprojeto.models.checklist.ChecklistRetorno;
import br.com.empresa.oprojeto.models.checklist.ChecklistSaida;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.util.DateUtil;

// TODO: implementar método buscar todos os checklist por colaborador
public class ChecklistDaoImpl extends DataBaseConnection implements 
		BaseDao<Checklist> {
	
	private Map<Pergunta, Resposta> perguntaRespostaMap = new HashMap<>();

	/**
	 * Insere um checklist no BD salvando na tabela CHECKLIST e chamando métodos
	 * especificos que salvam as respostas do map na tabela CHECKLIST_RESPOSTAS
	 * 
	 * @return boolean
	 * @version 1.0
	 * @since 7 de dez de 2015 13:52:18
	 * @author Luiz Felipe
	 */
	@Override
	public boolean insert(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
					+ "(DATA, CPF_COLABORADOR, PLACA_VEICULO, TIPO) "
					+ "VALUES (?,?,?,?) RETURNING CODIGO");						
			stmt.setTimestamp(1, DateUtil.toTimestamp(checklist.getData()));
			stmt.setLong(2, checklist.getCpfColaborador());
			stmt.setString(3, checklist.getPlacaVeiculo());
			stmt.setString(4, String.valueOf(checklist.getTipo()));
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				checklist.setCodigo(rSet.getLong("CODIGO"));
				insertRespostas(checklist);
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}		
		return true;
	}

	@Override
	public boolean update(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE CHECKLIST SET DATA = ?, "
					+ "CPF_COLABORADOR = ?, PLACA_VEICULO = ?, TIPO = ? "
					+ "WHERE CODIGO = ?");
			stmt.setTimestamp(1, DateUtil.toTimestamp(checklist.getData()));
			stmt.setLong(2, checklist.getCpfColaborador());
			stmt.setString(3, checklist.getPlacaVeiculo());
			stmt.setString(4, String.valueOf(checklist.getTipo()));
			stmt.setLong(5, checklist.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o checklist");
			}
			updateRespostas(checklist);
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM CHECKLIST WHERE CODIGO = ?");
			stmt.setLong(1, codigo);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public Checklist getByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM CHECKLIST C JOIN "
					+ "CHECKLIST_RESPOSTAS CR ON C.CODIGO = CR.COD_CHECKLIST "
					+ "WHERE C.CODIGO = ?");
			stmt.setLong(1, codigo);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Checklist checklist = createChecklist(rSet);
				return checklist;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public List<Checklist> getAll() throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM CHECKLIST C JOIN "
					+ "CHECKLIST_RESPOSTAS CR ON C.CODIGO = CR.COD_CHECKLIST");
			rSet = stmt.executeQuery();
			int count = 0;
			while (rSet.next()) {
				System.out.println(count++);
				Checklist checklist = createChecklist(rSet);
				checklists.add(checklist);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return checklists;
	}
	
	/**
	 * Método responsável por salvar as respostas de um checklist na tabela
	 * CHECKLIST_RESPOSTAS. As respostas e perguntas de um checklist vêm em um 
	 * map<pergunta, resposta> então precisamos percorrer todo esse map para
	 * adicionar todas as respostas de um checklist ao BD.
	 * 
	 * @return void
	 * @version 1.0
	 * @since 7 de dez de 2015 14:01:03
	 * @author Luiz Felipe
	 */
	private void insertRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS "
					+ "(COD_CHECKLIST, COD_PERGUNTA, RESPOSTA) VALUES "
					+ "(?, ?, ?)");
			for (Map.Entry<Pergunta, Resposta> entry : checklist.getPerguntaRespostaMap().entrySet()) {
			    Pergunta pergunta = entry.getKey();
			    Resposta resposta = entry.getValue();
			    stmt.setLong(1, checklist.getCodigo());
			    stmt.setLong(2, pergunta.getCodigo());
			    stmt.setString(3, resposta.getResposta());
			    stmt.executeUpdate();
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}
	
	private void updateRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE CHECKLIST_RESPOSTAS SET "
					+ "COD_PERGUNTA = ?, RESPOSTA = ? WHERE COD_CHECKLIST = ?");
			for (Map.Entry<Pergunta, Resposta> entry : checklist.getPerguntaRespostaMap().entrySet()) {
			    Pergunta pergunta = entry.getKey();
			    Resposta resposta = entry.getValue();
			    stmt.setLong(1, pergunta.getCodigo());
			    stmt.setString(2, resposta.getResposta());
			    stmt.setLong(3, checklist.getCodigo());
			    stmt.executeUpdate();
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}
	
	private Checklist createChecklist(ResultSet rSet) throws SQLException {
		Checklist checklist = null;
		// Não posso percorrer o mesmo result set senão não vou pegar
		ResultSet respostas = rSet;
		if (rSet.getString("TIPO").charAt(0) == 'S') {
			checklist = new ChecklistSaida();
		} else {
			checklist = new ChecklistRetorno();
		}
		checklist.setCodigo(rSet.getLong("COD_CHECKLIST"));
		checklist.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		checklist.setData(rSet.getTimestamp("DATA"));
		checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		checklist.setTipo(rSet.getString("TIPO").charAt(0));
		do {
			createPerguntaResposta(rSet);
		} while (respostas.next() && 
				checklist.getCodigo() == respostas.getLong("COD_CHECKLIST"));
		checklist.setPerguntaRespostaMap(perguntaRespostaMap);
		return checklist;
	}

	private void createPerguntaResposta(ResultSet rSet) throws SQLException {
		Pergunta pergunta = new Pergunta();
		pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
		Resposta resposta = new Resposta();
		resposta.setResposta(rSet.getString("RESPOSTA"));
		perguntaRespostaMap.put(pergunta, resposta);
	}
}
