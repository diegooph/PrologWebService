package br.com.zalf.prolog.webservice.integracao.webfinatto._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class EmpresaWebFinatto {
    @NotNull
    private final Long idCliente;
    @NotNull
    private final String nomeCliente;
    @NotNull
    private final List<UnidadeWebFinatto> unidades;
}