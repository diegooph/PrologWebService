package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.Empresa;
/**
 * 
 * Contém os métodos que geram os relatórios operacionais (indicadores)
 */
public interface RelatorioDao {
	/**
	 * Busca os itens do Filtro (empresa, unidade, equipe)
	 * @param cpf do solicitante, busca a partir das permissões
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return list de Empresa, contendo os itens do filtro
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	public List<Empresa> getFiltros(Long cpf, String token) throws SQLException;
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
