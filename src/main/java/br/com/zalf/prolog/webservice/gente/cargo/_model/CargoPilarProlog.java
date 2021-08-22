package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by luiz on 4/18/16.
 */
@Data
public final class CargoPilarProlog {
    private final int codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<CargoFuncionalidadeProlog> funcionalidades;
}
