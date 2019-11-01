package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.motivo.MotivoDescarte;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

/**
 * Created by Zart on 02/03/17.
 */
public final class DestinoDescarte extends Destino {

    @NotNull
    private MotivoDescarte motivoDescarte;
    @Nullable
    private String urlImagemDescarte1;
    @Nullable
    private String urlImagemDescarte2;
    @Nullable
    private String urlImagemDescarte3;

    public DestinoDescarte() {
        super(OrigemDestinoEnum.DESCARTE);
    }

    @NotNull
    public MotivoDescarte getMotivoDescarte() {
        return motivoDescarte;
    }

    public void setMotivoDescarte(@NotNull final MotivoDescarte motivoDescarte) {
        this.motivoDescarte = motivoDescarte;
    }

    @Nullable
    public String getUrlImagemDescarte1() {
        return urlImagemDescarte1;
    }

    public void setUrlImagemDescarte1(@Nullable String urlImagemDescarte1) {
        this.urlImagemDescarte1 = urlImagemDescarte1;
    }

    @Nullable
    public String getUrlImagemDescarte2() {
        return urlImagemDescarte2;
    }

    public void setUrlImagemDescarte2(@Nullable String urlImagemDescarte2) {
        this.urlImagemDescarte2 = urlImagemDescarte2;
    }

    @Nullable
    public String getUrlImagemDescarte3() {
        return urlImagemDescarte3;
    }

    public void setUrlImagemDescarte3(@Nullable String urlImagemDescarte3) {
        this.urlImagemDescarte3 = urlImagemDescarte3;
    }

    @Override
    public String toString() {
        return "DestinoDescarte{" +
                "motivoDescarte=" + motivoDescarte +
                ", urlImagemDescarte1='" + urlImagemDescarte1 + '\'' +
                ", urlImagemDescarte2='" + urlImagemDescarte2 + '\'' +
                ", urlImagemDescarte3='" + urlImagemDescarte3 + '\'' +
                '}';
    }
}
