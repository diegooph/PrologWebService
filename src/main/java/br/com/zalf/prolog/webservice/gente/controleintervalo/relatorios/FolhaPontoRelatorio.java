package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.util.date.Durations;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Clt;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    public void calculaTempoEmCadaTipoIntervalo(@NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal,
                                                @NotNull final Map<Long, TipoIntervalo> tiposIntervalosUnidade,
                                                @NotNull final ZoneId zoneId) {
        //noinspection ConstantConditions
        Preconditions.checkState(marcacoesDias != null);

        final LocalDateTime filtroInicio = dataInicial.atTime(LocalTime.MIN);
        final LocalDateTime filtroFim = dataFinal.atTime(LocalTime.MAX);

        // Map para irmos somando o tempo em segundos que o colaborador passou em cada tipo de intervalo dado
        // o período filtrado.
        final Map<Long, Long> segundosTotaisTipoIntervalo = new HashMap<>();

        // Map para irmos somando o tempo em segundos que o colaborador passou em horas noturnas (conforme estipulado
        // pela CLT) em cada tipo de intervalo dado o período filtrado.
        final Map<Long, Long> segundosTotaisHorasNoturnas = new HashMap<>();

        for (int i = 0; i < marcacoesDias.size(); i++) {
            final List<FolhaPontoIntervalo> intervalosDia = marcacoesDias.get(i).getIntervalosDia();
            for (int j = 0; j < intervalosDia.size(); j++) {
                final FolhaPontoIntervalo intervalo = intervalosDia.get(j);
                final LocalDateTime dataHoraInicio = intervalo.getDataHoraInicioUtc();
                final LocalDateTime dataHoraFim = intervalo.getDataHoraFimUtc();
                somaTempoDecorrido(
                        segundosTotaisTipoIntervalo,
                        segundosTotaisHorasNoturnas,
                        intervalo,
                        dataHoraInicio,
                        dataHoraFim,
                        filtroInicio,
                        // Sem o filtro de fim for maior que o horário atual do sistema, precisamos garantir que os
                        // cálculos utilizem o horário atual e não o filtro de fim.
                        filtroFim.isBefore(dataHoraGeracaoRelatorioUtc) ? filtroFim : dataHoraGeracaoRelatorioUtc,
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
                                    @NotNull final LocalDateTime filtroInicio,
                                    @NotNull final LocalDateTime filtroFim,
                                    @NotNull final ZoneId zoneId) {
        // Calcula a diferença de tempo entre início e fim, se ambos existirem.
        if (dataHoraInicio != null && dataHoraFim != null) {
            // Precisamos cobrir 4 diferentes casos:
            // 1 - Uma marcação com início ANTES do período do filtro e fim DENTRO do período.
            // 2 - Uma marcação com início DENTRO do período do filtro e fim DENTRO do período.
            // 3 - Uma marcação com início DENTRO do período do filtro e fim DEPOIS do período.
            // 4 - Uma marcação com início ANTES do período do filtro e fim DEPOIS do período.
            LocalDateTime inicio, fim;
            // Caso 1) ANTES -> DENTRO
            if (dataHoraInicio.isBefore(filtroInicio)
                    && filtroInicio.isBefore(dataHoraFim)
                    && dataHoraFim.isBefore(filtroFim)) {
                inicio = filtroInicio;
                fim = dataHoraFim;

                // Caso 2) DENTRO -> DENTRO
            } else if (filtroInicio.isBefore(dataHoraInicio)
                    && filtroFim.isAfter(dataHoraFim)) {
                inicio = dataHoraInicio;
                fim = dataHoraFim;

                // Caso 3) DENTRO -> DEPOIS
            } else if (dataHoraInicio.isAfter(filtroInicio)
                    && filtroFim.isAfter(dataHoraInicio)
                    && dataHoraFim.isAfter(filtroFim)) {
                inicio = dataHoraInicio;
                fim = filtroFim;

                // Caso 4) ANTES -> DEPOIS
            } else if (dataHoraInicio.isBefore(filtroInicio)
                    && dataHoraFim.isAfter(filtroFim)) {
                inicio = filtroInicio;
                fim = filtroFim;
            } else {
                throw new IllegalStateException("Condição não mapeada! :(");
            }
            final long segundos = ChronoUnit.SECONDS.between(inicio, fim);
            final long segundosNoturnos = Durations
                    .getSumOfHoursInRangeOnDays(inicio, fim, Clt.RANGE_HORAS_NOTURNAS, zoneId)
                    .getSeconds();
            segundosTotaisTipoIntervalo.merge(intervalo.getCodTipoIntervalo(), segundos, (a, b) -> a + b);
            segundosTotaisHorasNoturnas.merge(intervalo.getCodTipoIntervalo(), segundosNoturnos, (a, b) -> a + b);
        } else {
            // Se a marcação de início ou fim não existirem, nós não temos como calcular o tempo total nesse
            // tipo de intervalo. Porém, caso o colaborador tenha marcado apenas INÍCIOS para um tipo de
            // intervalo e nenhum fim, nós nunca vamos calcular o tempo total gasto e com isso, não adicionaremos
            // esse tipo de intervalo nos intervalos marcados pelo colaborador. Pois ele não estará no Map
            // segundosTotais. Esse putIfAbsent garante que cobrimos esse caso.
            segundosTotaisTipoIntervalo.putIfAbsent(intervalo.getCodTipoIntervalo(), 0L);
            segundosTotaisHorasNoturnas.putIfAbsent(intervalo.getCodTipoIntervalo(), 0L);
        }
    }

    @NotNull
    private Set<FolhaPontoTipoIntervalo> createTiposIntervalosMarcados(
            @NotNull final Map<Long, TipoIntervalo> tiposIntervalosUnidade,
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
}