package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AnaliseItemModeloChecklist {
    @NotNull
    private final Long codigoItem;
    private final boolean itemNovo;
    private final boolean itemMudouContexto;

    public AnaliseItemModeloChecklist(@NotNull final Long codigoItem,
                                      final boolean itemNovo,
                                      final boolean itemMudouContexto) {
        this.codigoItem = codigoItem;
        this.itemNovo = itemNovo;
        this.itemMudouContexto = itemMudouContexto;
    }

    @NotNull
    public Long getCodigoItem() {
        return codigoItem;
    }

    public boolean isItemNovo() {
        return itemNovo;
    }

    public boolean isItemMudouContexto() {
        return itemMudouContexto;
    }
}