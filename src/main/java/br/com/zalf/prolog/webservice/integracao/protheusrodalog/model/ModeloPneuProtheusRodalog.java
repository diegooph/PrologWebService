package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Objeto buscado através de um endpoint integrado, onde contém informações referentes ao modelo do pneu.
 * <p>
 * {@see protheusrodalog}
 */
public final class ModeloPneuProtheusRodalog {
    /**
     * Código único que indentifica este modelo de pneu no banco de dados.
     */
    private Double codigo;

    /**
     * Atributo alfanumérico que representa o nome deste modelo de pneu.
     */
    private String nomeModelo;

    /**
     * Valor inteiro que representa a quantidade de sulcos que o modelo possui.
     */
    private Integer quantidadeSulcos;

    /**
     * Valor que representa a altura dos sulcos do modelo de pneu quando novo.
     */
    private Double alturaSulcos;

    public ModeloPneuProtheusRodalog() {
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
