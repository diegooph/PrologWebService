package br.com.zalf.prolog.webservice.entrega.mapa;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Contém os métodos para import da tabela Mapa (ambev: 2art)
 */
public interface MapaDao {

	/**
	 * Este método insere toda a planilha de mapas importada no banco de dados. Caso uma entrada (um mapa) já exista,
	 * ele será atualizado. A chave para verificar se um mapa existe é o código desse mapa + o {@code codUnidade}.
	 *
	 * @param codUnidade   código da unidade onde os dados serão salvos.
	 * @param planilhaMapa os dados da planilha de mapas que serão salvos.
	 * @throws Throwable caso algum erro ocorrer.
	 */
	void insertOrUpdateMapa(@NotNull final Long codUnidade,
							@NotNull final List<String[]> planilhaMapa) throws Throwable;
}
