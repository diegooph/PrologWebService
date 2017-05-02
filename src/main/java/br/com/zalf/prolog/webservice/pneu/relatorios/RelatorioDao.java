package br.com.zalf.prolog.webservice.pneu.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.frota.pneu.Pneu;
import br.com.zalf.prolog.frota.pneu.relatorio.Aderencia;
import br.com.zalf.prolog.frota.pneu.relatorio.Faixa;
import br.com.zalf.prolog.frota.pneu.relatorio.ResumoServicos;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by didi on 9/15/16.
 */
public interface RelatorioDao {

	/**
	 * seleciona pneus com base no sulco
	 * @param codUnidades código da unidade
	 * @param status status do pneu
	 * @return lista de faixas
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status)throws SQLException;

	/**
	 * busca pneus com base numa faixa
	 * @param inicioFaixa inicio da faixa
	 * @param fimFaixa fim da faixa
	 * @param codEmpresa código da empresa
	 * @param codUnidade código da unidade
	 * @param limit limite de busca de dados no banco
	 * @param offset offset de busca no banco
	 * @return lista de pneus
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<Pneu> getPneusByFaixaSulco(double inicioFaixa, double fimFaixa, Long codEmpresa, String codUnidade, long limit, long offset) throws SQLException;

	/**
	 * busca uma lista de aderencias com base em um filtro
	 * @param ano ano à ser buscadp
	 * @param mes mes a ser buscado
	 * @param codUnidade código da unidade
	 * @return lista de aderencias
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException;

	/**
	 * busca uma lista de pneus com base em uma faixa de pressão
	 * @param codUnidades código da unidade
	 * @param status status do pneu
	 * @return lista de faixas
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status)throws SQLException;

	/**
	 * busca um resumo de serviços com base em um filtro
	 * @param ano ano a ser buscado
	 * @param mes mes a ser buscado
	 * @param codUnidades código da unidade
	 * @return lista de serviços
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) throws SQLException;

	/**
	 * /**
	 * Relatório que gera a previsão de troca de um pneu, dados baseados no histórico de aferições
	 * @param codUnidade código da unidade a ser buscada
	 * @param dataInicial data inicial da troca
	 * @param dataFinal data final da troca
	 * @param outputStream um OutputStream
	 * @throws IOException
	 * @throws SQLException
	 */
	public void getPrevisaoTrocaCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException;

	/**
	 * Relatório que gera a previsão de troca de um pneu, dados baseados no histórico de aferições
	 * @param codUnidade código da unidade
	 * @param dataInicial data inicial
	 * @param dataFinal data final
	 * @return
	 * @throws SQLException
	 */
	public Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException;

	public void getPrevisaoTrocaConsolidadoCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException;

	public Report getPrevisaoTrocaConsolidadoReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException;

	public void getAderenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException;

	public Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException;

	public void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream) throws SQLException, IOException;

	public Report getDadosUltimaAfericaoReport(Long codUnidade) throws SQLException;

}
