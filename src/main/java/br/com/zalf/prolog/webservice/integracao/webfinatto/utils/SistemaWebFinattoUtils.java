package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SistemaWebFinattoUtils {
    @NotNull
    public static List<Long> getCodUnidadesFiltroProlog(@NotNull final List<Empresa> filtrosProlog) {
        return filtrosProlog.stream()
                .map(Empresa::getListRegional)
                .flatMap(Collection::stream)
                .map(Regional::getListUnidade)
                .flatMap(Collection::stream)
                .map(Unidade::getCodigo)
                .collect(Collectors.toList());
    }

    @NotNull
    public static String formatCpfAsString(@NotNull final Long cpf) {
        return String.format("%011d", cpf);
    }
}
