package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import java.util.List;

/**
 * Created on 12/6/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ServicosAbertosHolder {
    private List<QuantidadeServicos> servicosAbertos;

    public List<QuantidadeServicos> getServicosAbertos() {
        return servicosAbertos;
    }

    public void setServicosAbertos(List<QuantidadeServicos> servicosAbertos) {
        this.servicosAbertos = servicosAbertos;
    }
}
