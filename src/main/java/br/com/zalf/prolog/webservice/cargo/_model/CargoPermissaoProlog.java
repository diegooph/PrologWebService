package br.com.zalf.prolog.webservice.cargo._model;

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
    /**
     * Atributo que indica se a permissão está associada ao cargo.
     */
    private final boolean permissaoLiberada;
    /**
     * Atributo que indica se a permissão está bloqueada no banco de dados.
     * Isso não impede que a permissão esteja associada ao cargo.
     */
    private final boolean permissaoBloqueada;
    @Nullable
    private final CargoPermissaoInfosBloqueio infosBloqueio;
}
