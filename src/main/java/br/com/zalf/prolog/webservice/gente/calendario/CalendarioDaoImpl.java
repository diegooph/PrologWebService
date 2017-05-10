package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.commons.colaborador.Equipe;
import br.com.zalf.prolog.webservice.commons.colaborador.Funcao;
import br.com.zalf.prolog.webservice.commons.colaborador.Unidade;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.empresa.EmpresaDaoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarioDaoImpl extends DatabaseConnection implements CalendarioDao {

	private static final String TAG = CalendarioDaoImpl.class.getSimpleName();
	/**
	 * Busca dos eventos, foi fracionada a busca em 5 partes, sendo elas:
	 * 1 - Busca os eventos exclusivos para uma unidade + função + equipe
	 * 2 - Busca os eventos exclusivos para uma unidade + função, independente da equipe
	 * 3 - Busca os eventos exclusivos para uma unidade + equipe, independente da função
	 * 4 - Busca os eventos exclusivos para uma unidade, independente da função e da equipe
	 * 5 - Faz um union com todos os resultados
	 */
	private static final String BUSCA_EVENTOS = "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO = C.COD_FUNCAO "
			+ "AND CAL.COD_EQUIPE = C.COD_EQUIPE "
			+ "WHERE C.CPF=? "
			+ "UNION "
			+ "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO = C.COD_FUNCAO "
			+ "AND CAL.COD_EQUIPE IS NULL "
			+ "WHERE C.CPF=? "
			+ "UNION "
			+ "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO IS NULL "
			+ "AND CAL.COD_EQUIPE = C.COD_EQUIPE "
			+ "WHERE C.CPF=? "
			+ "UNION "
			+ "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO IS NULL "
			+ "AND CAL.COD_EQUIPE IS NULL "
			+ "WHERE C.CPF=? ";

	@Override
	public List<Evento> getEventosByCpf(Long cpf) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Evento> listEvento = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EVENTOS);
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
			stmt.setLong(3, cpf);
			stmt.setLong(4, cpf);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				Evento evento = new Evento();
				evento.setData(rSet.getTimestamp("DATA"));
				evento.setDescricao(rSet.getString("DESCRICAO"));
				evento.setCodigo(rSet.getLong("CODIGO"));
				evento.setLocal(rSet.getString("LOCAL"));
				listEvento.add(evento);
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}
		return listEvento;
	}

	@Override
	public List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade,
								String nomeEquipe, String codFuncao) throws SQLException {
		Connection conn = null;
		ResultSet rSet = null;
		PreparedStatement stmt = null;
		List<Evento> eventos = new ArrayList<>();
		Evento e;
		Unidade unidade;
		Funcao funcaoTreinamento;
		Equipe equipeTreinamento;
		EmpresaDaoImpl empresaDao = new EmpresaDaoImpl();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM \n" +
					"(SELECT c.data, c.codigo, c.descricao, c.local, coalesce(c.cod_funcao, c.cod_funcao, -1) as cod_funcao, f.nome as funcao,\n" +
					"coalesce(c.cod_unidade, c.cod_unidade, -1) as cod_unidade, u.nome as unidade,\n" +
					"coalesce(c.cod_equipe, c.cod_equipe, -1) as cod_equipe, eq.nome as equipe\n" +
					"FROM calendario c  \n" +
					"join unidade u on u.codigo = c.cod_unidade  \n" +
					"join empresa e on e.codigo = u.cod_empresa\n" +
					"left join funcao f on f.codigo = c.cod_funcao\n" +
					"left join equipe eq on eq.cod_unidade = c.cod_unidade and eq.codigo = c.cod_equipe\n" +
					"WHERE E.CODIGO = ? AND C.data::DATE BETWEEN ? and ?) as f\n" +
					"WHERE F.cod_unidade::TEXT LIKE ?\n" +
					"AND F.cod_equipe::TEXT LIKE ? AND\n" +
					"  F.cod_funcao::TEXT LIKE ?");
			stmt.setLong(1, codEmpresa);
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setString(4, codUnidade);
			if(nomeEquipe.equals("%")){
				stmt.setString(5, "%");
			}else{
				stmt.setString(5, String.valueOf(empresaDao.getCodEquipeByCodUnidadeByNome(Long.parseLong(codUnidade), nomeEquipe)));
			}
			stmt.setString(6, codFuncao);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				e = new Evento();
				e.setCodigo(rSet.getLong("CODIGO"));
				e.setData(rSet.getDate("data"));
				e.setDescricao(rSet.getString("descricao"));
				e.setLocal(rSet.getString("local"));
				unidade = new Unidade();
				unidade.setCodigo(rSet.getLong("cod_unidade"));
				unidade.setNome(rSet.getString("unidade"));
				e.setUnidade(unidade);
				funcaoTreinamento = new Funcao();
				funcaoTreinamento.setCodigo(rSet.getLong("cod_funcao"));
				if (funcaoTreinamento.getCodigo() == -1){
					funcaoTreinamento.setNome("Todas");
				}else{
					funcaoTreinamento.setNome(rSet.getString("funcao"));
				}
				e.setFuncao(funcaoTreinamento);
				equipeTreinamento = new Equipe();
				equipeTreinamento.setCodigo(rSet.getLong("cod_equipe"));
				if (equipeTreinamento.getCodigo() == -1){
					equipeTreinamento.setNome("Todas");
				}else {
					equipeTreinamento.setNome(rSet.getString("equipe"));
				}
				e.setEquipe(equipeTreinamento);
				eventos.add(e);
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return eventos;
	}

	@Override
	public boolean delete (Long codUnidade, Long codEvento) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM calendario WHERE cod_unidade = ? AND codigo = ?");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codEvento);
			int count = stmt.executeUpdate();
			if (count > 0){
				return true;
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		return false;
	}

	@Override
	public AbstractResponse insert (Evento evento, String codUnidade, String codFuncao, String nomeEquipe) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Long nullLong = null;
		EmpresaDaoImpl empresaDao = new EmpresaDaoImpl();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO calendario(data, descricao, cod_unidade, cod_funcao, cod_equipe, local) " +
					"VALUES (?,?,?,?,?,?) returning codigo");
			stmt.setTimestamp(1, DateUtils.toTimestamp(evento.getData()));
			stmt.setString(2, evento.getDescricao());
			stmt.setLong(3, Long.parseLong(codUnidade));

			if (codFuncao.equals("%")){
				stmt.setNull(4, Types.BIGINT);
			}else {
				stmt.setLong(4, Long.parseLong(codFuncao));
			}

			if (nomeEquipe.equals("%")){
				stmt.setNull(5, Types.BIGINT);
			}else {
				stmt.setLong(5, empresaDao.getCodEquipeByCodUnidadeByNome(Long.parseLong(codUnidade), nomeEquipe));
			}
			stmt.setString(6, evento.getLocal());
			rSet = stmt.executeQuery();
			if(rSet.next()){
				return ResponseWithCod.Ok("Evento inserido com sucesso", rSet.getLong("codigo"));
			}else{
				return Response.Error("Erro ao inserir o evento");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	public boolean update (Evento evento, String codUnidade, String codFuncao, String nomeEquipe) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		EmpresaDaoImpl empresaDao = new EmpresaDaoImpl();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE CALENDARIO SET DATA = ?, DESCRICAO = ?, " +
					" COD_UNIDADE = ?, COD_FUNCAO = ?, COD_EQUIPE = ?, LOCAL = ? WHERE " +
					" CODIGO = ? AND COD_UNIDADE = ? ");
			stmt.setTimestamp(1, DateUtils.toTimestamp(evento.getData()));
			stmt.setString(2, evento.getDescricao());
			stmt.setLong(3, Long.parseLong(codUnidade));
			if (codFuncao.equals("%")){
				stmt.setNull(4, Types.BIGINT);
			}else {
				stmt.setLong(4, Long.parseLong(codFuncao));
			}

			if (nomeEquipe.equals("%")){
				stmt.setNull(5, Types.BIGINT);
			}else {
				stmt.setLong(5, empresaDao.getCodEquipeByCodUnidadeByNome(Long.parseLong(codUnidade), nomeEquipe));
			}
			stmt.setString(6, evento.getLocal());
			stmt.setLong(7, evento.getCodigo());
			stmt.setLong(8, Long.parseLong(codUnidade));
			int count = stmt.executeUpdate();
			if(count > 0){
				return true;
			}else{
				return false;
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
	}

}
