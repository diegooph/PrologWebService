package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-06-19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class CargoPermissaoInfosBloqueio {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String motivoBloqueio;
    @NotNull
    private final String descricaoBloqueio;
}
