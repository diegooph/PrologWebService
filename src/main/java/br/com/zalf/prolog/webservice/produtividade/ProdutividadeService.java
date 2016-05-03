package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.produtividade.ItemProdutividade;
import br.com.zalf.prolog.webservice.dao.ProdutividadeDaoImpl;

public class ProdutividadeService {
	private ProdutividadeDaoImpl dao = new ProdutividadeDaoImpl();
	
	public List<ItemProdutividade> getProdutividadeByPeriodo(
			LocalDate dataInicial, 
			LocalDate dataFinal, 
			Long cpf, String token) {
		try {
			return dao.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<ItemProdutividade>();
		}
	}
}