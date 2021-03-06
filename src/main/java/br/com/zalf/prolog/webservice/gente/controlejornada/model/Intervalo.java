package br.com.zalf.prolog.webservice.gente.controlejornada.model;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Zart on 18/08/2017.
 */
public class Intervalo {
    private Long codigo;
    private TipoMarcacao tipo;
    private LocalDateTime dataHoraInicio;
    private FonteDataHora fonteDataHoraInicio;
    private LocalDateTime dataHoraFim;
    private FonteDataHora fonteDataHoraFim;
    private Colaborador colaborador;
    private List<EdicaoIntervalo> edicoes;
    private Duration tempoDecorrido;
    private Localizacao localizacaoInicio;
    private Localizacao localizacaoFim;

    /**
     * Essa justificativa é obrigada a ser fornecida caso o {@link Colaborador} feche o intervalo
     * com um {@link #tempoDecorrido} <b>maior</b> que o {@link TipoMarcacao#tempoLimiteEstouro} do
     * {@link TipoMarcacao} ao qual ele é referente.
     */
    private String justificativaEstouro;

    /**
     * Essa justificativa é obrigada a ser fornecida caso o {@link Colaborador} feche o intervalo
     * com um {@link #tempoDecorrido} <b>menor</b> que o {@link TipoMarcacao#tempoRecomendado} do
     * {@link TipoMarcacao} ao qual ele é referente.
     */
    private String justificativaTempoRecomendado;

    /**
     * Quando um intervalo é criado, ele é por default válido (valido é {@code true}. Porém, alguém
     * do RH de uma {@link Unidade} pode invalidar esse intervalo. Ele não será deletado do BD mas
     * poderá ter uma visualização diferente quando exibido.
     */
    private Boolean valido;

    public Intervalo() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public FonteDataHora getFonteDataHoraInicio() {
        return fonteDataHoraInicio;
    }

    public void setFonteDataHoraInicio(final FonteDataHora fonteDataHoraInicio) {
        this.fonteDataHoraInicio = fonteDataHoraInicio;
    }

    public FonteDataHora getFonteDataHoraFim() {
        return fonteDataHoraFim;
    }

    public void setFonteDataHoraFim(final FonteDataHora fonteDataHoraFim) {
        this.fonteDataHoraFim = fonteDataHoraFim;
    }

    public TipoMarcacao getTipo() {
        return tipo;
    }

    public void setTipo(final TipoMarcacao tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(final LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(final LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public Localizacao getLocalizacaoInicio() {
        return localizacaoInicio;
    }

    public void setLocalizacaoInicio(final Localizacao localizacaoInicio) {
        this.localizacaoInicio = localizacaoInicio;
    }

    public Localizacao getLocalizacaoFim() {
        return localizacaoFim;
    }

    public void setLocalizacaoFim(final Localizacao localizacaoFim) {
        this.localizacaoFim = localizacaoFim;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(final Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Boolean isValido() {
        return valido;
    }

    public void setValido(final Boolean valido) {
        this.valido = valido;
    }

    public List<EdicaoIntervalo> getEdicoes() {
        return edicoes;
    }

    public void setEdicoes(final List<EdicaoIntervalo> edicoes) {
        this.edicoes = edicoes;
    }

    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    public void setJustificativaEstouro(final String justificativaEstouro) {
        this.justificativaEstouro = justificativaEstouro;
    }

    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }

    public void setJustificativaTempoRecomendado(final String justificativaTempoRecomendado) {
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
    }

    public Duration getTempoDecorrido() {
        return tempoDecorrido;
    }

    public void setTempoDecorrido(final Duration tempoDecorrido) {
        this.tempoDecorrido = tempoDecorrido;
    }

    public boolean temInicioEFim() {
        return dataHoraInicio != null && dataHoraFim != null;
    }
}