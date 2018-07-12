package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeColaborador extends RaizenProdutividade {

    private LocalDate data;
    private List<RaizenProdutividadeItemColaborador> itensRaizen;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<RaizenProdutividadeItemColaborador> getItensRaizen() {
        return itensRaizen;
    }

    public void setItensRaizen(List<RaizenProdutividadeItemColaborador> itensRaizen) {
        this.itensRaizen = itensRaizen;
    }
}
