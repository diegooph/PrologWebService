package br.com.zalf.prolog.webservice.v3.fleet.afericao;

import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.*;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class AfericaoMapper {
    @NotNull
    public List<AfericaoPlacaDto> toAfericaoPlacaDto(@NotNull final List<AfericaoPlacaProjection> afericoesPlacas) {
        final Map<Long, List<MedidaDto>> medidasAgrupadas = this.groupMedidasByAfericoesPlacas(afericoesPlacas);
        return medidasAgrupadas.keySet().stream()
                .map(codAfericao -> {
                    final AfericaoPlacaProjection primeiraAfericaoByCodigo = afericoesPlacas.stream()
                            .filter(afericaoPlaca -> Objects.equals(afericaoPlaca.getCodigo(), codAfericao))
                            .findFirst()
                            .orElseThrow();
                    return toAfericaoPlacaDto(primeiraAfericaoByCodigo,
                                              medidasAgrupadas.get(primeiraAfericaoByCodigo.getCodigo()));
                })
                .collect(toList());
    }

    @NotNull
    public AfericaoPlacaDto toAfericaoPlacaDto(@NotNull final AfericaoPlacaProjection afericaoPlaca,
                                               @NotNull final List<MedidaDto> medidas) {
        return AfericaoPlacaDto.of(afericaoPlaca.getCodigo(),
                                   afericaoPlaca.getCodUnidade(),
                                   afericaoPlaca.getCodColaboradorAferidor(),
                                   afericaoPlaca.getCpfAferidor(),
                                   afericaoPlaca.getNomeAferidor(),
                                   afericaoPlaca.getCodVeiculo(),
                                   afericaoPlaca.getPlacaVeiculo(),
                                   afericaoPlaca.getIdentificadorFrota(),
                                   afericaoPlaca.getKmVeiculo(),
                                   afericaoPlaca.getDataHoraAfericaoUtc(),
                                   afericaoPlaca.getDataHoraAfericaoTzAplicado(),
                                   afericaoPlaca.getTipoMedicaoColetadaAfericao(),
                                   afericaoPlaca.getTipoProcessoColetaAfericao(),
                                   afericaoPlaca.getTempoRealizacaoAfericaoInMillis(),
                                   afericaoPlaca.getFormaColetaDadosAfericao(),
                                   medidas.isEmpty() ? null : medidas);
    }

    @NotNull
    public List<AfericaoAvulsaDto> toAfericaoAvulsaDto(@NotNull final List<AfericaoAvulsaProjection> afericoesAvulsas) {
        final Map<Long, List<MedidaDto>> medidasAgrupadas = this.groupMedidasByAfericoesAvulsas(afericoesAvulsas);
        return medidasAgrupadas.keySet().stream()
                .map(codAfericao -> {
                    final AfericaoAvulsaProjection primeiraAfericaoByCodigo = afericoesAvulsas.stream()
                            .filter(afericaoAvulsa -> Objects.equals(afericaoAvulsa.getCodigo(), codAfericao))
                            .findFirst()
                            .orElseThrow();
                    return toAfericaoAvulsaDto(primeiraAfericaoByCodigo,
                                               medidasAgrupadas.get(primeiraAfericaoByCodigo.getCodigo()));
                })
                .collect(toList());
    }

    @NotNull
    public AfericaoAvulsaDto toAfericaoAvulsaDto(@NotNull final AfericaoAvulsaProjection afericaoAvulsa,
                                                 @NotNull final List<MedidaDto> medidas) {
        return AfericaoAvulsaDto.of(afericaoAvulsa.getCodigo(),
                                    afericaoAvulsa.getCodUnidade(),
                                    afericaoAvulsa.getCodColaboradroAferidor(),
                                    afericaoAvulsa.getCpfAferidor(),
                                    afericaoAvulsa.getNomeAferidor(),
                                    afericaoAvulsa.getDataHoraAfericaoUtc(),
                                    afericaoAvulsa.getDataHoraAfericaoTzAplicado(),
                                    afericaoAvulsa.getTipoMedicaoColetadaAfericao(),
                                    afericaoAvulsa.getTipoProcessoColetaAfericao(),
                                    afericaoAvulsa.getTempoRealizacaoAfericaoInMillis(),
                                    afericaoAvulsa.getFormaColetaDadosAfericao(),
                                    medidas.isEmpty() ? null : medidas);
    }

    private boolean projectionContainsMedidas(final @NotNull AfericaoAvulsaProjection projection) {
        return Objects.nonNull(projection.getCodPneu());
    }

    private boolean projectionContainsMedidas(final @NotNull AfericaoPlacaProjection projection) {
        return Objects.nonNull(projection.getCodPneu());
    }

    @NotNull
    private Map<Long,
            List<MedidaDto>> groupMedidasByAfericoesAvulsas(@NotNull final List<AfericaoAvulsaProjection> projections) {
        return projections.stream()
                .collect(groupingBy(AfericaoAvulsaProjection::getCodigo,
                                    filtering(this::projectionContainsMedidas,
                                              mapping(this::generateMedidaFromAfericao, toUnmodifiableList()))));
    }

    @NotNull
    private Map<Long,
            List<MedidaDto>> groupMedidasByAfericoesPlacas(@NotNull final List<AfericaoPlacaProjection> projections) {
        return projections.stream()
                .collect(groupingBy(AfericaoPlacaProjection::getCodigo,
                                    filtering(this::projectionContainsMedidas,
                                              mapping(this::generateMedidaFromAfericao, toUnmodifiableList()))));
    }

    @NotNull
    private MedidaDto generateMedidaFromAfericao(@NotNull final AfericaoAvulsaProjection projection) {
        return MedidaDto.of(projection.getCodPneu(),
                            projection.getCodigClientePneu(),
                            projection.getPosicao(),
                            projection.getVidaMomentoAfericao(),
                            projection.getPsi(),
                            projection.getAlturaSulcoInterno(),
                            projection.getAlturaSulcoCentralInterno(),
                            projection.getAlturaSulcoCentralExterno(),
                            projection.getAlturaSulcoExterno());
    }

    @NotNull
    private MedidaDto generateMedidaFromAfericao(@NotNull final AfericaoPlacaProjection projection) {
        return MedidaDto.of(projection.getCodPneu(),
                            projection.getCodigClientePneu(),
                            projection.getPosicao(),
                            projection.getVidaMomentoAfericao(),
                            projection.getPsi(),
                            projection.getAlturaSulcoInterno(),
                            projection.getAlturaSulcoCentralInterno(),
                            projection.getAlturaSulcoCentralExterno(),
                            projection.getAlturaSulcoExterno());
    }
}
