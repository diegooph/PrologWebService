package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.SincronizadorChecklistsAvilan;

import java.sql.SQLException;


public class Main {

	public static void main(String[] args) throws SQLException {
		new SincronizadorChecklistsAvilan().sync();
	}
}