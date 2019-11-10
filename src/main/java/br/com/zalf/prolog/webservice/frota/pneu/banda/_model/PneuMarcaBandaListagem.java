package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Created on 25/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaBandaListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @Nullable
    private List<PneuModeloBandaListagem> modelos;

    public PneuMarcaBandaListagem(@NotNull final Long codigo,
                                  @NotNull final String nome,
                                  @Nullable final List<PneuModeloBandaListagem> modelos) {
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
    public List<PneuModeloBandaListagem> getModelos() {
        return modelos;
    }

    public void setModelos(@Nullable final List<PneuModeloBandaListagem> modelos) {
        this.modelos = modelos;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PneuMarcaBandaListagem that = (PneuMarcaBandaListagem) o;
        return codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
