package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Wellington on 22/05/19.
 */
@Data
public final class CargoFuncionalidadeProlog {
    private final int codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<CargoPermissaoProlog> permissoes;
}
