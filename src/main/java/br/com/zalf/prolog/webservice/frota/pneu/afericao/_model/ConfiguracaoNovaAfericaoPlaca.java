package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

/**
 * Created on 06/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoNovaAfericaoPlaca extends ConfiguracaoNovaAfericao {
    private boolean podeAferirSulco;
    private boolean podeAferirPressao;
    private boolean podeAferirSulcoPressao;
    private boolean podeAferirEstepe;

    public ConfiguracaoNovaAfericaoPlaca() {

    }

    public boolean isPodeAferirSulco() {
        return podeAferirSulco;
    }

    public void setPodeAferirSulco(final boolean podeAferirSulco) {
        this.podeAferirSulco = podeAferirSulco;
    }

    public boolean isPodeAferirPressao() {
        return podeAferirPressao;
    }

    public void setPodeAferirPressao(final boolean podeAferirPressao) {
        this.podeAferirPressao = podeAferirPressao;
    }

    public boolean isPodeAferirSulcoPressao() {
        return podeAferirSulcoPressao;
    }

    public void setPodeAferirSulcoPressao(final boolean podeAferirSulcoPressao) {
        this.podeAferirSulcoPressao = podeAferirSulcoPressao;
    }

    public boolean isPodeAferirEstepe() {
        return podeAferirEstepe;
    }

    public void setPodeAferirEstepe(final boolean podeAferirEstepe) {
        this.podeAferirEstepe = podeAferirEstepe;
    }
}