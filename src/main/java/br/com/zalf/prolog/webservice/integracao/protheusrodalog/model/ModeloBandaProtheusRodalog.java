package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Objeto buscado através de um endpoint integrado, onde contém informações referentes ao modelo da banda aplicada no
 * Pneu.
 * <p>
 * O pneu só deve possuir banda aplicada para o caso em que já tenha sofrido algum recape.
 * <p>
 * {@see protheusrodalog}
 */
public final class ModeloBandaProtheusRodalog {
    /**
     * Código único que indentifica este modelo de banda no banco de dados.
     */
    private Double codigo;

    /**
     * Atributo alfanumérico que representa o nome deste modelo de banda.
     */
    private String nomeModelo;

    /**
     * Valor inteiro que representa a quantidade de sulcos que o modelo de banda possui.
     */
    private Integer quantidadeSulcos;

    /**
     * Valor que representa a altura dos sulcos do modelo de banda quando novo.
     */
    private Double alturaSulcos;

    public ModeloBandaProtheusRodalog() {
    }

    public Double getCodigo() {
        return codigo;
    }

    public void setCodigo(final Double codigo) {
        this.codigo = codigo;
    }

    public String getNomeModelo() {
        return nomeModelo;
    }

    public void setNomeModelo(final String nomeModelo) {
        this.nomeModelo = nomeModelo;
    }

    public Integer getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public void setQuantidadeSulcos(final Integer quantidadeSulcos) {
        this.quantidadeSulcos = quantidadeSulcos;
    }

    public Double getAlturaSulcos() {
        return alturaSulcos;
    }

    public void setAlturaSulcos(final Double alturaSulcos) {
        this.alturaSulcos = alturaSulcos;
    }
}
