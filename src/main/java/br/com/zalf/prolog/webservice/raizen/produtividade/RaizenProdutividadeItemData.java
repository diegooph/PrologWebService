package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.time.LocalDate;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItemData extends RaizenProdutividadeItem {

    private LocalDate data;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
