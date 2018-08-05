package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItem;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemColaborador;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeData extends RaizenProdutividade {
    @NotNull
    private final LocalDate data;
    private int totalColaboradoresNaoCadastrados;

    public RaizenProdutividadeData(@NotNull final LocalDate data) {
        super(RaizenProdutividadeAgrupamento.POR_DATA);
        this.data = data;
    }

    @NotNull
    public LocalDate getData() {
        return data;
    }

    public List<RaizenProdutividadeItemColaborador> getItensRaizen() {
        //noinspection unchecked
        return (List<RaizenProdutividadeItemColaborador>) itensRaizen;
    }

    @Override
    public void calculaItensNaoCadastrados() {
        Preconditions.checkState(getItensRaizen() != null, "itensRaizen não pode ser null!");
        for (final RaizenProdutividadeItem item : getItensRaizen()) {
            final RaizenProdutividadeItemColaborador colaborador = (RaizenProdutividadeItemColaborador) item;
            if (!colaborador.isColaboradorCadastrado()) {
                totalColaboradoresNaoCadastrados++;
            }
            if (!item.isPlacaCadastrada()) {
                setTotalPlacaNaoCadastradas(getTotalPlacaNaoCadastradas() + 1);
            }
        }
    }

    public int getTotalColaboradoresNaoCadastrados() {
        return totalColaboradoresNaoCadastrados;
    }
}