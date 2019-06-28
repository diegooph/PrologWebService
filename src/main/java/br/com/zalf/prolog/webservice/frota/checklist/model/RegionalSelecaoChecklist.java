package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-06-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class RegionalSelecaoChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<UnidadeSelecaoChecklist> unidadesVinculadas;

    public RegionalSelecaoChecklist(@NotNull final Long codigo,
                                    @NotNull final String nome,
                                    @NotNull final List<UnidadeSelecaoChecklist> unidadesVinculadas) {
        this.codigo = codigo;
        this.nome = nome;
        this.unidadesVinculadas = unidadesVinculadas;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<UnidadeSelecaoChecklist> getUnidadesVinculadas() {
        return unidadesVinculadas;
    }
}
