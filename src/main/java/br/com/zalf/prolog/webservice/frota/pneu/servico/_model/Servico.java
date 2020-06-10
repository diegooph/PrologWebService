package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Irá existir quando o fechamento for feito por um colaborador, não de forma automática pelo
     * Prolog.
     * <p>
     * Será setado apenas na busca de um serviço específico para visualização.
     */
    @Nullable
    private FormaColetaDadosAfericaoEnum formaColetaDadosFechamento;

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

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodAfericao() {
        return codAfericao;
    }

    public void setCodAfericao(final Long codAfericao) {
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

    public void setTipoServico(final TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public LocalDateTime getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(final LocalDateTime dataHoraAbertura) {
        this.dataHoraAbertura = dataHoraAbertura;
    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(final LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }

    public PneuComum getPneuComProblema() {
        return pneuComProblema;
    }

    public void setPneuComProblema(final PneuComum pneuComProblema) {
        this.pneuComProblema = pneuComProblema;
    }

    public Colaborador getColaboradorResponsavelFechamento() {
        return colaboradorResponsavelFechamento;
    }

    public void setColaboradorResponsavelFechamento(final Colaborador colaboradorResponsavelFechamento) {
        this.colaboradorResponsavelFechamento = colaboradorResponsavelFechamento;
    }

    public int getQtdApontamentos() {
        return qtdApontamentos;
    }

    public void setQtdApontamentos(final int qtdApontamentos) {
        this.qtdApontamentos = qtdApontamentos;
    }

    public long getKmVeiculoMomentoFechamento() {
        return kmVeiculoMomentoFechamento;
    }

    public void setKmVeiculoMomentoFechamento(final long kmVeiculoMomentoFechamento) {
        this.kmVeiculoMomentoFechamento = kmVeiculoMomentoFechamento;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public long getTempoRealizacaoServicoInMillis() {
        return tempoRealizacaoServicoInMillis;
    }

    public void setTempoRealizacaoServicoInMillis(final long tempoRealizacaoServicoInMillis) {
        this.tempoRealizacaoServicoInMillis = tempoRealizacaoServicoInMillis;
    }

    public Long getCpfResponsavelFechamento() {
        return colaboradorResponsavelFechamento.getCpf();
    }

    public Double getPressaoColetadaFechamento() {
        return pressaoColetadaFechamento;
    }

    public void setPressaoColetadaFechamento(final Double pressaoColetadaFechamento) {
        this.pressaoColetadaFechamento = pressaoColetadaFechamento;
    }

    public boolean isFechadoAutomaticamenteMovimentacao() {
        return fechadoAutomaticamenteMovimentacao;
    }

    public void setFechadoAutomaticamenteMovimentacao(final boolean fechadoAutomaticamenteMovimentacao) {
        this.fechadoAutomaticamenteMovimentacao = fechadoAutomaticamenteMovimentacao;
    }

    public boolean isFechadoAutomaticamenteIntegracao() {
        return fechadoAutomaticamenteIntegracao;
    }

    public void setFechadoAutomaticamenteIntegracao(final boolean fechadoAutomaticamenteIntegracao) {
        this.fechadoAutomaticamenteIntegracao = fechadoAutomaticamenteIntegracao;
    }

    @Nullable
    public FormaColetaDadosAfericaoEnum getFormaColetaDadosFechamento() {
        return formaColetaDadosFechamento;
    }

    public void setFormaColetaDadosFechamento(@NotNull final FormaColetaDadosAfericaoEnum formaColetaDadosFechamento) {
        this.formaColetaDadosFechamento = formaColetaDadosFechamento;
    }

    /**
     * Dessa forma conseguimos deixar apps antigos funcionando.
     */
    @Nullable
    public String getFormaColetaDadosFechamentoAsStringOrEquipamentoIfNull() {
        return formaColetaDadosFechamento != null
                ? formaColetaDadosFechamento.toString()
                : FormaColetaDadosAfericaoEnum.EQUIPAMENTO.toString();
    }
}