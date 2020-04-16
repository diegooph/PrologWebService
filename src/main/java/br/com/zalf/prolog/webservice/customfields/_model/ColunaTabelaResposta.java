package br.com.zalf.prolog.webservice.customfields._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Representa uma coluna específica de uma tabela de respostas dos campos personalizados.
 * O nome das tabelas que salvam as respostas dos campos estão definidos no enum
 * {@link CampoPersonalizadoFuncaoProlog das funcionalidades do Prolog}.
 * <p>
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
