package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloPneuRodoparHorizonte {
    /**
     * Código único que indentifica este modelo de pneu no banco de dados.
     */
    @NotNull
    private final Long codigo;
    /**
     * Atributo alfanumérico que representa o nome deste modelo de pneu.
     */
    @NotNull
    private final String nomeModelo;
    /**
     * Valor inteiro que representa a quantidade de sulcos que o modelo possui.
     */
    @NotNull
    private final Integer quantidadeSulcos;
    /**
     * Valor que representa a altura dos sulcos do modelo de pneu quando novo.
     */
    @NotNull
    private final Double alturaSulcos;

    public ModeloPneuRodoparHorizonte(@NotNull final Long codigo,
                                      @NotNull final String nomeModelo,
                                      @NotNull final Integer quantidadeSulcos,
                                      @NotNull final Double alturaSulcos) {
        this.codigo = codigo;
        this.nomeModelo = nomeModelo;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNomeModelo() {
        return nomeModelo;
    }

    @NotNull
    public Integer getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    @NotNull
    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}
