package br.com.zalf.prolog.webservice.entrega.relatorioOlder;

import br.com.zalf.prolog.entrega.relatorio.older.ConsolidadoHolder;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * 
 * Contém os métodos que geram os relatórios operacionais (indicadores)
 */
public interface RelatorioDao {

	/**
	 * Busca dos dados para gerar os relatórios e gráficos (indicadores, consolidadosDia, consolidadoHolder), 
	 * respeitando o período selecionado
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param equipe uma equipe
	 * @param codUnidade código da unidade a ser gerado os dados
	 * @param cpf do solicitante
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return objeto ConsolidadoHolder, contento todos os itens necessários para gerar os relatórios e gráficos
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	public ConsolidadoHolder getRelatorioByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
												   Long codUnidade, Long cpf, String token) throws SQLException;
		
}
