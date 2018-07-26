package br.com.zalf.prolog.webservice.raizen.produtividade.model.itens;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItemColaborador extends RaizenProdutividadeItem {
    @NotNull
    private final Long cpf;
    @Nullable
    private final String nome;
    private final boolean colaboradorCadastrado;

    public RaizenProdutividadeItemColaborador(@NotNull final Long cpf, @Nullable final String nome) {
        super(RaizenProdutividadeItemTipo.ITEM_COLABORADOR);
        this.cpf = cpf;
        this.nome = nome;
        this.colaboradorCadastrado = nome != null;
    }

    @NotNull
    public Long getCpf() {
        return cpf;
    }

    @Nullable
    public String getNome() {
        return nome;
    }

    public boolean isColaboradorCadastrado() {
        return colaboradorCadastrado;
    }
}