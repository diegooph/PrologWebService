package br.com.zalf.prolog.webservice.pneu.afericao;

import br.com.zalf.prolog.frota.pneu.Restricao;
import br.com.zalf.prolog.frota.pneu.afericao.Afericao;
import br.com.zalf.prolog.frota.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.frota.pneu.afericao.SelecaoPlacaAfericao;

import java.sql.SQLException;
import java.util.List;

public interface AfericaoDao {

	/**
	 * insere uma aferição lincada com o código da unidade
	 * @param afericao uma aferição
	 * @param codUnidade código da unidade
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean insert(Afericao afericao, Long codUnidade) throws SQLException;

	/**
	 * adiciona uma aferição ao veículo da placa
	 * @param placa placa do veículo
	 * @return retorna uma nova aferição
	 * @throws SQLException
	 */
	NovaAfericao getNovaAfericao(String placa) throws SQLException;

	/**
	 * retorna as restrições de medidas da unidade
	 * @param codUnidade código da unidade
	 * @return a restrição da unidade
	 * @throws SQLException
	 */
	Restricao getRestricoesByCodUnidade(Long codUnidade) throws SQLException;

	/**
	 * retorna as restrições de medidas da placa
	 * @param placa placa do veículo
	 * @return a restrição da placa
	 * @throws SQLException
	 */
	Restricao getRestricoesByPlaca(String placa) throws SQLException;

	//TODO - comentar o javadoc
	SelecaoPlacaAfericao getSelecaoPlacaAfericao(Long codUnidade) throws SQLException;

	/**
	 * pega a lista de aferições executadas
	 * @param codUnidades código da unidade
	 * @param placas placa do veículo
	 * @param limit
	 * @param offset
	 * @return uma lista de aferições
	 * @throws SQLException
	 */
	List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, long limit, long offset) throws SQLException;

	/**
	 * retorna uma aferição através do código dela
	 * @param codAfericao código da aferição
	 * @param codUnidade código da unidade
	 * @return a aferição
	 * @throws SQLException
	 */
	Afericao getByCod (Long codAfericao, Long codUnidade) throws SQLException;

}
