package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.util.List;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class NovaAfericaoPlacaProtheusRodalog {

    private String placa;
    private Integer codDiagrama;
    private Double variacaoAceitaSulcoMenorMilimetros;
    private Double variacaoAceitaSulcoMaiorMilimetros;

    /**
     * A afreição de estepes é uma opção de cada {@link Unidade}. Cada Unidade define quais {@link TipoVeiculo}
     * terão seus estepes aferíveis ou não. Assim este atributo serve para dizer se devemos aferir
     * os {@code estepesVeiculo} do {@code veiculo} em questão.
     */
    private Boolean deveAferirEstepes;
    private RestricaoAfericaoProtheusRodalog restricao;
    private List<PneuAfericaoProtheusRodalog> pneusVeiculo;

    /**
     * Os estepes de um {@link Veiculo} não são aferíveis, por isso eles vêm separada em uma lista
     * e não junto dos {@link Veiculo#listPneus} do {@link Veiculo}
     */
    private List<PneuAfericaoProtheusRodalog> estepesVeiculo;
}
