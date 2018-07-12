package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;

import java.util.List;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeData extends RaizenProdutividade {

    private Colaborador colaborador;
    private List<RaizenProdutividadeItemData> itensRaizen;

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public List<RaizenProdutividadeItemData> getItensRaizen() {
        return itensRaizen;
    }

    public void setItensRaizen(List<RaizenProdutividadeItemData> itensRaizen) {
        this.itensRaizen = itensRaizen;
    }
}
