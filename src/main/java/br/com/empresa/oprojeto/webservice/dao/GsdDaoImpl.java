package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import br.com.empresa.oprojeto.models.Colaborador;
import br.com.empresa.oprojeto.models.gsd.Gsd;
import br.com.empresa.oprojeto.models.gsd.PerguntaRespostaHolder;
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
			for (Map.Entry<Colaborador, PerguntaRespostaHolder> entry : gsd.getColaboradorMap().entrySet()) {
				Colaborador colaborador = entry.getKey();
				PerguntaRespostaHolder holder = entry.getValue();
				stmt.setLong(1, gsd.getCodigo());
				stmt.setLong(2, colaborador.getCpf());
				stmt.setLong(3, holder.getPergunta().getCodigo());
				stmt.setString(4, holder.getResposta().getResposta());
				stmt.executeUpdate();
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public boolean update(Gsd object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
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
		return null;
	}

	@Override
	public List<Gsd> getByAvaliador(Long cpf) throws SQLException {
		return null;
	}
	
	private void setStatementItems(PreparedStatement stmt, Gsd gsd) throws SQLException {
		stmt.setTimestamp(1, DateUtil.toTimestamp(gsd.getDataHora()));
		stmt.setString(2, gsd.getUrlFoto());
		stmt.setLong(3, gsd.getCpfMotorista());
		stmt.setLong(4, gsd.getCpfAjudante1());
		stmt.setLong(5, gsd.getCpfAjudante2());
		stmt.setString(6, gsd.getPlacaVeiculo());
	}

}
