package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;

import java.math.BigDecimal;

/**
 * Created on 30/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ServicoRealizadoRecapadora {

    private Long codigo;
    private Long codTipoServicoRecapadora;
    private Long codUnidade;
    private Long codPneu;
    private BigDecimal valor;
    private Integer vidaMomentoRealizacaoServico;
    /**
     * Precisamos utilizar o {@link Exclude} para a serialização/desserialização das subclasses
     * funcionar corretamente utilizando o {@link Gson}.
     */
    @Exclude
    private String tipo;

    public ServicoRealizadoRecapadora() {
    }

    public static RuntimeTypeAdapterFactory<ServicoRealizadoRecapadora> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(ServicoRealizadoRecapadora.class, "tipo")
                .registerSubtype(ServicoRealizadoRecapagem.class,
                        ServicoRealizadoRecapagem.TIPO_SERVICO_REALIZADO_RECAPAGEM);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodTipoServicoRecapadora() {
        return codTipoServicoRecapadora;
    }

    public void setCodTipoServicoRecapadora(final Long codTipoServicoRecapadora) {
        this.codTipoServicoRecapadora = codTipoServicoRecapadora;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(final BigDecimal valor) {
        this.valor = valor;
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
        return "ServicoRealizadoRecapadora{" +
                "codigo=" + codigo +
                ", codTipoServicoRecapadora=" + codTipoServicoRecapadora +
                ", codUnidade=" + codUnidade +
                ", codPneu=" + codPneu +
                ", valor=" + valor +
                ", vidaMomentoRealizacaoServico=" + vidaMomentoRealizacaoServico +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
