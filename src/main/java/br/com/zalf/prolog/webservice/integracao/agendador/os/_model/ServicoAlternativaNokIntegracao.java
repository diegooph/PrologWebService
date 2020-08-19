package br.com.zalf.prolog.webservice.integracao.agendador.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class ServicoAlternativaNokIntegracao {

    @NotNull
    private final String descricaoFechamentoServico;

}
