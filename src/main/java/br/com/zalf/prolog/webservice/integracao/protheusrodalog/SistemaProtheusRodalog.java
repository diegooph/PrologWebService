package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class SistemaProtheusRodalog extends Sistema {
    @NotNull
    private final ProtheusRodalogRequesterImpl requester;

    public SistemaProtheusRodalog(@NotNull final ProtheusRodalogRequesterImpl requester,
                                  @NotNull final SistemaKey sistemaKey,
                                  @NotNull final IntegradorProLog integradorProLog,
                                  @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        final String tokenIntegracao = getIntegradorProLog().getTokenIntegracaoByCodUnidadeProLog(codUnidade);
        return requester.insertAfericao(tokenIntegracao, codUnidade, ProtheusRodalogConverter.convertAfericao(afericao));
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        final String tokenIntegracao = getIntegradorProLog().getTokenIntegracaoByCodUnidadeProLog(codUnidade);
        return ProtheusRodalogConverter.convertCronogramaAfericao(requester.getCronogramaAfericao(tokenIntegracao, codUnidade));
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        final String tokenIntegracao = getIntegradorProLog().getTokenIntegracaoByCodUnidadeProLog(codUnidade);
        final NovaAfericaoPlacaProtheusRodalog novaAfericaoPlaca =
                requester.getNovaAfericaoPlaca(tokenIntegracao, codUnidade, placaVeiculo, tipoAfericao);
        if (novaAfericaoPlaca.getCodDiagrama() == null) {
            throw new IllegalStateException("Código do diagrama é null");
        }
        final Optional<DiagramaVeiculo> diagramaVeiculo =
                getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(novaAfericaoPlaca.getCodDiagrama().shortValue());
        if (!diagramaVeiculo.isPresent()) {
            throw new IllegalStateException(
                    "Nenhum diagrama encontrado para o código: " + novaAfericaoPlaca.getCodDiagrama());
        }
        return ProtheusRodalogConverter.convertNovaAfericaoPlaca(novaAfericaoPlaca, diagramaVeiculo.get());
    }
}
