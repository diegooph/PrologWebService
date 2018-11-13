package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

/**
 * Created on 13/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VersaoDadosMarcacao {
    /**
     * Atributo utilizado para guardar a versão em que os dados de Marcações se encontram,
     * para a unidade.
     */
    private Long versaoDadosBanco;

    /**
     * Atributo para salvar o Token que a unidade utiliza para sincronizar
     * as marcações de jornada.
     */
    private String tokenSincronizacaoMarcacao;

    public VersaoDadosMarcacao() {

    }

    public Long getVersaoDadosBanco() {
        return versaoDadosBanco;
    }

    public void setVersaoDadosBanco(final Long versaoDadosBanco) {
        this.versaoDadosBanco = versaoDadosBanco;
    }

    public String getTokenSincronizacaoMarcacao() {
        return tokenSincronizacaoMarcacao;
    }

    public void setTokenSincronizacaoMarcacao(final String tokenSincronizacaoMarcacao) {
        this.tokenSincronizacaoMarcacao = tokenSincronizacaoMarcacao;
    }
}
