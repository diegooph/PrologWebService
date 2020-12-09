package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created on 2020-07-29
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
@ToString
public final class FamiliasModelosPlacasBloqueio {
    private List<String> familiasBloqueadas;
    private List<String> modelosBloqueados;
    private List<String> placasBloqueadas;
}
