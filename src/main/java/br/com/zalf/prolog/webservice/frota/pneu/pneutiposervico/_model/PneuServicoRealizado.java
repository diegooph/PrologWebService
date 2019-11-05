package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import com.google.gson.Gson;

import java.math.BigDecimal;

/**
 * Created on 30/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuServicoRealizado {

    /**
     * Atributo que define o gatilho pelo qual este serviço foi necessário.
     * {@code FONTE_MOVIMENTACAO} indica que um processo de movimentação disparou a realização deste serviço.
     */
    public static final String FONTE_MOVIMENTACAO = "FONTE_MOVIMENTACAO";

    /**
     * Atributo que define o gatilho pelo qual este serviço foi necessário.
     * {@code FONTE_CADASTRO} indica que o cadastro de um {@link Pneu} cujo não está na primeira vida
     * disparou a criação deste serviço.
     */
    public static final String FONTE_CADASTRO = "FONTE_CADASTRO";

    private Long codigo;
    private Long codPneuTipoServico;
    private Long codUnidade;
    private Long codPneu;
    private BigDecimal custo;
    private Integer vidaMomentoRealizacaoServico;
    /**
     * Precisamos utilizar o {@link Exclude} para a serialização/desserialização das subclasses
     * funcionar corretamente utilizando o {@link Gson}.
     */
    @Exclude
    private String tipo;

    public PneuServicoRealizado() {
    }

    public static RuntimeTypeAdapterFactory<PneuServicoRealizado> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(PneuServicoRealizado.class, "tipo")
                .registerSubtype(PneuServicoRealizadoIncrementaVida.class,
                        PneuServicoRealizadoIncrementaVida.TIPO_SERVICO_REALIZADO_INCREMENTA_VIDA)
                .registerSubtype(PneuServicoRealizadoNaoIncrementaVida.class,
                        PneuServicoRealizadoNaoIncrementaVida.TIPO_SERVICO_REALIZADO_NAO_INCREMENTA_VIDA);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodPneuTipoServico() {
        return codPneuTipoServico;
    }

    public void setCodPneuTipoServico(final Long codPneuTipoServico) {
        this.codPneuTipoServico = codPneuTipoServico;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public Long getCodPneu() {
        return codPneu;
    }

    public void setCodPneu(final Long codPneu) {
        this.codPneu = codPneu;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public void setCusto(final BigDecimal custo) {
        this.custo = custo;
    }

    public Integer getVidaMomentoRealizacaoServico() {
        return vidaMomentoRealizacaoServico;
    }

    public void setVidaMomentoRealizacaoServico(final Integer vidaMomentoRealizacaoServico) {
        this.vidaMomentoRealizacaoServico = vidaMomentoRealizacaoServico;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "PneuServicoRealizado{" +
                "codigo=" + codigo +
                ", codPneuTipoServico=" + codPneuTipoServico +
                ", codUnidade=" + codUnidade +
                ", codPneu=" + codPneu +
                ", custo=" + custo +
                ", vidaMomentoRealizacaoServico=" + vidaMomentoRealizacaoServico +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
