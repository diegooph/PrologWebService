package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.historico;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.TipoAcaoAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 17/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DescricaoAcaoAjusteCreator {

    public DescricaoAcaoAjusteCreator() {
        throw new IllegalStateException(DescricaoAcaoAjusteCreator.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String create(@NotNull final TipoAcaoAjuste acaoAjuste,
                                @NotNull final TipoInicioFim tipoInicioFim,
                                @NotNull final LocalDateTime dataHoraAntiga,
                                @NotNull final LocalDateTime dataHoraNova,
                                @NotNull final String nomeColaborador,
                                @NotNull final LocalDateTime dataHoraAjuste) {
        switch (acaoAjuste) {
            case ADICAO:
            case ADICAO_INICIO_FIM:
                return "Marcação de " + tipoInicioFim.asString() + " com data e hora '" + dataHoraNova.toString() + "' criada por " + nomeColaborador;
            case EDICAO:
                return "Marcação de " + tipoInicioFim.asString() + " editada de '" + dataHoraAntiga.toString() + "' para '" + dataHoraNova.toString() + "' por " + nomeColaborador;
            case ATIVACAO_INATIVACAO:
                return "Marcação de " + tipoInicioFim.asString() + " ativada ou inativada por " + nomeColaborador;
        }


        throw new IllegalStateException();
    }
}