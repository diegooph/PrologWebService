package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

public enum EstadoVersaoIntervalo {
    VERSAO_DESATUALIZADA("VERSAO_DESATUALIZADA"),
    VERSAO_ATUALIZADA("VERSAO_ATUALIZADA"),
    UNIDADE_SEM_USO_INTERVALO("UNIDADE_SEM_USO_INTERVALO");

    private final String key;

    EstadoVersaoIntervalo(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}