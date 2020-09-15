package br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 12/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public class VeiculoVisualizacaoPneu {
    @NotNull
    private final Long codigoPneu;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final String nomeMarcaPneu;
    @NotNull
    private final Long codMarcaPneu;
    @NotNull
    private final Long codUnidadeAlocado;
    @NotNull
    private final Long codRegionalAlocado;
    @NotNull
    private final Double pressaoAtual;
    private final int vidaAtual;
    private final int vidaTotal;
    private final boolean pneuNovoNuncaRodado;
    @NotNull
    private final String nomeModeloPneu;
    @NotNull
    private final Long codModeloPneu;
    private final int qtdSulcosModeloPneu;
    @NotNull
    private final Double alturaSulcosModeloPneu;
    private final int altura;
    private final int largura;
    @NotNull
    private final Double aro;
    @NotNull
    private final Long codDimensao;
    @NotNull
    private final Double pressaoRecomendada;
    @NotNull
    private final Double alturaSulcoCentralInterno;
    @NotNull
    private final Double alturaSulcoCentralExterno;
    @NotNull
    private final Double alturaSulcoInterno;
    @NotNull
    private final Double alturaSulcoExterno;
    @NotNull
    private final String dot;
    @NotNull
    private final Double valor;
    @Nullable
    private final Long codModeloBanda;
    @Nullable
    private final String nomeModeloBanda;
    private final int qtdSulcosModeloBanda;
    @Nullable
    private final Double alturaSulcosModeloBanda;
    @Nullable
    private final Long codMarcaBanda;
    @Nullable
    private final String nomeMarcaBanda;
    @Nullable
    private final Double valorBanda;
    private final int posicaoPneu;
    @NotNull
    private final String nomenclatura;
    @NotNull
    private final Long codVeiculoAplicado;
    @NotNull
    private final String placaAplicado;
}