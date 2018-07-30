package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuAfericaoAvulsa {
    private Pneu pneu;
    private LocalDateTime dataHoraUltimaAferica;
    private String nomeColaboradorAfericao;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaUltimaAfericao;
    private Long codigoUltimaAferica;
    private TipoProcessoColetaAfericao tipoProcessoAfericao;
    private String placaAplicadoQuandoAferido;

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

    public LocalDateTime getDataHoraUltimaAferica() {
        return dataHoraUltimaAferica;
    }

    public void setDataHoraUltimaAferica(final LocalDateTime dataHoraUltimaAferica) {
        this.dataHoraUltimaAferica = dataHoraUltimaAferica;
    }

    public TipoMedicaoColetadaAfericao getTipoMedicaoColetadaUltimaAfericao() {
        return tipoMedicaoColetadaUltimaAfericao;
    }

    public void setTipoMedicaoColetadaUltimaAfericao(final TipoMedicaoColetadaAfericao tipoMedicaoColetadaUltimaAfericao) {
        this.tipoMedicaoColetadaUltimaAfericao = tipoMedicaoColetadaUltimaAfericao;
    }

    public Long getCodigoUltimaAferica() {
        return codigoUltimaAferica;
    }

    public void setCodigoUltimaAferica(final Long codigoUltimaAferica) {
        this.codigoUltimaAferica = codigoUltimaAferica;
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

    public boolean isJaFoiAferido() {
        return dataHoraUltimaAferica != null
                && nomeColaboradorAfericao != null
                && tipoMedicaoColetadaUltimaAfericao != null
                && codigoUltimaAferica != null
                && tipoProcessoAfericao != null;
    }
}