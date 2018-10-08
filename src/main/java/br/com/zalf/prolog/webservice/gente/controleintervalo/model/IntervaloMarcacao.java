package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date dataNascimentoColaborador;
    @NotNull
    private LocalDateTime dataHoraMaracao;
    @NotNull
    private FonteDataHora fonteDataHora;
    @NotNull
    private TipoInicioFim tipoMarcacaoIntervalo;
    @Nullable
    private String justificativaEstouro;
    @Nullable
    private String justificativaTempoRecomendado;
    @Nullable
    private Localizacao localizacaoMarcacao;
    @Nullable
    private Long codMarcacaoVinculada;

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
    public Date getDataNascimentoColaborador() {
        return dataNascimentoColaborador;
    }

    public void setDataNascimentoColaborador(@NotNull final Date dataNascimentoColaborador) {
        this.dataNascimentoColaborador = dataNascimentoColaborador;
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
    public TipoInicioFim getTipoMarcacaoIntervalo() {
        return tipoMarcacaoIntervalo;
    }

    public void setTipoMarcacaoIntervalo(@NotNull TipoInicioFim tipoMarcacaoIntervalo) {
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

    @Nullable
    public Long getCodMarcacaoVinculada() {
        return codMarcacaoVinculada;
    }

    public void setCodMarcacaoVinculada(@Nullable final Long codMarcacaoVinculada) {
        this.codMarcacaoVinculada = codMarcacaoVinculada;
    }

    @Override
    public String toString() {
        return "IntervaloMarcacao{" +
                "codigo=" + codigo +
                ", codMarcacaoPorUnidade=" + codMarcacaoPorUnidade +
                ", codTipoIntervalo=" + codTipoIntervalo +
                ", codUnidade=" + codUnidade +
                ", cpfColaborador=" + cpfColaborador +
                ", dataNascimentoColaborador=" + dataNascimentoColaborador +
                ", dataHoraMaracao=" + dataHoraMaracao +
                ", fonteDataHora=" + fonteDataHora +
                ", tipoMarcacaoIntervalo=" + tipoMarcacaoIntervalo +
                ", justificativaEstouro='" + justificativaEstouro + '\'' +
                ", justificativaTempoRecomendado='" + justificativaTempoRecomendado + '\'' +
                ", localizacaoMarcacao=" + localizacaoMarcacao +
                ", codMarcacaoVinculada=" + codMarcacaoVinculada +
                '}';
    }
}