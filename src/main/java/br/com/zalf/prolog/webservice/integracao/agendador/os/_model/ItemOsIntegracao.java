package br.com.zalf.prolog.webservice.integracao.agendador.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2020-08-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class ItemOsIntegracao {

    @NotNull
    private final Long codItemOs;
    @NotNull
    private final Long codAlternativa;
    @NotNull
    private final String codAuxiliarAlternativa;
    @NotNull
    private final String descricaoAlternativa;
    @NotNull
    private final LocalDateTime dataHoraFechamento;
    @NotNull
    private final String descricaoFechamentoItem;

}
