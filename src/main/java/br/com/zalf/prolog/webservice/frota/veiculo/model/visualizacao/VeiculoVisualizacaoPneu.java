package br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 12/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
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
    @Nullable
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

    public VeiculoVisualizacaoPneu(@NotNull final Long codigoPneu,
                                   @NotNull final String codigoCliente,
                                   @NotNull final String nomeMarcaPneu,
                                   @NotNull final Long codMarcaPneu,
                                   @NotNull final Long codUnidadeAlocado,
                                   @NotNull final Long codRegionalAlocado,
                                   @NotNull final Double pressaoAtual,
                                   final int vidaAtual,
                                   final int vidaTotal,
                                   @NotNull final Boolean pneuNovoNuncaRodado,
                                   @NotNull final String nomeModeloPneu,
                                   @NotNull final Long codModeloPneu,
                                   final int qtdSulcosModeloPneu,
                                   @NotNull final Double alturaSulcosModeloPneu,
                                   final int altura,
                                   final int largura,
                                   @NotNull final Double aro,
                                   @NotNull final Long codDimensao,
                                   @NotNull final Double pressaoRecomendada,
                                   @NotNull final Double alturaSulcoCentralInterno,
                                   @NotNull final Double alturaSulcoCentralExterno,
                                   @NotNull final Double alturaSulcoInterno,
                                   @NotNull final Double alturaSulcoExterno,
                                   @NotNull final String dot,
                                   @NotNull final Double valor,
                                   @Nullable final Long codModeloBanda,
                                   @Nullable final String nomeModeloBanda,
                                   final int qtdSulcosModeloBanda,
                                   @Nullable final Double alturaSulcosModeloBanda,
                                   @Nullable final Long codMarcaBanda,
                                   @Nullable final String nomeMarcaBanda,
                                   @Nullable final Double valorBanda,
                                   final int posicaoPneu,
                                   @NotNull final String nomenclatura,
                                   @NotNull final Long codVeiculoAplicado,
                                   @NotNull final String placaAplicado) {
        this.codigoPneu = codigoPneu;
        this.codigoCliente = codigoCliente;
        this.nomeMarcaPneu = nomeMarcaPneu;
        this.codMarcaPneu = codMarcaPneu;
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.codRegionalAlocado = codRegionalAlocado;
        this.pressaoAtual = pressaoAtual;
        this.vidaAtual = vidaAtual;
        this.vidaTotal = vidaTotal;
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
        this.nomeModeloPneu = nomeModeloPneu;
        this.codModeloPneu = codModeloPneu;
        this.qtdSulcosModeloPneu = qtdSulcosModeloPneu;
        this.alturaSulcosModeloPneu = alturaSulcosModeloPneu;
        this.altura = altura;
        this.largura = largura;
        this.aro = aro;
        this.codDimensao = codDimensao;
        this.pressaoRecomendada = pressaoRecomendada;
        this.alturaSulcoCentralInterno = alturaSulcoCentralInterno;
        this.alturaSulcoCentralExterno = alturaSulcoCentralExterno;
        this.alturaSulcoInterno = alturaSulcoInterno;
        this.alturaSulcoExterno = alturaSulcoExterno;
        this.dot = dot;
        this.valor = valor;
        this.codModeloBanda = codModeloBanda;
        this.nomeModeloBanda = nomeModeloBanda;
        this.qtdSulcosModeloBanda = qtdSulcosModeloBanda;
        this.alturaSulcosModeloBanda = alturaSulcosModeloBanda;
        this.codMarcaBanda = codMarcaBanda;
        this.nomeMarcaBanda = nomeMarcaBanda;
        this.valorBanda = valorBanda;
        this.posicaoPneu = posicaoPneu;
        this.nomenclatura = nomenclatura;
        this.codVeiculoAplicado = codVeiculoAplicado;
        this.placaAplicado = placaAplicado;
    }

    public Long getCodigoPneu() {
        return codigoPneu;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public String getNomeMarcaPneu() {
        return nomeMarcaPneu;
    }

    public Long getCodMarcaPneu() {
        return codMarcaPneu;
    }

    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    public Long getCodRegionalAlocado() {
        return codRegionalAlocado;
    }

    public Double getPressaoAtual() {
        return pressaoAtual;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public int getVidaTotal() {
        return vidaTotal;
    }

    public Boolean getPneuNovoNuncaRodado() {
        return pneuNovoNuncaRodado;
    }

    public String getNomeModeloPneu() {
        return nomeModeloPneu;
    }

    public Long getCodModeloPneu() {
        return codModeloPneu;
    }

    public int getQtdSulcosModeloPneu() {
        return qtdSulcosModeloPneu;
    }

    public Double getAlturaSulcosModeloPneu() {
        return alturaSulcosModeloPneu;
    }

    public int getAltura() {
        return altura;
    }

    public int getLargura() {
        return largura;
    }

    public Double getAro() {
        return aro;
    }

    public Long getCodDimensao() {
        return codDimensao;
    }

    public Double getPressaoRecomendada() {
        return pressaoRecomendada;
    }

    public Double getAlturaSulcoCentralInterno() {
        return alturaSulcoCentralInterno;
    }

    public Double getAlturaSulcoCentralExterno() {
        return alturaSulcoCentralExterno;
    }

    public Double getAlturaSulcoInterno() {
        return alturaSulcoInterno;
    }

    public Double getAlturaSulcoExterno() {
        return alturaSulcoExterno;
    }

    public String getDot() {
        return dot;
    }

    public Double getValor() {
        return valor;
    }

    public Long getCodModeloBanda() {
        return codModeloBanda;
    }

    public String getNomeModeloBanda() {
        return nomeModeloBanda;
    }

    public int getQtdSulcosModeloBanda() {
        return qtdSulcosModeloBanda;
    }

    public Double getAlturaSulcosModeloBanda() {
        return alturaSulcosModeloBanda;
    }

    public Long getCodMarcaBanda() {
        return codMarcaBanda;
    }

    public String getNomeMarcaBanda() {
        return nomeMarcaBanda;
    }

    public Double getValorBanda() {
        return valorBanda;
    }

    public int getPosicaoPneu() {
        return posicaoPneu;
    }

    public String getNomenclatura() {
        return nomenclatura;
    }

    public Long getCodVeiculoAplicado() {
        return codVeiculoAplicado;
    }

    public String getPlacaAplicado() {
        return placaAplicado;
    }
}