package br.com.zalf.prolog.webservice;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 31/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Filtros {
    /**
     * Caractere que determina se todas as opções em uma listagem foram selecionadas.
     */
    private static final String FILTRO_TODOS = "%";

    private Filtros() {
        throw new IllegalStateException(Filtros.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean isFiltroTodos(@NotNull final String value) {
        return value.equals(FILTRO_TODOS);
    }

    @NotNull
    public static String getFiltroTodos() {
        return FILTRO_TODOS;
    }
}