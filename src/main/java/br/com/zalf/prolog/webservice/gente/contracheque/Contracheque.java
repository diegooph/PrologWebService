package br.com.zalf.prolog.webservice.gente.contracheque;

import java.util.List;

/**
 * Created by Zalf on 24/11/16.
 */
public class Contracheque {

    private List<ItemContracheque> itens;

    public Contracheque() {
    }

    public Contracheque(List<ItemContracheque> itens) {
        this.itens = itens;
    }

    public List<ItemContracheque> getItens() {
        return itens;
    }

    public void setItens(List<ItemContracheque> itens) {
        this.itens = itens;
    }

    @Override
    public String toString() {
        return "Contracheque{" +
                "itens=" + itens +
                '}';
    }
}
