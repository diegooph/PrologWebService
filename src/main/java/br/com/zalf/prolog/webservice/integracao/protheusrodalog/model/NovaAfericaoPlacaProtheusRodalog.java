package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class NovaAfericaoPlacaProtheusRodalog {
    /**
     * Atributo alfanúmerico que representa a placa do veículo que será aferido.
     */
    private String placa;

    /**
     * Valor numérico que representa a última quilometragem da {@code placa}. Assumimos como a quilometragem mais atual
     * da placa.
     */
    private Long ultimoKmVeiculo;

    /**
     * Atributo numérico que representa o código da {@link Unidade unidade} em que a placa está alocada.
     */
    private Long codUnidadePlacaAlocada;

    /**
     * Atributo numérico que indica qual o diagrama do veículo que será aferido. Diagrama consiste na organização dos
     * pneus nos eixos do veículo, exemplos:
     * * Diagrama TOCO - possui 2 eixos e 2 pneus cada eixo;
     * * Diagrama TRUCK - possui 3 eixos, 2 pneus no eixo direcional e 4 pneus em cada eixo restante.
     */
    private Integer codDiagrama;

    /**
     * Variação de perda de milimitragem que o ProLog aceitará no momento da aferição de cada sulco.
     * <p>
     * Supondo que a última medida do sulco seja de 15.0 milímetros e a {@code variacaoAceitaSulcoMenorMilimetros}
     * seja de 2.0 milímetros, no momento que o usuário medir o sulco atual do pneu, se a medida estiver abaixo de
     * 13.0 milímetros (última medida - variação), o ProLog irá mostrar um aviso, pedindo que o usuário confirme aquela
     * medida ou realize novamente a medição daquele sulco.
     */
    private Double variacaoAceitaSulcoMenorMilimetros;

    /**
     * Variação de ganho de milimitragem que o ProLog aceitará no momento da aferição de cada sulco.
     * <p>
     * Supondo que a última medida do sulco seja de 15.0 milímetros e a {@code variacaoAceitaSulcoMaiorMilimetros}
     * seja de 2.0 milímetros, no momento que o usuário medir o sulco atual do pneu, se a medida estiver acima de
     * 17.0 milímetros (última medida + variação), o ProLog irá mostrar um aviso, pedindo que o usuário confirme aquela
     * medida ou realize novamente a medição daquele sulco.
     */
    private Double variacaoAceitaSulcoMaiorMilimetros;

    /**
     * Atributo booleano que indica se deve-se aferir os estepes do veículo ou não.
     * <p>
     * A aferição de estepes é uma opção de cada {@link Unidade unidade}. Cada unidade define quais
     * {@link TipoVeiculo tipos de veículos} terão seus estepes aferíveis ou não.
     */
    private Boolean deveAferirEstepes;

    /**
     * Objeto que contém informações para aplicar a regra de negócio do ProLog sobre as medições dos pneus.
     * {@see RestricaoAfericaoProtheusRodalog}
     */
    private RestricaoAfericaoProtheusRodalog restricao;

    /**
     * Lista de {@link PneuAfericaoProtheusRodalog pneus} aplicados aos eixos do veículo que será aferido.
     */
    private List<PneuAfericaoProtheusRodalog> pneusVeiculo;

    /**
     * Lista de {@link PneuAfericaoProtheusRodalog estepes} aplicados ao veículo que será aferido. Estes pneus só
     * serão aferidos se a flag {@code deveAferirEstepes} for setada como <code>TRUE</code>.
     */
    private List<PneuAfericaoProtheusRodalog> estepesVeiculo;

    public NovaAfericaoPlacaProtheusRodalog() {
    }

    @NotNull
    public static NovaAfericaoPlacaProtheusRodalog createNovaAfericaoDummy() {
        final NovaAfericaoPlacaProtheusRodalog novaAfericao = new NovaAfericaoPlacaProtheusRodalog();
        novaAfericao.setPlaca("PRO0001");
        novaAfericao.setUltimoKmVeiculo(10000L);
        novaAfericao.setCodUnidadePlacaAlocada(29L);
        novaAfericao.setCodDiagrama(1);
        novaAfericao.setVariacaoAceitaSulcoMaiorMilimetros(2.0);
        novaAfericao.setVariacaoAceitaSulcoMenorMilimetros(2.0);
        novaAfericao.setDeveAferirEstepes(true);
        novaAfericao.setRestricao(RestricaoAfericaoProtheusRodalog.getRestricaoDummy());
        final List<PneuAfericaoProtheusRodalog> pneusVeiculo = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            pneusVeiculo.add(PneuAfericaoProtheusRodalog.getPneuAfericaoDummy(false));
        }
        novaAfericao.setPneusVeiculo(pneusVeiculo);
        final List<PneuAfericaoProtheusRodalog> estepes = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            estepes.add(PneuAfericaoProtheusRodalog.getPneuAfericaoDummy(true));
        }
        novaAfericao.setEstepesVeiculo(estepes);
        return novaAfericao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(final String placa) {
        this.placa = placa;
    }

    public Long getUltimoKmVeiculo() {
        return ultimoKmVeiculo;
    }

    public void setUltimoKmVeiculo(final Long ultimoKmVeiculo) {
        this.ultimoKmVeiculo = ultimoKmVeiculo;
    }

    public Long getCodUnidadePlacaAlocada() {
        return codUnidadePlacaAlocada;
    }

    public void setCodUnidadePlacaAlocada(final Long codUnidadePlacaAlocada) {
        this.codUnidadePlacaAlocada = codUnidadePlacaAlocada;
    }

    public Integer getCodDiagrama() {
        return codDiagrama;
    }

    public void setCodDiagrama(final Integer codDiagrama) {
        this.codDiagrama = codDiagrama;
    }

    public Double getVariacaoAceitaSulcoMenorMilimetros() {
        return variacaoAceitaSulcoMenorMilimetros;
    }

    public void setVariacaoAceitaSulcoMenorMilimetros(final Double variacaoAceitaSulcoMenorMilimetros) {
        this.variacaoAceitaSulcoMenorMilimetros = variacaoAceitaSulcoMenorMilimetros;
    }

    public Double getVariacaoAceitaSulcoMaiorMilimetros() {
        return variacaoAceitaSulcoMaiorMilimetros;
    }

    public void setVariacaoAceitaSulcoMaiorMilimetros(final Double variacaoAceitaSulcoMaiorMilimetros) {
        this.variacaoAceitaSulcoMaiorMilimetros = variacaoAceitaSulcoMaiorMilimetros;
    }

    public Boolean getDeveAferirEstepes() {
        return deveAferirEstepes;
    }

    public void setDeveAferirEstepes(final Boolean deveAferirEstepes) {
        this.deveAferirEstepes = deveAferirEstepes;
    }

    public RestricaoAfericaoProtheusRodalog getRestricao() {
        return restricao;
    }

    public void setRestricao(final RestricaoAfericaoProtheusRodalog restricao) {
        this.restricao = restricao;
    }

    public List<PneuAfericaoProtheusRodalog> getPneusVeiculo() {
        return pneusVeiculo;
    }

    public void setPneusVeiculo(final List<PneuAfericaoProtheusRodalog> pneusVeiculo) {
        this.pneusVeiculo = pneusVeiculo;
    }

    public List<PneuAfericaoProtheusRodalog> getEstepesVeiculo() {
        return estepesVeiculo;
    }

    public void setEstepesVeiculo(final List<PneuAfericaoProtheusRodalog> estepesVeiculo) {
        this.estepesVeiculo = estepesVeiculo;
    }
}
