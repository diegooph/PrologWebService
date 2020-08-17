package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created on 2020-07-29
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class FamiliasModelosBloqueio {
    private List<String> familiasBloqueadas;
    private List<String> modelosBloqueados;
}
