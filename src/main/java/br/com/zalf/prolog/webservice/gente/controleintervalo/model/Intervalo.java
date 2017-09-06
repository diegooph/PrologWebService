package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.Unidade;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 18/08/2017.
 */
public class Intervalo {
    private Long codigo;
    private TipoIntervalo tipo;
    private Date dataHoraInicio;
    private FonteDataHora fonteDataHoraInicio;
    private Date dataHoraFim;
    private FonteDataHora fonteDataHoraFim;
    private Colaborador colaborador;
    private List<EdicaoIntervalo> edicoes;
    private Duration tempoDecorrido;

    /**
     * Essa justificativa é obrigada a ser fornecida caso o {@link Colaborador} feche o intervalo
     * com um {@link #tempoDecorrido} <b>maior</b> que o {@link TipoIntervalo#tempoLimiteEstouro} do
     * {@link TipoIntervalo} ao qual ele é referente.
     */
    private String justificativaEstouro;

    /**
     * Essa justificativa é obrigada a ser fornecida caso o {@link Colaborador} feche o intervalo
     * com um {@link #tempoDecorrido} <b>menor</b> que o {@link TipoIntervalo#tempoRecomendado} do
     * {@link TipoIntervalo} ao qual ele é referente.
     */
    private String justificativaTempoRecomendado;

    /**
     * Quando um intervalo é criado, ele é por default válido (valido é {@code true}. Porém, alguém
     * do RH de uma {@link Unidade} pode invalidar esse intervalo. Ele não será deletado do BD mas
     * poderá ter uma visualização diferente quando exibido.
     */
    private boolean valido;

    public Intervalo() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public FonteDataHora getFonteDataHoraInicio() {
        return fonteDataHoraInicio;
    }

    public void setFonteDataHoraInicio(FonteDataHora fonteDataHoraInicio) {
        this.fonteDataHoraInicio = fonteDataHoraInicio;
    }

    public FonteDataHora getFonteDataHoraFim() {
        return fonteDataHoraFim;
    }

    public void setFonteDataHoraFim(FonteDataHora fonteDataHoraFim) {
        this.fonteDataHoraFim = fonteDataHoraFim;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public TipoIntervalo getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntervalo tipo) {
        this.tipo = tipo;
    }

    public Date getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(Date dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public Date getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Date dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public List<EdicaoIntervalo> getEdicoes() {
        return edicoes;
    }

    public void setEdicoes(List<EdicaoIntervalo> edicoes) {
        this.edicoes = edicoes;
    }

    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    public void setJustificativaEstouro(String justificativaEstouro) {
        this.justificativaEstouro = justificativaEstouro;
    }

    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }

    public void setJustificativaTempoRecomendado(String justificativaTempoRecomendado) {
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
    }

    public Duration getTempoDecorrido() {
        return tempoDecorrido;
    }

    public void setTempoDecorrido(Duration tempoDecorrido) {
        this.tempoDecorrido = tempoDecorrido;
    }
}