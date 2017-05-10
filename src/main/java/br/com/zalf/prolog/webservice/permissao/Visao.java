package br.com.zalf.prolog.webservice.permissao;

import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;

import java.util.List;


/**
 * Created by luiz on 4/18/16.
 */
public class Visao {
    List<Pilar> pilares;

    public List<Pilar> getPilares() {
        return pilares;
    }

    public void setPilares(List<Pilar> pilares) {
        this.pilares = pilares;
    }

    @Override
    public String toString() {
        return "Visao{" +
                "pilares=" + pilares +
                '}';
    }
}
