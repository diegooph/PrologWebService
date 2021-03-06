package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Clt;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.threeten.extra.Interval;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class FolhaPontoRelatorio {
    @NotNull
    private final Colaborador colaborador;
    @NotNull
    private final List<FolhaPontoDia> marcacoesDias;
    private Set<FolhaPontoTipoIntervalo> tiposIntervalosMarcados;

    @SerializedName("dataHoraGeracaoRelatorio")
    @NotNull
    private final LocalDateTime dataHoraGeracaoRelatorioZoned;

    @Exclude
    @NotNull
    private final LocalDateTime dataHoraGeracaoRelatorioUtc;

    public FolhaPontoRelatorio(@NotNull final Colaborador colaborador,
                               @NotNull final List<FolhaPontoDia> marcacoesDias,
                               @NotNull final LocalDateTime dataHoraGeracaoRelatorioUtc,
                               @NotNull final LocalDateTime dataHoraGeracaoRelatorioZoned) {
        this.colaborador = colaborador;
        this.marcacoesDias = marcacoesDias;
        this.dataHoraGeracaoRelatorioUtc = dataHoraGeracaoRelatorioUtc;
        this.dataHoraGeracaoRelatorioZoned = dataHoraGeracaoRelatorioZoned;
    }

    @NotNull
    public Colaborador getColaborador() {
        return colaborador;
    }

    @NotNull
    public Set<FolhaPontoTipoIntervalo> getTiposIntervalosMarcados() {
        return tiposIntervalosMarcados;
    }

    @NotNull
    public List<FolhaPontoDia> getMarcacoesDias() {
        return marcacoesDias;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void calculaTempoEmCadaTipoIntervalo(@NotNull final Map<Long, TipoMarcacao> tiposIntervalosUnidade,
                                                @NotNull final ZoneId zoneId) {
        //noinspection ConstantConditions
        Preconditions.checkState(marcacoesDias != null);

        // Map para irmos somando o tempo em segundos que o colaborador passou em cada tipo de intervalo dado
        // o per??odo filtrado.
        final Map<Long, Long> segundosTotaisTipoIntervalo = new HashMap<>();

        // Map para irmos somando o tempo em segundos que o colaborador passou em horas noturnas (conforme estipulado
        // pela CLT) em cada tipo de intervalo dado o per??odo filtrado.
        final Map<Long, Long> segundosTotaisHorasNoturnas = new HashMap<>();

        for (int i = 0; i < marcacoesDias.size(); i++) {
            final List<FolhaPontoIntervalo> intervalosDia = marcacoesDias.get(i).getIntervalosDia();
            for (int j = 0; j < intervalosDia.size(); j++) {
                final FolhaPontoIntervalo intervalo = intervalosDia.get(j);
                final LocalDateTime dataHoraInicio = intervalo.getDataHoraInicio();
                final LocalDateTime dataHoraFim = intervalo.getDataHoraFim();
                somaTempoDecorrido(
                        segundosTotaisTipoIntervalo,
                        segundosTotaisHorasNoturnas,
                        intervalo,
                        dataHoraInicio,
                        dataHoraFim,
                        zoneId);
            }
        }

        tiposIntervalosMarcados = createTiposIntervalosMarcados(
                tiposIntervalosUnidade,
                segundosTotaisTipoIntervalo,
                segundosTotaisHorasNoturnas);
    }

    private void somaTempoDecorrido(@NotNull final Map<Long, Long> segundosTotaisTipoIntervalo,
                                    @NotNull final Map<Long, Long> segundosTotaisHorasNoturnas,
                                    @NotNull final FolhaPontoIntervalo intervalo,
                                    @Nullable final LocalDateTime dataHoraInicio,
                                    @Nullable final LocalDateTime dataHoraFim,
                                    @NotNull final ZoneId zoneId) {
        // Calcula a diferen??a de tempo entre in??cio e fim, se ambos existirem.
        if (dataHoraInicio != null && dataHoraFim != null) {
            final ZonedDateTime inicioZoned = dataHoraInicio.atZone(zoneId);
            final ZonedDateTime fimZoned = dataHoraFim.atZone(zoneId);
            final long segundos = ChronoUnit.SECONDS.between(inicioZoned, fimZoned);
            final long segundosNoturnos = calculaHorasNoturnas(inicioZoned, fimZoned, zoneId).getSeconds();
            segundosTotaisTipoIntervalo.merge(intervalo.getCodTipoIntervalo(), segundos, (a, b) -> a + b);
            segundosTotaisHorasNoturnas.merge(intervalo.getCodTipoIntervalo(), segundosNoturnos, (a, b) -> a + b);
        } else {
            // Se a marca????o de in??cio ou fim n??o existirem, n??s n??o temos como calcular o tempo total nesse
            // tipo de intervalo. Por??m, caso o colaborador tenha marcado apenas IN??CIOS para um tipo de
            // intervalo e nenhum fim, n??s nunca vamos calcular o tempo total gasto e com isso, n??o adicionaremos
            // esse tipo de intervalo nos intervalos marcados pelo colaborador. Pois ele n??o estar?? no Map
            // segundosTotais. Esse putIfAbsent garante que cobrimos esse caso.
            segundosTotaisTipoIntervalo.putIfAbsent(intervalo.getCodTipoIntervalo(), 0L);
            segundosTotaisHorasNoturnas.putIfAbsent(intervalo.getCodTipoIntervalo(), 0L);
        }
    }

    @NotNull
    private Set<FolhaPontoTipoIntervalo> createTiposIntervalosMarcados(
            @NotNull final Map<Long, TipoMarcacao> tiposIntervalosUnidade,
            @NotNull final Map<Long, Long> segundosTotaisTipoIntervalo,
            @NotNull final Map<Long, Long> segundosHorasNoturnasTipoIntervalo) {
        Preconditions.checkArgument(segundosTotaisTipoIntervalo.size() == segundosHorasNoturnasTipoIntervalo.size());

        final Set<FolhaPontoTipoIntervalo> tiposIntervalosMarcados = new HashSet<>();
        tiposIntervalosUnidade.forEach((codTipoIntervalo, tipoIntervalo) -> {
            final Long segundosTotal = segundosTotaisTipoIntervalo.get(codTipoIntervalo);
            final Long segundosHorasNoturnasTotal = segundosHorasNoturnasTipoIntervalo.get(codTipoIntervalo);
            if (segundosTotal != null && segundosHorasNoturnasTotal != null) {
                tiposIntervalosMarcados.add(FolhaPontoTipoIntervalo.createFromTipoIntervalo(
                        tipoIntervalo,
                        segundosTotal,
                        segundosHorasNoturnasTotal));
            }
        });

        return tiposIntervalosMarcados;
    }

    /**
     * @deprecated em 15/01/2019. Essa solu????o n??o lida corretamente com mudan??as de hor??rio de ver??o/inverno e por
     * isso n??o deve mais ser utilizada. Existe uma function em BD que realiza o mesmo tipo de c??lculo e considera
     * corretamente as mudan??as de hor??rio de ver??o/inverno, portanto, devemos utilizar o c??lculo em banco de dados.
     */
    @Deprecated
    @NotNull
    private Duration calculaHorasNoturnas(@NotNull final ZonedDateTime fromTz,
                                          @NotNull final ZonedDateTime toTz,
                                          @NotNull final ZoneId zoneId) {
        final Interval interval = Interval.of(fromTz.toInstant(), toTz.toInstant());

        final LocalDate ldStart = fromTz.toLocalDate();
        final LocalDate ldStop = toTz.toLocalDate();
        LocalDate localDate = ldStart;

        final LocalTime timeStartMin = LocalTime.MIN;
        final LocalTime timeStop05 = Clt.FIM_HORAS_NOTURAS;

        final LocalTime timeStart22 = Clt.INICIO_HORAS_NOTURAS;
        final LocalTime timeStopMax = LocalTime.MAX;

        Duration totalDuration = Duration.ZERO;
        while (!localDate.isAfter(ldStop)) {
            final Interval intervalMinTo05 = Interval.of(
                    localDate.atTime(timeStartMin).atZone(zoneId).toInstant(),
                    localDate.atTime(timeStop05).atZone(zoneId).toInstant());
            if (interval.overlaps(intervalMinTo05)) {
                totalDuration = totalDuration.plus(interval.intersection(intervalMinTo05).toDuration());
            }

            final Interval interval22ToMax = Interval.of(
                    localDate.atTime(timeStart22).atZone(zoneId).toInstant(),
                    localDate.atTime(timeStopMax).atZone(zoneId).toInstant());
            if (interval.overlaps(interval22ToMax)) {
                // Como o nosso Max ?? o LocalTime.MAX, ele vai at?? 23:59:59.999999999. Dessa forma, a cada compara????o
                // dessa, n??s acabamos perdendo um nano de segundo no valor total das horas noturnas. Para compensar
                // isso, utilizamos o plusNanos(1). ?? um hack simples e feio, mas funciona.
                totalDuration = totalDuration.plus(interval.intersection(interval22ToMax).toDuration().plusNanos(1));
            }

            // Setup the next loop.
            localDate = localDate.plusDays(1);
        }
        return totalDuration;
    }
}