package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class Raizen {

    private List<RaizenItem> raizenItens;

    public Raizen() {
    }

    public List<RaizenItem> getItensRaizen() {
        return raizenItens;
    }

    public void setRaizenItens(final List<RaizenItem> raizenItens) {
        this.raizenItens = raizenItens;
    }

    @Override
    public String toString() {
        return "Raizen{" +
                "raizenItens=" + raizenItens +
                "}";
    }
}