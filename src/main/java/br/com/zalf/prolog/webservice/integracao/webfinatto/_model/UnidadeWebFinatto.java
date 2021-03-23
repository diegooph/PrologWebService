package br.com.zalf.prolog.webservice.integracao.webfinatto._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class UnidadeWebFinatto {
    @NotNull
    private final Long idUnidade;
    @NotNull
    private final String descricaoUnidade;
}
