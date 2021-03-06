package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico;

import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.TipoAcaoAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cria descrições humanamente legíveis para todas as ações de ajuste.
 *
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
                return String.format("Marcação de <b>%s</b> <i>CRIADA</i> com data e hora <b>\"%s\"</b> por %s",
                        inicioFimExibicao,
                        format(dataHoraNova),
                        nomeColaborador);
            case EDICAO:
                return String.format("Marcação de <b>%s</b> <i>EDITADA</i> de <b>\"%s\"</b> para <b>\"%s\"</b> por %s",
                        inicioFimExibicao,
                        format(dataHoraAntiga),
                        format(dataHoraNova),
                        nomeColaborador);
            case ATIVACAO:
                return String.format("Marcação de <b>%s</b> (%s) <i>ATIVADA</i> por %s",
                        inicioFimExibicao,
                        format(dataHoraAntiga),
                        nomeColaborador);
            case INATIVACAO:
                return String.format("Marcação de <b>%s</b> (%s) <i>INATIVADA</i> por %s",
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