package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 30/08/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface AfericaoRelatorioDao {

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em arquivo CSV.
     *
     * @param out         - Streaming onde os dados serão escritos.
     * @param codUnidade  - Código da unidade pela qual as informações serão filtradas.
     * @throws Throwable - Se algum erro ocorrer.
     */

    void getDadosGeraisProduditivdadeCsv(@NotNull final OutputStream out,
                                         @NotNull final Long codUnidade) throws Throwable;

}
