package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class ChecklistDaoImpl extends DataBaseConnection implements 
		BaseDao<Checklist> {
	
	private Map<Pergunta, Resposta> perguntaRespostaMap = new HashMap<>();

	@Override
	public boolean save(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			if(checklist.getCodigo() == null){
				stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
						+ "(DATA, CPF_COLABORADOR, PLACA_VEICULO, TIPO) "
						+ "VALUES (?,?,?,?)");						
			}else{
				stmt = conn.prepareStatement("UPDATE CHECKLIST SET DATA = ?, "
						+ "CPF_COLABORADOR = ?, PLACA_VEICULO = ?, TIPO = ? "
						+ "WHERE CODIGO = ?");
			}
			stmt.setTimestamp(1, DateUtil.toTimestamp(checklist.getData()));
			stmt.setLong(2, checklist.getCpfColaborador());
			stmt.setString(3, checklist.getPlacaVeiculo());
			stmt.setString(4, String.valueOf(checklist.getTipo()));
			if(checklist.getCodigo() != null){		
				stmt.setLong(5, checklist.getCodigo());
			}
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o formulário");
			}
			// Se inseriu, ler o id auto incremento
			if (checklist.getCodigo() == null) {
				Long codigoChecklist = getGeneratedId(stmt);
				checklist.setCodigo(codigoChecklist);
				saveRespostas(checklist);
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public Checklist getByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM CHECKLIST C JOIN "
					+ "CHECKLIST_RESPOSTA CR ON C.CODIGO = CR.COD_CHECKLIST "
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
					+ "CHECKLIST_RESPOSTA CR ON C.CODIGO = CR.COD_CHECKLIST");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Checklist checklist = createChecklist(rSet);
				checklists.add(checklist);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return checklists;
	}
	
	private void saveRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST_RESPOSTAS "
					+ "(COD_CHECKLIST, COD_PERGUNTA, RESPOSTA) VALUES "
					+ "(?, ?, ?)");
			for (Map.Entry<Pergunta, Resposta> entry : 
				checklist.getPerguntaRespostaMap().entrySet()) {
			    Pergunta pergunta = entry.getKey();
			    Resposta resposta = entry.getValue();
			    stmt.setLong(1, checklist.getCodigo());
			    stmt.setLong(2, pergunta.getCodigo());
			    stmt.setString(3, resposta.isResposta());
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
		for (Map.Entry<Pergunta, Resposta> entry : 
			checklist.getPerguntaRespostaMap().entrySet()) {
		    Pergunta key = entry.getKey();
		    Resposta value = entry.getValue();   
		}
	}
	
	// Id gerado com o campo auto incremento
	private static Long getGeneratedId(Statement stmt) throws SQLException {
		ResultSet rs = stmt.getGeneratedKeys();
		if (rs.next()) {
			Long id = rs.getLong(1);
			return id;
		}
		return 0L;
	}
	
	private Checklist createChecklist(ResultSet rSet) throws SQLException {
		Checklist checklist = null;
		if (rSet.getString("TIPO").charAt(0) == 'S') {
			checklist = new ChecklistSaida();
		} else {
			checklist = new ChecklistRetorno();
		}
		checklist.setCodigo(rSet.getLong("CODIGO_CHECKLIST"));
		checklist.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		checklist.setData(rSet.getDate("DATA"));
		checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		checklist.setTipo(rSet.getString("TIPO").charAt(0));
		do {
			createPerguntaResposta(rSet);
		} while (rSet.next());
		checklist.setPerguntaRespostaMap(perguntaRespostaMap);
		return checklist;
	}

	private void createPerguntaResposta(ResultSet rSet) throws SQLException {
		Pergunta pergunta = new Pergunta();
		pergunta.setCodigo(rSet.getLong("CODIGO_PERGUNTA"));
		Resposta resposta = new Resposta();
		resposta.setResposta(rSet.getString("RESPOSTA"));
		perguntaRespostaMap.put(pergunta, resposta);
	}
}
