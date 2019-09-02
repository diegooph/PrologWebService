package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 30/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuNomenclaturaItemVisualizacao {

    private String nomenclatura;
    private int posicaoProlog;

    public PneuNomenclaturaItemVisualizacao() {

    }

    public PneuNomenclaturaItemVisualizacao(String nomenclatura, int posicaoProlog) {
        this.nomenclatura = nomenclatura;
        this.posicaoProlog = posicaoProlog;
    }

    public String getNomenclatura() {
        return nomenclatura;
    }

    public void setNomenclatura(final String nomenclatura) {
        this.nomenclatura = nomenclatura;
    }

    public int getPosicaoProlog() {
        return posicaoProlog;
    }

    public void setPosicaoProlog(int posicaoProlog) {
        this.posicaoProlog = posicaoProlog;
    }

    @NotNull
    public static PneuNomenclaturaItemVisualizacao createDummy(int i) {
        return new PneuNomenclaturaItemVisualizacao("POSICAO"+i, i+11);
    }
}
