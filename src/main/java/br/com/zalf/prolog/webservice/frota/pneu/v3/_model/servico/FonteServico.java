package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Created on 2021-03-25
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@AllArgsConstructor
@Getter
public enum FonteServico {
    CADASTRO("FONTE_CADASTRO"), MOVIMENTACAO("FONTE_MOVIMENTACAO");

    private final String name;

    @NotNull
    public static FonteServico fromName(@NotNull final String name) {
        return Stream.of(FonteServico.values())
                .filter(e -> e.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nenhuma fonte de serviço encontrada para a string: "
                                + name));
    }
}