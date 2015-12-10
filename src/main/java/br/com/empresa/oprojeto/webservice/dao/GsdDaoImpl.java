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
import br.com.empresa.oprojeto.models.gsd.Gsd;
import br.com.empresa.oprojeto.models.gsd.Gsd.PerguntaRespostaHolder;
import br.com.empresa.oprojeto.models.util.DateUtil;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.dao.interfaces.GsdDao;

public class GsdDaoImpl extends DataBaseConnection implements BaseDao<Gsd>, GsdDao {

	@Override
	public boolean insert(Gsd gsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			// Insere os PDVs
			PdvDaoImpl pdvDao = new PdvDaoImpl();
			pdvDao.insertList(gsd.getPdvs());
			conn = getConnection();
			// Query para inserir um GSD e retornar seu ID AUTO INCREMENTO
			stmt = conn.prepareStatement("INSERT INTO GSD (DATA_HORA, "
					+ "URL_ASSINATURA, CPF_AVALIADOR, CPF_MOTORISTA, "
					+ "CPF_AJUDANTE_1, CPF_AJUDANTE_2, PLACA_VEICULO) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO");
			setStatementItems(stmt, gsd);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				// Seta o código no objeto GSD para poder fazer a inserir na 
				// tabela GSD_RESPOSTAS já que lá pede o código do GSD.
				gsd.setCodigo(rSet.getLong("CODIGO"));
				insertRespostas(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return true;
	}
	
	@Override
	public boolean update(Gsd gsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			// Atualiza os PDVs
			PdvDaoImpl pdvDao = new PdvDaoImpl();
			pdvDao.updateList(gsd.getPdvs());
			// Atualiza as respostas.
			updateRespostas(gsd);
			
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE GSD SET DATA_HORA = ?, "
					+ "URL_ASSINATURA = ?, CPF_AVALIADOR = ?, CPF_MOTORISTA = ?, "
					+ "CPF_AJUDANTE_1 = ?, CPF_AJUDANTE_2 = ?, PLACA_VEICULO = ? "
					+ "WHERE CODIGO = ?");
			setStatementItems(stmt, gsd);
			stmt.setLong(8, gsd.getCodigo());
			return (stmt.executeUpdate() > 0);

		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public Gsd getByCod(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Gsd> getAll() throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Gsd> getByColaborador(Long cpf) throws SQLException {
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
				createPerguntasRespostas(cpf, gsd);
				listGsd.add(gsd);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listGsd;
	}

	@Override
	public List<Gsd> getByAvaliador(Long cpf) throws SQLException {
		return null;
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
		Map<Long, Gsd.PerguntaRespostaHolder> map = new HashMap<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT GR.COD_PERGUNTA, GR.RESPOSTA "
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
				Gsd.PerguntaRespostaHolder holder = new Gsd.PerguntaRespostaHolder(pergunta, resposta);
				pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
				resposta.setResposta(rSet.getString("RESPOSTA"));
				map.put(cpf, holder);
			}
			gsd.setColaboradorMap(map);
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
					+ "CPF_COLABORADOR, COD_PERGUNTA, RESPOSTA) VALUES (?, ?, ?)");
			// Cada colaborador possui um objeto PerguntaRespostaHolder que 
			// contém uma Pergunta e uma Resposta dentro. Ou seja, cada colaborador
			// irá vir associado a uma pergunta e uma resposta.
			for (Map.Entry<Long, Gsd.PerguntaRespostaHolder> entry : gsd.getColaboradorMap().entrySet()) {
				Long cpf = entry.getKey();
				PerguntaRespostaHolder holder = entry.getValue();
				stmt.setLong(1, gsd.getCodigo());
				stmt.setLong(2, cpf);
				stmt.setLong(3, holder.getPergunta().getCodigo());
				stmt.setString(4, holder.getResposta().getResposta());
				stmt.executeUpdate();
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}
	
	private void updateRespostas(Gsd gsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE GSD_RESPOSTAS SET RESPOSTA = ? "
					+ "WHERE COD_GSD = ? AND CPF_COLABORADOR = ? AND "
					+ "COD_PERGUNTA = ?");
			for (Map.Entry<Long, Gsd.PerguntaRespostaHolder> entry : gsd.getColaboradorMap().entrySet()) {
				Long cpf = entry.getKey();
				PerguntaRespostaHolder holder = entry.getValue();
				stmt.setString(1, holder.getResposta().getResposta());
				stmt.setLong(2, gsd.getCodigo());
				stmt.setLong(3, cpf);
				stmt.setLong(4, holder.getPergunta().getCodigo());
				stmt.executeUpdate();
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}
	
	private void setStatementItems(PreparedStatement stmt, Gsd gsd) throws SQLException {
		stmt.setTimestamp(1, DateUtil.toTimestamp(gsd.getDataHora()));
		stmt.setString(2, gsd.getUrlFoto());
		stmt.setLong(3, gsd.getCpfAvaliador());
		stmt.setLong(4, gsd.getCpfMotorista());
		stmt.setLong(5, gsd.getCpfAjudante1());
		stmt.setLong(6, gsd.getCpfAjudante2());
		stmt.setString(7, gsd.getPlacaVeiculo());
	}
}
