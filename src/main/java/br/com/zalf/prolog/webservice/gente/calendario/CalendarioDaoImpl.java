package br.com.zalf.prolog.webservice.gente.calendario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class CalendarioDaoImpl extends DatabaseConnection implements CalendarioDao{

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

//	public List<Evento> getAll (long dataInicial, long dataFinal, int limit, int offset,
//								String equipe, Long codUnidade, String status, String categoria){
//		Connection conn = null;
//		ResultSet rSet = null;
//		PreparedStatement stmt = null;
//		try{
//			conn = getConnection();
//			stmt = conn.prepareStatement("");
//		}
//
//
//
//
//	}
}
