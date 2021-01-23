package br.com.zalf.prolog.webservice.entrega.mapa;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Contém os métodos para import da tabela Mapa (ambev: 2art)
 */
public interface MapaDao {
	
	void insertOrUpdateMapa(@NotNull final Long codUnidade,
							@NotNull final List<String[]> planilhaMapa) throws Throwable;
}
