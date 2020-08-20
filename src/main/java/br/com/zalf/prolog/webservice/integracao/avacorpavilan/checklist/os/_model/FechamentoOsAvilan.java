package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class FechamentoOsAvilan {

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
    private String servicoRealizado;
    @NotNull
    private String complemento;
    @NotNull
    private String objetivoOrdemServico = "1";

}
