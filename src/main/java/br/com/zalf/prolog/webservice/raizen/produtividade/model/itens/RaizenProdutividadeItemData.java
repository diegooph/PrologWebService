package br.com.zalf.prolog.webservice.raizen.produtividade.model.itens;

import java.time.LocalDate;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItemData extends RaizenProdutividadeItem {
    private LocalDate dataViagem;

    public RaizenProdutividadeItemData() {
        super(RaizenProdutividadeItemTipo.ITEM_DATA);
    }

    public LocalDate getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(LocalDate dataViagem) {
        this.dataViagem = dataViagem;
    }
}
