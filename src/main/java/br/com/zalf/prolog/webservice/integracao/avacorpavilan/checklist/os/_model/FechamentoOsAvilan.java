package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class FechamentoOsAvilan {

    @NotNull
    private final String grupo = "1";
    @NotNull
    private final String empresa = "1";
    @Nullable
    private final String filial;
    @Nullable
    private final String unidade;
    @Nullable
    private final LocalDateTime dtinc;
    @Nullable
    private final String servicoRealizado;
    @Nullable
    private final String complemento;
    @NotNull
    private final String objetivoOrdemServico = "1";

}
