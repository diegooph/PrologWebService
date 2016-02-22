package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.CalendarioDao;

public class CalendarioDaoImpl extends DatabaseConnection implements CalendarioDao{

	private static final String BUSCA_EVENTOS = "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON " 
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "  
			+ "AND CAL.COD_FUNCAO = C.COD_FUNCAO "
			+ "AND CAL.COD_EQUIPE = C.COD_EQUIPE "
			+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
			+ "WHERE C.CPF=? "
			+ "UNION "
			+ "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO = C.COD_FUNCAO "
			+ "AND CAL.COD_EQUIPE IS NULL "
			+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
			+ "WHERE C.CPF=? "
			+ "UNION "
			+ "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO IS NULL "
			+ "AND CAL.COD_EQUIPE = C.COD_EQUIPE "
			+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
			+ "WHERE C.CPF=? "
			+ "UNION "
			+ "SELECT CAL.CODIGO, CAL.DESCRICAO, CAL.DATA, CAL.LOCAL FROM "
			+ "COLABORADOR C JOIN CALENDARIO CAL ON "
			+ "CAL.COD_UNIDADE = C.COD_UNIDADE "
			+ "AND CAL.COD_FUNCAO IS NULL "
			+ "AND CAL.COD_EQUIPE IS NULL "
			+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND TA.TOKEN = ? "
			+ "WHERE C.CPF=? ";

	public List<Evento> getEventosByCpf(Long cpf, String token) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Evento> listEvento = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EVENTOS);
			stmt.setLong(1, cpf);
			stmt.setString(2, token); 
			stmt.setLong(3, cpf); 
			stmt.setLong(4, cpf);
			stmt.setString(5, token); 
			stmt.setLong(6, cpf); 
			stmt.setLong(7, cpf);
			stmt.setString(8, token); 
			stmt.setLong(9, cpf); 
			stmt.setLong(10, cpf);
			stmt.setString(11, token); 
			stmt.setLong(12, cpf); 
			rSet = stmt.executeQuery();
			while(rSet.next()){
				Evento evento = new Evento();
				evento.setData(rSet.getTimestamp("DATA"));
				evento.setDescricao(rSet.getString("DESCRICAO"));
				evento.setCodigo(rSet.getLong("CODIGO"));
				evento.setLocal(rSet.getString("LOCAL"));
				System.out.println(evento.getCodigo());
				System.out.println(evento.getData());
				System.out.println(evento.getDescricao());
				System.out.println(evento.getLocal());
				listEvento.add(evento);
			}

		}finally{
			closeConnection(conn, stmt, rSet);
		}
		return listEvento;
	}
}
