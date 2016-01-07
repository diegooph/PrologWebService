package br.com.zalf.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.produtividade.ItemProdutividade;

public interface ProdutividadeDao {
	
	
	List<ItemProdutividade> getProdutividadeByPeriodo (LocalDate dataInicial, LocalDate dataFinal,
			long cpf) throws SQLException;
	
	List<ItemProdutividade> listItemProdutividade = new ArrayList<>();
	
	
	
	
	
	
	
}
