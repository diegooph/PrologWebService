package br.com.zalf.prolog.webservice.integracao.praxio.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-07-21
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class IntegracaoPraxioUnidadesBloqueadas {
    @NotNull
    private List<Long> codUnidadesBloqueadas;
}
