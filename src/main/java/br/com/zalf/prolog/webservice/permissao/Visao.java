package br.com.zalf.prolog.webservice.permissao;

import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
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

    public boolean hasAccessToFunction(int codPilar, int codPermissao) {
        if (pilares == null)
            return false;

        for (Pilar pilar : pilares) {
            if (pilar.codigo == codPilar) {
                if (pilar.funcoes == null)
                    return false;

                for (FuncaoProLog funcao : pilar.funcoes) {
                    if (funcao != null && funcao.getCodigo() == codPermissao)
                        return true;
                }

                return false;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Visao{" +
                "pilares=" + pilares +
                '}';
    }
}
