package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.historico;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.TipoAcaoAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 17/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DescricaoAcaoAjusteCreator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DescricaoAcaoAjusteCreator() {
        throw new IllegalStateException(DescricaoAcaoAjusteCreator.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String create(@NotNull final TipoAcaoAjuste acaoAjuste,
                                @NotNull final TipoInicioFim tipoInicioFim,
                                @NotNull final LocalDateTime dataHoraAntiga,
                                @NotNull final LocalDateTime dataHoraNova,
                                @NotNull final String nomeColaborador) {
        final String inicioFimExibicao = tipoInicioFim.getLegibleString().toLowerCase();
        switch (acaoAjuste) {
            case ADICAO:
            case ADICAO_INICIO_FIM:
                return String.format("\u002B Marcação de <b>%s</b> <i>CRIADA</i> com data e hora <b>\"%s\"</b> por %s",
                        inicioFimExibicao,
                        format(dataHoraNova),
                        nomeColaborador);
            case EDICAO:
                return String.format("\u270E Marcação de <b>%s</b> <i>EDITADA</i> de <b>\"%s\"</b> para <b>\"%s\"</b> por %s",
                        inicioFimExibicao,
                        format(dataHoraAntiga),
                        format(dataHoraNova),
                        nomeColaborador);
            case ATIVACAO:
                return String.format("\u2713 Marcação de <b>%s</b> (%s) <i>ATIVADA</i> por %s",
                        inicioFimExibicao,
                        format(dataHoraAntiga),
                        nomeColaborador);
            case INATIVACAO:
                return String.format("\u0058 Marcação de <b>%s</b> (%s) <i>INATIVADA</i> por %s",
                        inicioFimExibicao,
                        format(dataHoraAntiga),
                        nomeColaborador);
        }


        throw new IllegalStateException();
    }

    @NotNull
    private static String format(@NotNull final LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }
}