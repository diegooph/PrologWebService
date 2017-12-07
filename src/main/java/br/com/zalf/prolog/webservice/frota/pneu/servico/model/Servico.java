package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by jean on 04/04/16.
 */
public abstract class Servico {
    private Long codigo;

    /**
     * O código da {@link Afericao} que originou esse serviço.
     */
    private Long codAfericao;
    private Date dataHoraAbertura;
    private Date dataHoraFechamento;
    private Pneu pneuComProblema;
    private Colaborador colaboradorResponsavelFechamento;
    private int qtdApontamentos;
    private long kmVeiculoMomentoFechamento;
    private String placaVeiculo;
    private Double pressaoColetadaFechamento;

    /**
     * Armazena o tempo que o colaborador levou para realizar esse serviço, em milisegundos.
     */
    private long tempoRealizacaoServicoInMillis;

    /**
     * O tipo desse serviço. Precisamos utilizar o {@link Exclude} para a serialização/desserialização das subclasses
     * funcionar corretamente utilizando o {@link Gson}.
     */
    @Exclude
    private TipoServico tipoServico;

    public Servico() {

    }

    public static RuntimeTypeAdapterFactory<Servico> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(Servico.class, "tipoServico")
                .registerSubtype(ServicoCalibragem.class, TipoServico.CALIBRAGEM.asString())
                .registerSubtype(ServicoMovimentacao.class, TipoServico.MOVIMENTACAO.asString())
                .registerSubtype(ServicoInspecao.class, TipoServico.INSPECAO.asString());
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodAfericao() {
        return codAfericao;
    }

    public void setCodAfericao(Long codAfericao) {
        this.codAfericao = codAfericao;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public Date getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(Date dataHoraAbertura) {
        this.dataHoraAbertura = dataHoraAbertura;
    }

    public Date getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(Date dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }

    public Pneu getPneuComProblema() {
        return pneuComProblema;
    }

    public void setPneuComProblema(Pneu pneuComProblema) {
        this.pneuComProblema = pneuComProblema;
    }

    public Colaborador getColaboradorResponsavelFechamento() {
        return colaboradorResponsavelFechamento;
    }

    public void setColaboradorResponsavelFechamento(Colaborador colaboradorResponsavelFechamento) {
        this.colaboradorResponsavelFechamento = colaboradorResponsavelFechamento;
    }

    public int getQtdApontamentos() {
        return qtdApontamentos;
    }

    public void setQtdApontamentos(int qtdApontamentos) {
        this.qtdApontamentos = qtdApontamentos;
    }

    public long getKmVeiculoMomentoFechamento() {
        return kmVeiculoMomentoFechamento;
    }

    public void setKmVeiculoMomentoFechamento(long kmVeiculoMomentoFechamento) {
        this.kmVeiculoMomentoFechamento = kmVeiculoMomentoFechamento;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public long getTempoRealizacaoServicoInMillis() {
        return tempoRealizacaoServicoInMillis;
    }

    public void setTempoRealizacaoServicoInMillis(long tempoRealizacaoServicoInMillis) {
        this.tempoRealizacaoServicoInMillis = tempoRealizacaoServicoInMillis;
    }

    public Long getCpfResponsavelFechamento() {
        return colaboradorResponsavelFechamento.getCpf();
    }

    public Double getPressaoColetadaFechamento() {
        return pressaoColetadaFechamento;
    }

    public void setPressaoColetadaFechamento(Double pressaoColetadaFechamento) {
        this.pressaoColetadaFechamento = pressaoColetadaFechamento;
    }
}