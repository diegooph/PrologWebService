package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloBandaRodoparHorizonte {
    /**
     * Código único que indentifica este modelo de banda no banco de dados.
     */
    @NotNull
    private final Long codigo;
    /**
     * Atributo alfanumérico que representa o nome deste modelo de banda.
     */
    @NotNull
    private final String nomeModelo;
    /**
     * Valor inteiro que representa a quantidade de sulcos que o modelo de banda possui.
     */
    @NotNull
    private final Integer quantidadeSulcos;
    /**
     * Valor que representa a altura dos sulcos do modelo de banda quando novo.
     */
    @NotNull
    private final Double alturaSulcos;

    public ModeloBandaRodoparHorizonte(@NotNull final Long codigo,
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
