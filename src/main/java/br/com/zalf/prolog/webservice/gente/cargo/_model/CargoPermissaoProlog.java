package br.com.zalf.prolog.webservice.gente.cargo._model;

import br.com.zalf.prolog.webservice.permissao.pilares.ImpactoPermissaoProLog;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Zalf on 24/10/16.
 */
@Data
public final class CargoPermissaoProlog {
    private final int codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final ImpactoPermissaoProLog impacto;
    @NotNull
    private final String descricao;
    private final boolean permissaoAssociada;
    private final boolean permissaoBloqueada;
    @Nullable
    private final CargoPermissaoInfosBloqueio infosBloqueio;
}
