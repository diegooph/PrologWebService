package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-08-07
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class ModelosChecklistBloqueados {

    @NotNull
    private final Long codUnidade;
    @NotNull
    private final List<Long> codModelosBloqueados;

}
