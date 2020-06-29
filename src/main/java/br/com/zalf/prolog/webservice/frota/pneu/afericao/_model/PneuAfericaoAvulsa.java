package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuAfericaoAvulsa {
    private Pneu pneu;
    private LocalDateTime dataHoraUltimaAfericao;
    private String nomeColaboradorAfericao;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaUltimaAfericao;
    private Long codigoUltimaAfericao;
    private TipoProcessoColetaAfericao tipoProcessoAfericao;
    private String placaAplicadoQuandoAferido;
    private String identificadorFrotaAplicadoQuandoAferido;

    public PneuAfericaoAvulsa() {

    }

    public Pneu getPneu() {
        return pneu;
    }

    public void setPneu(final Pneu pneu) {
        Preconditions.checkArgument(
                pneu.getStatus() == StatusPneu.ESTOQUE,
                "Por enquanto só podemos aferir avulsamente pneus em estoque");
        this.pneu = pneu;
    }

    public LocalDateTime getDataHoraUltimaAfericao() {
        return dataHoraUltimaAfericao;
    }

    public void setDataHoraUltimaAfericao(final LocalDateTime dataHoraUltimaAfericao) {
        this.dataHoraUltimaAfericao = dataHoraUltimaAfericao;
    }

    public TipoMedicaoColetadaAfericao getTipoMedicaoColetadaUltimaAfericao() {
        return tipoMedicaoColetadaUltimaAfericao;
    }

    public void setTipoMedicaoColetadaUltimaAfericao(final TipoMedicaoColetadaAfericao tipoMedicaoColetadaUltimaAfericao) {
        this.tipoMedicaoColetadaUltimaAfericao = tipoMedicaoColetadaUltimaAfericao;
    }

    public Long getCodigoUltimaAfericao() {
        return codigoUltimaAfericao;
    }

    public void setCodigoUltimaAfericao(final Long codigoUltimaAfericao) {
        this.codigoUltimaAfericao = codigoUltimaAfericao;
    }

    public String getNomeColaboradorAfericao() {
        return nomeColaboradorAfericao;
    }

    public void setNomeColaboradorAfericao(final String nomeColaboradorAfericao) {
        this.nomeColaboradorAfericao = nomeColaboradorAfericao;
    }

    public TipoProcessoColetaAfericao getTipoProcessoAfericao() {
        return tipoProcessoAfericao;
    }

    public void setTipoProcessoAfericao(final TipoProcessoColetaAfericao tipoProcessoAfericao) {
        this.tipoProcessoAfericao = tipoProcessoAfericao;
    }

    public String getPlacaAplicadoQuandoAferido() {
        return placaAplicadoQuandoAferido;
    }

    public void setPlacaAplicadoQuandoAferido(final String placaAplicadoQuandoAferido) {
        this.placaAplicadoQuandoAferido = placaAplicadoQuandoAferido;
    }

    public String getIdentificadorFrotaAplicadoQuandoAferido() {
        return identificadorFrotaAplicadoQuandoAferido;
    }

    public void setIdentificadorFrotaAplicadoQuandoAferido(String identificadorFrotaAplicadoQuandoAferido) {
        this.identificadorFrotaAplicadoQuandoAferido = identificadorFrotaAplicadoQuandoAferido;
    }

    public boolean isJaFoiAferido() {
        return dataHoraUltimaAfericao != null
                && nomeColaboradorAfericao != null
                && tipoMedicaoColetadaUltimaAfericao != null
                && codigoUltimaAfericao != null
                && tipoProcessoAfericao != null;
    }
}