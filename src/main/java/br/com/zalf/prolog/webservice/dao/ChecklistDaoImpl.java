package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Resposta;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.ChecklistRetorno;
import br.com.zalf.prolog.models.checklist.ChecklistSaida;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.ChecklistDao;

public class ChecklistDaoImpl extends DatabaseConnection implements ChecklistDao {

	// Limit usado nas buscas para limitar a quantidade de resultados.
	private static final int LIMIT = 10;

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
					+ "(DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO) "
					+ "VALUES (?,?,?,?) RETURNING CODIGO");						
			stmt.setTimestamp(1, DateUtils.toTimestamp(checklist.getData()));
			stmt.setLong(2, checklist.getCpfColaborador());
			stmt.setString(3, checklist.getPlacaVeiculo());
			stmt.setString(4, String.valueOf(checklist.getTipo()));
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				checklist.setCodigo(rSet.getLong("CODIGO"));
				insertRespostas(checklist);
				insertItemManutencao(checklist, conn);
			}else{
				throw new SQLException("Erro ao inserir o checklist");
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}		
		return true;
	}
/**
 * Método para inserir itens com apontados como problema no chcklist em uma tabela destinada ao controle de manutenção
 * @param checklist um Checklist
 * @throws SQLException caso não seja possível realizar as buscas e inserts
 */
	public void insertItemManutencao(Checklist checklist, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			// verifica se já existe item em aberto na tabela manutenção
			stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_MANUTENCAO CM WHERE PLACA = ? AND ITEM = ? AND DATA_RESOLUCAO IS NULL");
			for (Map.Entry<Pergunta, Resposta> entry : checklist.getPerguntaRespostaMap().entrySet()) {
				Pergunta pergunta = entry.getKey();
				Resposta resposta = entry.getValue();
				// verifica apenas os itens cuja resposta foi negativa (tem problema)
				if(!resposta.getResposta().equals("S")){
					stmt.setString(1, checklist.getPlacaVeiculo());
					stmt.setLong(2, pergunta.getCodigo());
					rSet = stmt.executeQuery();
					if(rSet.next()){ //caso o item já exista e ainda não tenha sido resolvido, devemos incrementar a coluna qt_apontamentos
						System.out.println("Item já existe e esta sendo atualizado o total de apontamentos");
						int tempApontamentos = rSet.getInt("QT_APONTAMENTOS");
						tempApontamentos += 1;
						updateQtApontamentos(checklist.getPlacaVeiculo(), pergunta.getCodigo(), tempApontamentos, conn);
					}else{ //item não existe, incluir na lista de manutenção
						System.out.println("Item não existe e esta sendo criado na tabela manutenção");
						insertApontamento(checklist.getPlacaVeiculo(), pergunta.getCodigo(), DateUtils.toTimestamp(checklist.getData()), conn);
					}
				}
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}

	}
	public void insertApontamento(String placa, long codPergunta, Timestamp dataApontamento, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MANUTENCAO VALUES (? , ? , ?)");
		stmt.setTimestamp(1, dataApontamento);
		stmt.setString(2, placa);
		stmt.setLong(3, codPergunta);
		int count = stmt.executeUpdate();
		if(count == 0){
			throw new SQLException("Erro ao inserir item na tabela de manutenção");
		}
		closeConnection(null, stmt, null);
	}

	public void updateQtApontamentos(String placa, long codPergunta, int apontamentos, Connection conn) throws SQLException{
		Connection connection = conn;
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("UPDATE CHECKLIST_MANUTENCAO SET QT_APONTAMENTOS = ? WHERE PLACA = ? AND ITEM = ? AND DATA_RESOLUCAO IS NULL");
		stmt.setInt(1, apontamentos);
		stmt.setString(2, placa);
		stmt.setLong(3, codPergunta);
		int count = stmt.executeUpdate();
		if(count == 0){
			throw new SQLException("Erro ao atualizar a quantidade de apontamentos");
		}
		closeConnection(null, stmt, null);
	}

	@Override
	public boolean update(Request<Checklist> checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE CHECKLIST SET DATA_HORA = ?, "
					+ "CPF_COLABORADOR = ?, PLACA_VEICULO = ?, TIPO = ? "
					+ "WHERE CODIGO = ?");
			//			stmt.setTimestamp(1, DateUtils.toTimestamp(checklist.getData()));
			//			stmt.setLong(2, checklist.getCpfColaborador());
			//			stmt.setString(3, checklist.getPlacaVeiculo());
			//			stmt.setString(4, String.valueOf(checklist.getTipo()));
			//			stmt.setLong(5, checklist.getCodigo());
			//			int count = stmt.executeUpdate();
			//			if(count == 0){
			//				throw new SQLException("Erro ao atualizar o checklist");
			//			}
			//			updateRespostas(checklist);
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(Request<Checklist> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM CHECKLIST WHERE CODIGO = ?");
			//			stmt.setLong(1, codigo);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	// TODO: Fazer join token
	@Override
	public Checklist getByCod(Request<?> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CODIGO, DATA_HORA, "
					+ "CPF_COLABORADOR, PLACA_VEICULO, TIPO FROM CHECKLIST C "
					+ "WHERE C.CODIGO = ?");
			//stmt.setLong(1, codigo);
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
	public List<Checklist> getAll(Request<?> request) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		//TODO verificar token e buscar apenas checklists da unidade informada no request
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CODIGO, DATA_HORA, "
					+ "CPF_COLABORADOR, PLACA_VEICULO, TIPO FROM CHECKLIST"
					+ "ORDER BY DATA_HORA DESC");
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
	
	@Override
	public List<Checklist> getAllByCodUnidade(Request<?> request) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
					+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, TIPO FROM CHECKLIST C "
					+ "JOIN COLABORADOR CO ON CO.CPF=C.CPF_COLABORADOR JOIN "
					+ "TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
					+ "WHERE CO.COD_UNIDADE = ? "
					+ "ORDER BY DATA_HORA DESC");
			stmt.setLong(1, request.getCpf());
			stmt.setString(2, request.getToken());
			stmt.setLong(3, request.getCodUnidade());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Checklist checklist = createChecklist(rSet);
				System.out.println(checklist);
				checklists.add(checklist);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return checklists;
	}

	@Override
	public List<Checklist> getAllExcetoColaborador(Long cpf, long offset) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CODIGO, DATA_HORA, "
					+ "CPF_COLABORADOR, PLACA_VEICULO, TIPO FROM CHECKLIST"
					+ "WHERE CPF_COLABORADOR != ? "
					+ "ORDER BY DATA_HORA DESC "
					+ "LIMIT ? OFFSET ? ");
			stmt.setLong(1, cpf);
			stmt.setInt(2, LIMIT);
			stmt.setLong(3, offset);
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

	@Override
	public List<Checklist> getByColaborador(Long cpf, String token, long offset) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
					+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.TIPO FROM CHECKLIST C "
					+ "JOIN TOKEN_AUTENTICACAO TA ON ? = TA.CPF_COLABORADOR AND "
					+ "? = TA.TOKEN WHERE C.CPF_COLABORADOR = ? "
					+ "ORDER BY C.DATA_HORA DESC "
					+ "LIMIT ? OFFSET ?");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			stmt.setInt(4, LIMIT);
			stmt.setLong(5, offset);
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

	@Override
	public List<Pergunta> getPerguntas() throws SQLException {
		List<Pergunta> perguntas = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_PERGUNTAS "
					+ "ORDER BY ORDEM");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pergunta pergunta = new Pergunta();
				pergunta.setCodigo(rSet.getLong("CODIGO"));
				pergunta.setOrdemExibicao(rSet.getInt("ORDEM"));
				pergunta.setPergunta(rSet.getString("PERGUNTA"));
				perguntas.add(pergunta);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return perguntas;
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
		if (rSet.getString("TIPO").toUpperCase().charAt(0) == 'S') {
			checklist = new ChecklistSaida();
		} else {
			checklist = new ChecklistRetorno();
		}
		checklist.setCodigo(rSet.getLong("CODIGO"));
		checklist.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		checklist.setData(rSet.getTimestamp("DATA_HORA"));
		checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		checklist.setTipo(rSet.getString("TIPO").charAt(0));
		createPerguntasRespostas(checklist);
		return checklist;
	}

	private void createPerguntasRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		LinkedHashMap<Pergunta, Resposta> map = new LinkedHashMap<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CR.COD_PERGUNTA, CP.PERGUNTA, "
					+ "CP.ORDEM, CR.RESPOSTA "
					+ "FROM CHECKLIST_RESPOSTAS CR JOIN CHECKLIST_PERGUNTAS CP "
					+ "ON CR.COD_PERGUNTA = CP.CODIGO JOIN CHECKLIST C ON "
					+ "C.CODIGO = CR.COD_CHECKLIST WHERE C.CPF_COLABORADOR = ? "
					+ "AND CR.COD_CHECKLIST = ? AND CP.ORDEM > 0 "
					+ "ORDER BY CP.ORDEM");
			stmt.setLong(1, checklist.getCpfColaborador());
			stmt.setLong(2, checklist.getCodigo());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Pergunta pergunta = new Pergunta();
				Resposta resposta = new Resposta();
				pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
				pergunta.setPergunta(rSet.getString("PERGUNTA"));
				pergunta.setOrdemExibicao(rSet.getInt("ORDEM"));
				resposta.setResposta(rSet.getString("RESPOSTA"));
				map.put(pergunta, resposta);
			}
			checklist.setPerguntaRespostaMap(map);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}
}
