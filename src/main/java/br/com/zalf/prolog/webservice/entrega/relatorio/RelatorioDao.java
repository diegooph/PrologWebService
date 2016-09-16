package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.entrega.indicador.older.*;
import br.com.zalf.prolog.entrega.relatorio.ConsolidadoHolder;

import java.sql.ResultSet;
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
	ConsolidadoHolder getRelatorioByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
												   Long codUnidade, Long cpf, String token) throws SQLException;

	/**
	 * cria um item de devolução de caixa
	 * @param rSet conjunto de informações do banco de dados
	 * @return um objeto de devolução
	 * @throws SQLException caso não for possivel criar
	 */
	ItemDevolucaoCx createDevCx(ResultSet rSet) throws SQLException;

	/**
	 * cria um item de devolução de
	 * @param rSet conjunto de informações do banco de dados
	 * @return
	 * @throws SQLException
	 */
	ItemDevolucaoNf createDevNf(ResultSet rSet) throws SQLException;

	ItemDevolucaoHl createDevHl(ResultSet rSet) throws SQLException;

	ItemTempoInterno createTempoInterno(ResultSet rSet) throws SQLException;

	ItemTempoRota createTempoRota(ResultSet rSet) throws SQLException;

	ItemTempoLargada createTempoLargada(ResultSet rSet) throws SQLException;

	ItemJornadaLiquida createJornadaLiquida(ResultSet rSet) throws SQLException;

	ItemTracking createTracking (ResultSet rSet) throws SQLException;


		
}
