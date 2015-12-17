package br.com.empresa.oprojeto.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.produtividade.ItemProdutividade;
import br.com.empresa.oprojeto.webservice.dao.ProdutividadeDaoImpl;

public class ProdutividadeService {
	private ProdutividadeDaoImpl dao = new ProdutividadeDaoImpl();
	
	public List<ItemProdutividade> getProdutividadeByPeriodo(
			LocalDate dataInicial, 
			LocalDate dataFinal, 
			long cpf) {
		try {
			return dao.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<ItemProdutividade>();
		}
	}
}