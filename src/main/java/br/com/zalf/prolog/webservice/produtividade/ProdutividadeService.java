package br.com.zalf.prolog.webservice.produtividade;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.produtividade.ItemProdutividade;

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