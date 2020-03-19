package br.com.zalf.prolog.webservice.customfields;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class ColunaTabelaResposta {
    @NotNull
    private final String nomeColuna;
    @NotNull
    private final Object valorColuna;
}
