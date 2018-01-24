package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 24/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MotivoDescarte extends Motivo {

    @Nullable
    private String urlImagemDescarte1;
    @Nullable
    private String urlImagemDescarte2;
    @Nullable
    private String urlImagemDescarte3;

    public MotivoDescarte(@NotNull Long codigo,
                          @NotNull String motivo,
                          boolean ativo,
                          @Nullable String urlImagemDescarte1,
                          @Nullable String urlImagemDescarte2,
                          @Nullable String urlImagemDescarte3) {
        super(codigo, motivo, ativo);
        this.urlImagemDescarte1 = urlImagemDescarte1;
        this.urlImagemDescarte2 = urlImagemDescarte2;
        this.urlImagemDescarte3 = urlImagemDescarte3;
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
        return "MotivoDescarte{" +
                "urlImagemDescarte1='" + urlImagemDescarte1 + '\'' +
                ", urlImagemDescarte2='" + urlImagemDescarte2 + '\'' +
                ", urlImagemDescarte3='" + urlImagemDescarte3 + '\'' +
                '}';
    }
}
