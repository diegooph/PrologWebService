package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class ItemOsAvilan {

    @NotNull
    private final String grupo = "1";
    @NotNull
    private final String empresa = "1";
    @Nullable
    private final String filial;
    @Nullable
    private final String unidade;
    @NotNull
    private final LocalDateTime dtinc;
    @Nullable
    private final String defeito;
    @NotNull
    private final String complemento;
    @NotNull
    private final List<FechamentoOsAvilan> ordemServicoDefeitoServicoIn;

}
