package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Este objeto consiste nas informações de cada {@link ModeloAfericaoRodoparHorizonte modelo de veículo} disponível para
 * a aferição dentro do {@link CronogramaAfericaoRodoparHorizonte cronograma de aferição}.
 * <p>
 * Todas as informações presentes neste objeto são recebidas através de um endpoint integrado. Assim, é de
 * responsábilidade do endpoint, fornecer as informações necessárias no padrão especificado por este objeto para que a
 * integração funcione corretamente.
 * <p>
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloAfericaoRodoparHorizonte {
    /**
     * Representação do modelo do veículo.
     */
    @NotNull
    private final String nomeModelo;
    /**
     * Código único de identificação deste modelo de veículo no banco de dados.
     */
    @NotNull
    private final Long codModelo;
    /**
     * Número inteiro que representa a quantidade de placas que estão associadas à este modelo.
     */
    @NotNull
    private final Long totalPlacasModelo;
    /**
     * Lista de {@link PlacaAfericaoRodoparHorizonte placas} que pertencem a este modelo de veículo.
     */
    @NotNull
    private final List<PlacaAfericaoRodoparHorizonte> placasAfericao;

    public ModeloAfericaoRodoparHorizonte(@NotNull final String nomeModelo,
                                          @NotNull final Long codModelo,
                                          @NotNull final Long totalPlacasModelo,
                                          @NotNull final List<PlacaAfericaoRodoparHorizonte> placasAfericao) {
        this.nomeModelo = nomeModelo;
        this.codModelo = codModelo;
        this.totalPlacasModelo = totalPlacasModelo;
        this.placasAfericao = placasAfericao;
    }

    @NotNull
    public String getNomeModelo() {
        return nomeModelo;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getTotalPlacasModelo() {
        return totalPlacasModelo;
    }

    @NotNull
    public List<PlacaAfericaoRodoparHorizonte> getPlacasAfericao() {
        return placasAfericao;
    }
}
