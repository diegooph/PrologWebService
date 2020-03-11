package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import com.google.gson.Gson;

import java.time.LocalDateTime;

/**
 * Created by jean on 04/04/16.
 */
public abstract class Servico {
    private Long codigo;

    /**
     * O código da {@link AfericaoPlaca} que originou esse serviço.
     */
    private Long codAfericao;
    /**
     * O código da {@link Unidade} onde o serviço foi aberto.
     */
    private Long codUnidade;
    private LocalDateTime dataHoraAbertura;
    private LocalDateTime dataHoraFechamento;
    private PneuComum pneuComProblema;
    private Colaborador colaboradorResponsavelFechamento;
    private int qtdApontamentos;
    private long kmVeiculoMomentoFechamento;
    private String placaVeiculo;
    private Double pressaoColetadaFechamento;

    /**
     * Indica se esse serviço foi fechado automaticamente por um {@link ProcessoMovimentacao}.
     */
    private boolean fechadoAutomaticamenteMovimentacao;

    /**
     * Indica se esse serviço foi fechado automaticamente por um método de integração.
     */
    private boolean fechadoAutomaticamenteIntegracao;

    /**
     * Armazena o tempo que o colaborador levou para realizar esse serviço, em milissegundos.
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

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public LocalDateTime getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(LocalDateTime dataHoraAbertura) {
        this.dataHoraAbertura = dataHoraAbertura;
    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }

    public PneuComum getPneuComProblema() {
        return pneuComProblema;
    }

    public void setPneuComProblema(PneuComum pneuComProblema) {
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

    public boolean isFechadoAutomaticamenteMovimentacao() {
        return fechadoAutomaticamenteMovimentacao;
    }

    public void setFechadoAutomaticamenteMovimentacao(boolean fechadoAutomaticamenteMovimentacao) {
        this.fechadoAutomaticamenteMovimentacao = fechadoAutomaticamenteMovimentacao;
    }

    public boolean isFechadoAutomaticamenteIntegracao() {
        return fechadoAutomaticamenteIntegracao;
    }

    public void setFechadoAutomaticamenteIntegracao(boolean fechadoAutomaticamenteIntegracao) {
        this.fechadoAutomaticamenteIntegracao = fechadoAutomaticamenteIntegracao;
    }
}