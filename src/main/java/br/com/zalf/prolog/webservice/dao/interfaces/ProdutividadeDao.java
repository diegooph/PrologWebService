package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.produtividade.ItemProdutividade;

public interface ProdutividadeDao {
	
	
	List<ItemProdutividade> getProdutividadeByPeriodo (LocalDate dataInicial, LocalDate dataFinal,
			Long cpf, String token) throws SQLException;
	
	List<ItemProdutividade> listItemProdutividade = new ArrayList<>();
	
	
	
	
	
	
	
}
