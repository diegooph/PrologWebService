package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutividadeService {

	private ProdutividadeDao dao = new ProdutividadeDaoImpl();
	private static final String TAG = ProdutividadeService.class.getSimpleName();
	
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

	public PeriodoProdutividade getPeriodoProdutividade(int ano, int mes, Long codUnidade, Long cpf) {
		try {
			return dao.getPeriodoProdutividade(ano, mes, codUnidade, cpf);
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao buscar o período da produtividade da unidade/cpf %d/%d", codUnidade, cpf), e);
			return null;
		}
	}
}