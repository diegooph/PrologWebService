package br.com.zalf.prolog.webservice.entrega.produtividade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutividadeService {

	private ProdutividadeDao dao = new ProdutividadeDaoImpl();
	
	public List<ItemProdutividade> getProdutividadeByPeriodo(int ano, int mes, Long cpf) {
		try {
			return dao.getProdutividadeByPeriodo(ano, mes, cpf, true);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<ItemProdutividade>();
		}
	}

	public List<HolderColaboradorProdutividade> getConsolidadoProdutividade(Long codUnidade, String equipe, String codFuncao,
																			long dataInicial, long dataFinal){
		try{
			return dao.getConsolidadoProdutividade(codUnidade, equipe, codFuncao, dataInicial, dataFinal);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}
}