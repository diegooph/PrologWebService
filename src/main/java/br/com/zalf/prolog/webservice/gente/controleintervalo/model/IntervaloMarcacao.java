package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class IntervaloMarcacao {
    @NotNull
    private Long codigo;
    @NotNull
    private Long codMarcacaoPorUnidade;
    @NotNull
    private Long codTipoIntervalo;
    @NotNull
    private Long codUnidade;
    @NotNull
    private Long cpfColaborador;
    @NotNull
    private LocalDateTime dataHoraMaracao;
    @NotNull
    private FonteDataHora fonteDataHora;
    @NotNull
    private TipoMarcacaoIntervalo tipoMarcacaoIntervalo;
    @Nullable
    private String justificativaEstouro;
    @Nullable
    private String justificativaTempoRecomendado;
    @Nullable
    private Localizacao localizacaoMarcacao;

    public IntervaloMarcacao() {

    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(@NotNull Long codigo) {
        this.codigo = codigo;
    }

    @NotNull
    public Long getCodTipoIntervalo() {
        return codTipoIntervalo;
    }

    public void setCodTipoIntervalo(@NotNull Long codTipoIntervalo) {
        this.codTipoIntervalo = codTipoIntervalo;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(@NotNull Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    @NotNull
    public Long getCodMarcacaoPorUnidade() {
        return codMarcacaoPorUnidade;
    }

    public void setCodMarcacaoPorUnidade(@NotNull Long codMarcacaoPorUnidade) {
        this.codMarcacaoPorUnidade = codMarcacaoPorUnidade;
    }

    @NotNull
    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(@NotNull Long cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    @NotNull
    public LocalDateTime getDataHoraMaracao() {
        return dataHoraMaracao;
    }

    public void setDataHoraMaracao(@NotNull LocalDateTime dataHoraMaracao) {
        this.dataHoraMaracao = dataHoraMaracao;
    }

    @NotNull
    public FonteDataHora getFonteDataHora() {
        return fonteDataHora;
    }

    public void setFonteDataHora(@NotNull FonteDataHora fonteDataHora) {
        this.fonteDataHora = fonteDataHora;
    }

    @NotNull
    public TipoMarcacaoIntervalo getTipoMarcacaoIntervalo() {
        return tipoMarcacaoIntervalo;
    }

    public void setTipoMarcacaoIntervalo(@NotNull TipoMarcacaoIntervalo tipoMarcacaoIntervalo) {
        this.tipoMarcacaoIntervalo = tipoMarcacaoIntervalo;
    }

    @Nullable
    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    public void setJustificativaEstouro(@Nullable String justificativaEstouro) {
        this.justificativaEstouro = justificativaEstouro;
    }

    @Nullable
    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }

    public void setJustificativaTempoRecomendado(@Nullable String justificativaTempoRecomendado) {
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
    }

    @Nullable
    public Localizacao getLocalizacaoMarcacao() {
        return localizacaoMarcacao;
    }

    public void setLocalizacaoMarcacao(@Nullable final Localizacao localizacaoMarcacao) {
        this.localizacaoMarcacao = localizacaoMarcacao;
    }

    @Override
    public String toString() {
        return "IntervaloMarcacao{" +
                "codigo=" + codigo +
                ", codMarcacaoPorUnidade=" + codMarcacaoPorUnidade +
                ", codTipoIntervalo=" + codTipoIntervalo +
                ", codUnidade=" + codUnidade +
                ", cpfColaborador=" + cpfColaborador +
                ", dataHoraMaracao=" + dataHoraMaracao +
                ", fonteDataHora=" + fonteDataHora +
                ", tipoMarcacaoIntervalo=" + tipoMarcacaoIntervalo +
                ", justificativaEstouro='" + justificativaEstouro + '\'' +
                ", justificativaTempoRecomendado='" + justificativaTempoRecomendado + '\'' +
                ", localizacaoMarcacao=" + localizacaoMarcacao +
                '}';
    }
}