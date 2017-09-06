package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

public enum EstadoIntervaloSupport {
    VERSAO_DESATUALIZADA("VERSAO_DESATUALIZADA"),
    VERSAO_ATUALIZADA("VERSAO_ATUALIZADA"),
    SEM_PERMISSAO_INTERVALO("SEM_PERMISSAO_INTERVALO");

    private final String key;

    EstadoIntervaloSupport(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}