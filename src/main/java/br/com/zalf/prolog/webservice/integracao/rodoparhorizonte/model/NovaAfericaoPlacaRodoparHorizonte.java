package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class NovaAfericaoPlacaRodoparHorizonte {
    /**
     * Atributo alfanúmerico que representa a placa do veículo que será aferido.
     */
    @NotNull
    private final String placa;
    /**
     * Valor numérico que representa a última quilometragem da {@code placa}. Assumimos como a quilometragem mais atual
     * da placa.
     */
    @NotNull
    private final Long ultimoKmVeiculo;
    /**
     * Atributo numérico que representa o código da {@link Unidade unidade} em que a placa está alocada.
     */
    @NotNull
    private final Long codUnidadePlacaAlocada;
    /**
     * Atributo numérico que indica qual o diagrama do veiculo que será aferido. Esse código é o código usado no ProLog.
     * Diagrama consiste na organização dos pneus nos eixos do veículo, exemplos:
     * * Diagrama TOCO - possui 2 eixos e 2 pneus em cada eixo;
     * * Diagrama TRUCK - possui 3 eixos, 2 pneus no eixo direcional e 4 pneus em cada eixo restante.
     */
    @NotNull
    private final Long codDiagramaVeiculo;
    /**
     * Lista de {@link PneuAfericaoRodoparHorizonte pneus} aplicados aos eixos do veículo que será aferido.
     */
    @NotNull
    private final List<PneuAfericaoRodoparHorizonte> pneusVeiculo;
    /**
     * Lista de {@link PneuAfericaoRodoparHorizonte estepes} aplicados ao veículo que será aferido.
     */
    @NotNull
    private final List<PneuAfericaoRodoparHorizonte> estepesVeiculo;

    public NovaAfericaoPlacaRodoparHorizonte(@NotNull final String placa,
                                             @NotNull final Long ultimoKmVeiculo,
                                             @NotNull final Long codUnidadePlacaAlocada,
                                             @NotNull final Long codDiagramaVeiculo,
                                             @NotNull final List<PneuAfericaoRodoparHorizonte> pneusVeiculo,
                                             @NotNull final List<PneuAfericaoRodoparHorizonte> estepesVeiculo) {
        this.placa = placa;
        this.ultimoKmVeiculo = ultimoKmVeiculo;
        this.codUnidadePlacaAlocada = codUnidadePlacaAlocada;
        this.codDiagramaVeiculo = codDiagramaVeiculo;
        this.pneusVeiculo = pneusVeiculo;
        this.estepesVeiculo = estepesVeiculo;
    }

    @NotNull
    public String getPlaca() {
        return placa;
    }

    @NotNull
    public Long getUltimoKmVeiculo() {
        return ultimoKmVeiculo;
    }

    @NotNull
    public Long getCodUnidadePlacaAlocada() {
        return codUnidadePlacaAlocada;
    }

    @NotNull
    public Long getCodDiagramaVeiculo() {
        return codDiagramaVeiculo;
    }

    @NotNull
    public List<PneuAfericaoRodoparHorizonte> getPneusVeiculo() {
        return pneusVeiculo;
    }

    @NotNull
    public List<PneuAfericaoRodoparHorizonte> getEstepesVeiculo() {
        return estepesVeiculo;
    }
}
