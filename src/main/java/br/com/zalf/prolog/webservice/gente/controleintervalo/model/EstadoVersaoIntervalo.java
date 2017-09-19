package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;

public enum EstadoVersaoIntervalo {
    VERSAO_DESATUALIZADA("VERSAO_DESATUALIZADA"),
    VERSAO_ATUALIZADA("VERSAO_ATUALIZADA"),

    /**
     * Estado utilizado caso uma {@link Unidade unidade} nunca tenha usado nada referente ao controle de intervalo
     * ou, caso já tenha utilizado, se atualmente não houver nenhum {@link Colaborador colaborador} dessa
     * {@link Unidade unidade} com acesso ao controle de intervalo.
     */
    UNIDADE_SEM_USO_INTERVALO("UNIDADE_SEM_USO_INTERVALO");

    private final String key;

    EstadoVersaoIntervalo(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}