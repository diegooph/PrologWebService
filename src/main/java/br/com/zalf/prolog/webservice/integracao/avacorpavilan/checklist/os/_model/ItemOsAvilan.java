package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

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
    private String grupo = "1";
    @NotNull
    private String empresa = "1";
    @NotNull
    private String filial;
    @NotNull
    private String unidade;
    @NotNull
    private LocalDateTime dtinc;
    @NotNull
    private String defeito;
    @NotNull
    private String complemento;
    @NotNull
    private List<FechamentoOsAvilan> ordemServicoDefeitoServicoIn;

}
