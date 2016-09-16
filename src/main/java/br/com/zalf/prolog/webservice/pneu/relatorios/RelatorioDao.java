package br.com.zalf.prolog.webservice.pneu.relatorios;

import br.com.zalf.prolog.frota.pneu.Pneu;
import br.com.zalf.prolog.frota.pneu.relatorio.Aderencia;
import br.com.zalf.prolog.frota.pneu.relatorio.Faixa;
import br.com.zalf.prolog.frota.pneu.relatorio.ResumoServicos;

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
	 * @throws SQLException
	 */
	List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status)throws SQLException;

	/**
	 * busca pneus com base numa faixa
	 * @param inicioFaixa inicio da faixa
	 * @param fimFaixa fim da faixa
	 * @param codEmpresa código da empresa
	 * @param codUnidade código da unidade
	 * @param limit
	 * @param offset
	 * @return lista de pneus
	 * @throws SQLException
	 */
	List<Pneu> getPneusByFaixa(double inicioFaixa, double fimFaixa, Long codEmpresa, String codUnidade, long limit, long offset) throws SQLException;

	/**
	 * busca uma lista de aderencias com base em um filtro
	 * @param ano ano à ser buscadp
	 * @param mes mes a ser buscado
	 * @param codUnidade código da unidade
	 * @return lista de aderencias
	 * @throws SQLException
	 */
	List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException;

	/**
	 * busca uma lista de pneus com base em uma faixa de pressão
	 * @param codUnidades código da unidade
	 * @param status status do pneu
	 * @return lista de faixas
	 * @throws SQLException
	 */
	List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status)throws SQLException;

	/**
	 * busca um resumo de serviços com base em um filtro
	 * @param ano ano a ser buscado
	 * @param mes mes a ser buscado
	 * @param codUnidades código da unidade
	 * @return lista de serviços
	 * @throws SQLException
	 */
	List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) throws SQLException;

}
