package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.Date;
import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.Produtividade;

public interface ProdutividadeDao {
	
	List<Produtividade> getByColaboradorByDia (long cpf, Date dataInicial, Date dataFinal);
	
}
