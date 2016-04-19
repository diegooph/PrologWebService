package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class ChecklistDaoImpl extends DatabaseConnection{

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

	public boolean insert(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO CHECKLIST "
					+ "(DATA_HORA, CPF_COLABORADOR, PLACA_VEICULO, TIPO, KM_VEICULO) "
					+ "VALUES (?,?,?,?,?) RETURNING CODIGO");						
			stmt.setTimestamp(1, DateUtils.toTimestamp(checklist.getData()));
			stmt.setLong(2, checklist.getCpfColaborador());
			stmt.setString(3, checklist.getPlacaVeiculo());
			stmt.setString(4, String.valueOf(checklist.getTipo()));
			stmt.setLong(5, checklist.getKmAtualVeiculo());
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
			for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
				// verifica apenas os itens cuja resposta foi negativa (tem problema)
				if(respostaTemProblema(resposta)){
					stmt.setString(1, checklist.getPlacaVeiculo());
					stmt.setLong(2, resposta.getCodigo());
					rSet = stmt.executeQuery();
					if(rSet.next()){ //caso o item já exista e ainda não tenha sido resolvido, devemos incrementar a coluna qt_apontamentos
						System.out.println("Item já existe e esta sendo atualizado o total de apontamentos");
						int tempApontamentos = rSet.getInt("QT_APONTAMENTOS");
						tempApontamentos += 1;
						updateQtApontamentos(checklist.getPlacaVeiculo(), resposta.getCodigo(), tempApontamentos, conn);
					}else{ //item não existe, incluir na lista de manutenção
						System.out.println("Item não existe e esta sendo criado na tabela manutenção");
						insertApontamento(checklist.getPlacaVeiculo(), resposta.getCodigo(), DateUtils.toTimestamp(checklist.getData()), conn);
					}
				}
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}
	}
	// Verifica se alguma alternativa da pergunta foi marcada
	private boolean respostaTemProblema(PerguntaRespostaChecklist perguntaRespostaChecklist){
		for(PerguntaRespostaChecklist.Alternativa alternativa : perguntaRespostaChecklist.getAlternativasResposta()){
			if(alternativa.selected == true){
				return true;
			}
		}
		return false;
	}


	public void insertApontamento(String placa, long codPergunta, Timestamp dataApontamento, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MANUTENCAO VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA=?),? , ? , ?)");
		stmt.setString(1, placa);
		stmt.setTimestamp(2, dataApontamento);
		stmt.setString(3, placa);
		stmt.setLong(4, codPergunta);
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

	/*		@Override
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
		}*/

	/*		@Override
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
		}*/

	/*		// TODO: Fazer join token
		@Override
		public Checklist getByCod(Request<?> request) throws SQLException {
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rSet = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
						+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.TIPO, CO.NOME "
						+ "FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
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
		}*/

	/*		@Override
		public List<Checklist> getAll(Request<?> request) throws SQLException {
			List<Checklist> checklists = new ArrayList<>();
			//TODO verificar token e buscar apenas checklists da unidade informada no request
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rSet = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
						+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.TIPO, CO.NOME "
						+ "FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
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
		}*/

	/*		@Override
		public List<Checklist> getAllByCodUnidade(Long cpf, String token, Long codUnidade, LocalDate dataInicial, LocalDate dataFinal, int limit, long offset) throws SQLException {
			List<Checklist> checklists = new ArrayList<>();
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rSet = null;
			try {

				String query = "SELECT C.CODIGO, C.DATA_HORA, "
						+ "C.CPF_COLABORADOR, CO.NOME, C.PLACA_VEICULO, TIPO FROM CHECKLIST C "
						+ "JOIN COLABORADOR CO ON CO.CPF=C.CPF_COLABORADOR JOIN "
						+ "TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
						+ "WHERE CO.COD_UNIDADE = ? AND C.DATA_HORA::DATE BETWEEN ? AND ?  "
						+ "ORDER BY DATA_HORA DESC "
						+ "LIMIT ? OFFSET ?";
				conn = getConnection();
				stmt = conn.prepareStatement(query);
				stmt.setLong(1, cpf);
				stmt.setString(2, token);
				stmt.setLong(3, codUnidade);
				stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
				stmt.setDate(5, DateUtils.toSqlDate(dataFinal));
				stmt.setInt(6, limit);
				stmt.setLong(7, offset);

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
		}*/

	/*		@Override
		public List<Checklist> getAllExcetoColaborador(Long cpf, long offset) throws SQLException {
			List<Checklist> checklists = new ArrayList<>();
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rSet = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
						+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.TIPO, CO.NOME "
						+ "FROM CHECKLIST C JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
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
		}*/

	//	@Override
	public List<Checklist> getByColaborador(Long cpf, int limit, long offset) throws SQLException {
		List<Checklist> checklists = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA, "
					+ "C.CPF_COLABORADOR, C.PLACA_VEICULO, C.KM_VEICULO, C.TIPO , CO.NOME FROM CHECKLIST C "
					+ "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
					+ "WHERE C.CPF_COLABORADOR = ? "
					+ "ORDER BY C.DATA_HORA DESC "
					+ "LIMIT ? OFFSET ?");
			stmt.setLong(1, cpf);
			stmt.setInt(2, limit);
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

	//@Override
	public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade) throws SQLException {
		List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
		List<PerguntaRespostaChecklist.Alternativa> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		PerguntaRespostaChecklist.Alternativa alternativa =  new PerguntaRespostaChecklist.Alternativa();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA, CP.PERGUNTA, CP.ORDEM AS ORDEM_PERGUNTA, "
					+ "CP.SINGLE_CHOICE, CAP.CODIGO AS COD_ALTERNATIVA, "
					+ "CAP.ALTERNATIVA, CAP.ORDEM AS ORDEM_ALTERNATIVA "
					+ "FROM CHECKLIST_PERGUNTAS CP "
					+ "JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CP.CODIGO = CAP.COD_PERGUNTA "
					+ "AND CP.COD_UNIDADE = CAP.COD_UNIDADE WHERE CP.COD_UNIDADE = 1 "
					+ "ORDER BY CP.ORDEM, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rSet = stmt.executeQuery();
			if(rSet.first()){
				pergunta = createPergunta(rSet);
				alternativa = createAlternativa(rSet);
				alternativas.add(alternativa);			
			}
			while (rSet.next()) {
				if(rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()){
					alternativa = createAlternativa(rSet);
					alternativas.add(alternativa);			
				}else{
					pergunta.setAlternativasResposta(alternativas);
					perguntas.add(pergunta);
					alternativas = new ArrayList<>();

					pergunta = createPergunta(rSet);

					alternativa = createAlternativa(rSet);
					alternativas.add(alternativa);			
				}
			}
			pergunta.setAlternativasResposta(alternativas);
			perguntas.add(pergunta);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return perguntas;
	}



	private PerguntaRespostaChecklist createPergunta(ResultSet rSet) throws SQLException{
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
		pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
		pergunta.setPergunta(rSet.getString("PERGUNTA"));
		pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
		return pergunta;
	}

	private PerguntaRespostaChecklist.Alternativa createAlternativa(ResultSet rSet) throws SQLException{
		PerguntaRespostaChecklist.Alternativa alternativa = new PerguntaRespostaChecklist.Alternativa();
		alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
		alternativa.alternativa = rSet.getString("ALTERNATIVA");
		if(alternativa.alternativa.equals("Outros")){
			alternativa.tipo = PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS;
		}
		return alternativa;
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
					+ "(COD_UNIDADE, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA, RESPOSTA) "
					+ "VALUES ((SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA=?), ?, ?, ?, ?)");
			for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
				for(PerguntaRespostaChecklist.Alternativa alternativa : resposta.getAlternativasResposta()){
					stmt.setString(1, checklist.getPlacaVeiculo());
					stmt.setLong(2, checklist.getCodigo());
					stmt.setLong(3, resposta.getCodigo());
					stmt.setLong(4, alternativa.codigo);
					if(alternativa.selected == true){
						if(alternativa.tipo == PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS){// escolheu uma das opções ou nenhuma
							stmt.setString(5, alternativa.respostaOutros); // armazena o texto inserido pelo usuário
						}else{
							stmt.setString(5, "NOK"); // nok para itens com problema
						}
					}else{// a alternativa veio com texto setado, ou seja, foi selecionada a opção "outros"
						stmt.setString(5, "OK"); // ok para itens sem problema
					}
				}
				stmt.executeUpdate();
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	/*	private void updateRespostas(Checklist checklist) throws SQLException {
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
		}*/

	private Checklist createChecklist(ResultSet rSet) throws SQLException {
		Checklist checklist = new Checklist();
		checklist.setCodigo(rSet.getLong("CODIGO"));
		checklist.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		checklist.setNomeColaborador(rSet.getString("NOME"));
		checklist.setData(rSet.getTimestamp("DATA_HORA"));
		checklist.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
		checklist.setTipo(rSet.getString("TIPO").charAt(0));
		checklist.setKmAtualVeiculo(rSet.getLong("KM_VEICULO"));
		createPerguntasRespostas(checklist);
		return checklist;
	}

	private void createPerguntasRespostas(Checklist checklist) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
		List<PerguntaRespostaChecklist.Alternativa> alternativas = new ArrayList<>();
		PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
		PerguntaRespostaChecklist.Alternativa alternativa =  new PerguntaRespostaChecklist.Alternativa();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CP.CODIGO AS COD_PERGUNTA, CP.ORDEM AS ORDEM_PERGUNTA, "
					+ "CP.PERGUNTA, CP.SINGLE_CHOICE,CAP.CODIGO AS COD_ALTERNATIVA, "
					+ "CAP.ORDEM, CAP.ALTERNATIVA, CR.RESPOSTA  "
					+ "FROM CHECKLIST C JOIN CHECKLIST_RESPOSTAS CR ON C.CODIGO = CR.COD_CHECKLIST "
					+ "JOIN CHECKLIST_PERGUNTAS CP ON CP.CODIGO = CR.COD_PERGUNTA JOIN "
					+ "CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CAP.CODIGO = CR.COD_ALTERNATIVA "
					+ "WHERE C.CODIGO = ? AND C.CPF_COLABORADOR = ? "
					+ "ORDER BY CP.ORDEM, CAP.ORDEM", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, checklist.getCodigo());
			stmt.setLong(2, checklist.getCpfColaborador());
			rSet = stmt.executeQuery();
			if(rSet.first()){
				pergunta = createPergunta(rSet);
				alternativa = createAlternativa(rSet);
				setRespostaAlternativa(alternativa, rSet);
				alternativas.add(alternativa);			
			}
			while (rSet.next()) {
				if(rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()){
					alternativa = createAlternativa(rSet);
					setRespostaAlternativa(alternativa, rSet);
					alternativas.add(alternativa);			
				}else{
					pergunta.setAlternativasResposta(alternativas);
					perguntas.add(pergunta);
					alternativas = new ArrayList<>();

					pergunta = createPergunta(rSet);

					alternativa = createAlternativa(rSet);
					setRespostaAlternativa(alternativa, rSet);
					alternativas.add(alternativa);			
				}
			}
			pergunta.setAlternativasResposta(alternativas);
			perguntas.add(pergunta);

		} finally {
			closeConnection(conn, stmt, rSet);
		}
		checklist.setListRespostas(perguntas);
	}

	private void setRespostaAlternativa(PerguntaRespostaChecklist.Alternativa alternativa, ResultSet rSet) throws SQLException{

		if(rSet.getString("RESPOSTA").equals("NOK")){
			alternativa.selected = true;
		}else if(rSet.getString("RESPOSTA").equals("OK")){
			alternativa.selected = false;
		}else{
			alternativa.tipo = PerguntaRespostaChecklist.Alternativa.TIPO_OUTROS;
			alternativa.respostaOutros = rSet.getString("RESPOSTA");
		}
	}
}
