package br.com.zalf.prolog.webservice.frota.pneu.modelo._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Created on 2019-11-02
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuMarcaListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @Nullable
    private List<PneuModeloListagem> modelos;

    public PneuMarcaListagem(@NotNull final Long codigo,
                             @NotNull final String nome,
                             @Nullable final List<PneuModeloListagem> modelos) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelos = modelos;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @Nullable
    public List<PneuModeloListagem> getModelos() {
        return modelos;
    }

    public void setModelos(@Nullable final List<PneuModeloListagem> modelos) {
        this.modelos = modelos;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PneuMarcaListagem that = (PneuMarcaListagem) o;
        return codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
