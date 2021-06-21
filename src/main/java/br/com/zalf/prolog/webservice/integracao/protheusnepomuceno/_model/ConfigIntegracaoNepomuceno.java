package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public final class ConfigIntegracaoNepomuceno {
    private List<String> familiasBloqueadas;
    private List<String> modelosBloqueados;
    private List<String> placasBloqueadas;
    private List<String> deParaCamposPersonalizados;
    private String codSucataPneu;
}
