package br.com.zalf.prolog.webservice.gente.calendario;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.L;

public class CalendarioDaoImpl extends DatabaseConnection implements CalendarioDao{

	private static final String tag = CalendarioDaoImpl.class.getSimpleName();
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

	public List<Evento> getAll (long dataInicial, long dataFinal, Long codEmpresa, String codUnidade, String equipe, String funcao) throws SQLException{

		L.d(tag, new Date(dataInicial).toString());
		L.d(tag, new Date(dataFinal).toString());
		L.d(tag, String.valueOf(codEmpresa));
		L.d(tag, String.valueOf(codUnidade));
		L.d(tag, equipe);
		L.d(tag, String.valueOf(funcao));

		Connection conn = null;
		ResultSet rSet = null;
		PreparedStatement stmt = null;
		List<Evento> eventos = new ArrayList<>();
		Evento e = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM\n" +
					"(SELECT c.data, c.codigo, c.descricao, c.local, coalesce(c.cod_funcao, c.cod_funcao, -1) as cod_funcao, " +
					"coalesce(c.cod_unidade, cod_unidade, -1) as cod_unidade, " +
					"coalesce(c.cod_equipe, c.cod_equipe, -1) as cod_equipe " +
					"FROM calendario c " +
					"join unidade u on u.codigo = c.cod_unidade " +
					"join empresa e on e.codigo = u.cod_empresa " +
					"WHERE E.CODIGO = ? AND C.data::DATE BETWEEN ? and ?) as f " +
					"WHERE F.cod_unidade::TEXT LIKE ? " +
					"AND F.cod_equipe::TEXT LIKE ? AND " +
					"\tF.cod_funcao::TEXT LIKE ?");
			stmt.setLong(1, codEmpresa);
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setString(4, codUnidade);
			stmt.setString(5, equipe);
			stmt.setString(6, funcao);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				e = new Evento();
				e.setCodigo(rSet.getLong("CODIGO"));
				e.setData(rSet.getDate("data"));
				e.setDescricao(rSet.getString("descricao"));
				e.setLocal(rSet.getString("local"));
				eventos.add(e);
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return eventos;
	}
}
