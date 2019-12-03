package br.com.zalf.prolog.webservice.entrega.metas;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Contém os métodos para gerenciamento das metas operacionais (indicadores)
 */
public interface MetasDao {

	/**
	 * Retorna as metas da unidade.
	 *
	 * @param codUnidade Código da unidade.
	 * @return Um objeto {@link Metas metas} contendo os valores de cada meta daquela unidade.
	 * @throws Throwable Caso aconteça algum erro.
	 */
	@NotNull
	Optional<Metas> getByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

	/**
	 * Altera as metas de uma unidade.
	 *
	 * @param metas      Objeto contendo as novas metas.
	 * @param codUnidade Código da unidade que irá atualizar as metas.
	 * @throws Throwable Caso aconteça algum erro.
	 */
	void update(@NotNull final Metas metas, @NotNull final Long codUnidade) throws Throwable;

}