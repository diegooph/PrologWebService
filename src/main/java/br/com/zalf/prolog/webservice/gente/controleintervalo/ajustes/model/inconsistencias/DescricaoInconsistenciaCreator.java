package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DescricaoInconsistenciaCreator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DescricaoInconsistenciaCreator() {
        throw new IllegalStateException(DescricaoInconsistenciaCreator.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @NotNull
    public static String descricaoSemVinculo(@NotNull final LocalDateTime dataHoraMarcacao,
                                             @NotNull final TipoInicioFim tipoInicioFim) {
        return String.format(
                "A marcação com data/hora <b>\"%s\"</b> não possui vínculo de <b>%s</b>",
                format(dataHoraMarcacao),
                tipoInicioFim.getTipoContrario().getLegibleString());
    }

    @NotNull
    public static String descricaoFimAntesInicio(@NotNull final LocalDateTime dataHoraInicio,
                                                 @NotNull final LocalDateTime dataHoraFim) {
        return String.format(
                "A marcação de fim feita em <b>\"%s\"</b> tem data/hora menor do que a marcação de início: <b>\"%s\"</b>",
                format(dataHoraFim),
                format(dataHoraInicio));
    }

    @NotNull
    private static String format(@NotNull final LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }
}