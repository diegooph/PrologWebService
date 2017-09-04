package br.com.zalf.prolog.webservice.colaborador;

public enum EstadoIntervaloSupport {
    /**
     * Para esse estado a ação a ser tomada é salvar os dados
     * recebidos no BD local.
     */
    DATA_DESATUALIZADA("DATA_DESATUALIZADA"),

    /**
     * Para esse estado, deve-se apenas abrir o aplicativo
     * seguindo o fluxo normal da tela.
     */
    DATA_ATUALIZADA("DATA_ATUALIZADA"),

    /**
     * Neste estado devemos apagar as informações contidas no BD local.
     */
    SEM_PERMISSAO_INTERVALO("SEM_PERMISSAO_INTERVALO");

    private final String key;

    EstadoIntervaloSupport(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}