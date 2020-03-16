package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

/**
 * Objeto que contem as informações do {@link PneuAfericaoProtheusRodalog pneu} utilizado para a realização da aferição.
 * <p>
 * Todos os atributos do pneu são buscados no endpoint integrado.
 * <p>
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * {@see protheusrodalog}
 */
public final class PneuAfericaoProtheusRodalog {

    /**
     * Atributo alfanumérico que representa o código pelo qual o usuário identifica o pneu. Normalmente costuma-se
     * utilizar o código de fogo do pneu para identificá-lo.
     */
    private String codigoCliente;

    /**
     * Código único de identificação do pneu no banco de dados.
     */
    private Long codigo;

    /**
     * Código da {@link Unidade unidade} a qual o pneu está alocado.
     */
    private Long codUnidadeAlocado;

    /**
     * Número inteiro que representa a vida atual do pneu. Vida do pneu pode ser interpretada como a quantidade de
     * recapes que o pneu já sofreu.
     */
    private Integer vidaAtual;

    /**
     * Número inteiro que representa o total de vidas do pneu. Pode-se interpretar este atributo como o total de recapes
     * que o pneu pode sofrer ao longo de toda a duração dele.
     */
    private Integer vidaTotal;

    /**
     * Número inteiro que representa a posição em que o pneu está aplicado no veículo.
     */
    private Integer posicao;

    /**
     * Valor estipulado como a pressão ideal para este pneu.
     */
    private Double pressaoCorreta;

    /**
     * Última medida de pressão capturada, ou, valor mais atual para a pressão do pneu.
     */
    private Double pressaoAtual;

    /**
     * Última medida do sulco interno capturado, ou, valor mais atual para o sulco interno do pneu.
     */
    private Double sulcoInternoAtual;

    /**
     * Última medida do sulco central interno capturado, ou, valor mais atual para o sulco central interno do pneu.
     */
    private Double sulcoCentralInternoAtual;

    /**
     * Última medida do sulco central externo capturado, ou, valor mais atual para o sulco central externo do pneu.
     */
    private Double sulcoCentralExternoAtual;

    /**
     * Última medida do sulco externo capturado, ou, valor mais atual para o sulco externo do pneu.
     */
    private Double sulcoExternoAtual;

    /**
     * Objeto que contém informações sobre o {@link ModeloPneuProtheusRodalog modelo do pneu}.
     */
    private ModeloPneuProtheusRodalog modeloPneu;

    /**
     * Objeto que contém informações sobre o {@link ModeloPneuProtheusRodalog modelo da banda} aplicada no pneu.
     * <p>
     * Este atributo só será utilizado para o caso de o pneu já ter sofrido algum recape.
     */
    private ModeloBandaProtheusRodalog modeloBanda;

    public PneuAfericaoProtheusRodalog() {
    }

    @NotNull
    static PneuAfericaoProtheusRodalog getPneuAfericaoDummy(final boolean isEstepe) {
        final PneuAfericaoProtheusRodalog pneu = new PneuAfericaoProtheusRodalog();
        pneu.setCodigoCliente("PN01");
        pneu.setCodigo(101010L);
        pneu.setCodUnidadeAlocado(29L);
        pneu.setVidaAtual(2);
        pneu.setVidaTotal(3);
        pneu.setPosicao(isEstepe ? 901 : 111);
        pneu.setPressaoAtual(105.0);
        pneu.setPressaoCorreta(115.0);
        pneu.setSulcoInternoAtual(10.0);
        pneu.setSulcoCentralInternoAtual(10.0);
        pneu.setSulcoCentralExternoAtual(10.0);
        pneu.setSulcoExternoAtual(10.0);
        pneu.setModeloPneu(ModeloPneuProtheusRodalog.getModeloPneuDummy());
        pneu.setModeloBanda(ModeloBandaProtheusRodalog.getModeloPneuDummy());
        return pneu;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(final String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    public void setCodUnidadeAlocado(final Long codUnidadeAlocado) {
        this.codUnidadeAlocado = codUnidadeAlocado;
    }

    public Integer getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(final Integer vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public Integer getVidaTotal() {
        return vidaTotal;
    }

    public void setVidaTotal(final Integer vidaTotal) {
        this.vidaTotal = vidaTotal;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(final Integer posicao) {
        this.posicao = posicao;
    }

    public Double getPressaoCorreta() {
        return pressaoCorreta;
    }

    public void setPressaoCorreta(final Double pressaoCorreta) {
        this.pressaoCorreta = pressaoCorreta;
    }

    public Double getPressaoAtual() {
        return pressaoAtual;
    }

    public void setPressaoAtual(final Double pressaoAtual) {
        this.pressaoAtual = pressaoAtual;
    }

    public Double getSulcoInternoAtual() {
        return sulcoInternoAtual;
    }

    public void setSulcoInternoAtual(final Double sulcoInternoAtual) {
        this.sulcoInternoAtual = sulcoInternoAtual;
    }

    public Double getSulcoCentralInternoAtual() {
        return sulcoCentralInternoAtual;
    }

    public void setSulcoCentralInternoAtual(final Double sulcoCentralInternoAtual) {
        this.sulcoCentralInternoAtual = sulcoCentralInternoAtual;
    }

    public Double getSulcoCentralExternoAtual() {
        return sulcoCentralExternoAtual;
    }

    public void setSulcoCentralExternoAtual(final Double sulcoCentralExternoAtual) {
        this.sulcoCentralExternoAtual = sulcoCentralExternoAtual;
    }

    public Double getSulcoExternoAtual() {
        return sulcoExternoAtual;
    }

    public void setSulcoExternoAtual(final Double sulcoExternoAtual) {
        this.sulcoExternoAtual = sulcoExternoAtual;
    }

    public ModeloPneuProtheusRodalog getModeloPneu() {
        return modeloPneu;
    }

    public void setModeloPneu(final ModeloPneuProtheusRodalog modeloPneu) {
        this.modeloPneu = modeloPneu;
    }

    public ModeloBandaProtheusRodalog getModeloBanda() {
        return modeloBanda;
    }

    public void setModeloBanda(final ModeloBandaProtheusRodalog modeloBanda) {
        this.modeloBanda = modeloBanda;
    }

    public boolean isRecapado() {
        // Se vidaAtual > 1 significa que o pneu já sofreu recape.
        return vidaAtual > 1;
    }

    public boolean temSulcosAtuais() {
        return this.sulcoExternoAtual != null
                && this.sulcoCentralExternoAtual != null
                && this.sulcoCentralInternoAtual != null
                && this.sulcoInternoAtual != null;
    }
}
