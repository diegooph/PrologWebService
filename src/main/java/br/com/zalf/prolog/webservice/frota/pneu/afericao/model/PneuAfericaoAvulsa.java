package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuTipo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import com.google.common.base.Preconditions;

import java.util.Date;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuAfericaoAvulsa extends Pneu {
    private Date dataHoraUltimaAferica;
    private String nomeColaboradorAfericao;
    private TipoMedicaoColetadaAfericao tipoUltimaAfericao;
    private Long codigoUltimaAferica;
    private TipoProcessoColetaAfericao tipoProcessoAfericao;
    private String placaAplicadoQuandoAferido;

    public PneuAfericaoAvulsa() {
        super(PneuTipo.PNEU_AFERICAO_AVULSA);
    }

    public Date getDataHoraUltimaAferica() {
        return dataHoraUltimaAferica;
    }

    public void setDataHoraUltimaAferica(final Date dataHoraUltimaAferica) {
        this.dataHoraUltimaAferica = dataHoraUltimaAferica;
    }

    public TipoMedicaoColetadaAfericao getTipoUltimaAfericao() {
        return tipoUltimaAfericao;
    }

    public void setTipoUltimaAfericao(final TipoMedicaoColetadaAfericao tipoUltimaAfericao) {
        this.tipoUltimaAfericao = tipoUltimaAfericao;
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
                && tipoUltimaAfericao != null
                && codigoUltimaAferica != null
                && tipoProcessoAfericao != null;
    }

    @Override
    public void setStatus(final StatusPneu status) {
        Preconditions.checkArgument(
                status == StatusPneu.ESTOQUE,
                "Por enquanto só podemos aferir avulsamente pneus em estoque");
        super.setStatus(status);
    }
}