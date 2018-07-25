package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItem;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemData;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeColaborador extends RaizenProdutividade {
    @NotNull
    private final String cpf;
    @Nullable
    private final String nome;
    private final boolean colaboradorCadastrado;

    public RaizenProdutividadeColaborador(@NotNull final String cpf,
                                          @Nullable final String nome) {
        super(RaizenProdutividadeAgrupamento.POR_COLABORADOR);
        this.cpf = cpf;
        this.nome = nome;
        this.colaboradorCadastrado = nome != null;
    }

    @NotNull
    public String getCpf() {
        return cpf;
    }

    @Nullable
    public String getNome() {
        return nome;
    }

    public boolean isColaboradorCadastrado() {
        return colaboradorCadastrado;
    }

    public List<RaizenProdutividadeItemData> getItensRaizen() {
        //noinspection unchecked
        return (List<RaizenProdutividadeItemData>) itensRaizen;
    }

    @Override
    protected void calculaItensNaoCadastrados() {
        Preconditions.checkState(getItensRaizen() != null, "itensRaizen não pode ser null!");
        for (final RaizenProdutividadeItem item : getItensRaizen()) {
            if (!item.isPlacaCadastrada()) {
                setTotalPlacaNaoCadastradas(getTotalPlacaNaoCadastradas() + 1);
            }
        }
    }
}