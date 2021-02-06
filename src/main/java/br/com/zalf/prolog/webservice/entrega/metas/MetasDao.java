package br.com.zalf.prolog.webservice.entrega.metas;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Contém os métodos para gerenciamento das metas operacionais (indicadores)
 */
public interface MetasDao {

	@NotNull
	Optional<Metas> getByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

	void update(@NotNull final Metas metas, @NotNull final Long codUnidade) throws Throwable;

}