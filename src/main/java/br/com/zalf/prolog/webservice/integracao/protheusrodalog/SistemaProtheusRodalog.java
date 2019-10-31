package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.NovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.ProtheusRodalogResponseAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.TipoMedicaoAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.error.ProtheusRodalogException;
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
        final ProtheusRodalogResponseAfericao responseAfericao = requester.insertAfericao(
                tokenIntegracao,
                codUnidade,
                ProtheusRodalogConverter.convertAfericao(afericao));
        if (responseAfericao.isStatus() && responseAfericao.getCodigoAfericaoInserida() != null) {
            return responseAfericao.getCodigoAfericaoInserida();
        } else {
            throw new Exception("[INTEGRACAO - RODALOG] Não foi possível inserir a Aferição no sistema");
        }
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        final String tokenIntegracao = getIntegradorProLog().getTokenIntegracaoByCodUnidadeProLog(codUnidade);
        return ProtheusRodalogConverter
                .convertCronogramaAfericao(requester.getCronogramaAfericao(tokenIntegracao, codUnidade));
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) {
        try {
            final String tokenIntegracao = getIntegradorProLog().getTokenIntegracaoByCodUnidadeProLog(codUnidade);
            final NovaAfericaoPlacaProtheusRodalog novaAfericaoPlaca =
                    requester.getNovaAfericaoPlaca(
                            tokenIntegracao,
                            codUnidade,
                            placaVeiculo,
                            TipoMedicaoAfericaoProtheusRodalog.fromString(tipoAfericao));
            if (novaAfericaoPlaca.getCodDiagrama() == null) {
                throw new IllegalStateException("[INTEGRACAO - RODALOG] O código do diagrama é null\n" +
                        "CodUnidade: " + codUnidade + "\n" +
                        "Placa: " + placaVeiculo);
            }
            final Optional<DiagramaVeiculo> diagramaVeiculo =
                    getIntegradorProLog()
                            .getDiagramaVeiculoByCodDiagrama(novaAfericaoPlaca.getCodDiagrama().shortValue());
            if (!diagramaVeiculo.isPresent()) {
                throw new IllegalStateException(
                        "[INTEGRACAO - RODALOG] Nenhum diagrama encontrado para o código: "
                                + novaAfericaoPlaca.getCodDiagrama());
            }
            return ProtheusRodalogConverter.convertNovaAfericaoPlaca(novaAfericaoPlaca, diagramaVeiculo.get());
        } catch (final ProtheusRodalogException protheusException) {
            // Se está chegando até aqui uma ProtheusRodalogException significa que já mapeamos o problema.
            throw protheusException;
        } catch (final Throwable t) {
            // Mas se for algo diferente, devemos mapear a exception e ainda logar ela junto ao mapeamento.
            throw new ProtheusRodalogException(
                    "[INTEGRACAO - RODALOG] Erro na integração com o Protheus",
                    "Alguma informação veio errada do Protheus",
                    t);
        }
    }
}
