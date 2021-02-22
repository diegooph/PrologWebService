package br.com.zalf.prolog.webservice.integracao.integrador._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
public final class UnidadeRestricaoHolder {
    @NotNull
    final Map<String, UnidadeRestricao> infosUnidadeRestricao;
}
