package br.com.zalf.prolog.webservice.raizen.produtividade.model;

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

    public RaizenProdutividadeColaborador() {
        setTipoAgrupamento(RaizenProdutividadeAgrupamento.POR_COLABORADOR);
    }

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
//        calculaItensErrados();
    }

/*    private void calculaItensErrados() {
        Preconditions.checkNotNull(itensRaizen, "itensRaizen não pode ser null!");
        int qtdColaboradoresErrados = 0;
        int qtdPlacasErradas = 0;
        int qtdMapasErrados = 0;
        for (final RaizenProdutividadeItemColaborador item : itensRaizen) {
            if (!item.isCpfMotoristaOk() || !item.isCpfAjudante1Ok() || !item.isCpfAjudante2Ok()) {
                qtdColaboradoresErrados++;
            }
            if (!item.isPlacaOk()) {
                qtdPlacasErradas++;
            }

        }
        this.qtdColaboradoresErrados = qtdColaboradoresErrados;
        this.qtdPlacasErradas = qtdPlacasErradas;
    }    */
//    }
}
