package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.MedicaoAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.TipoMedicaoAfericaoRodoparHorizonte;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RodoparHorizonteConverter {
    @NotNull
    public static AfericaoPlacaRodoparHorizonte convert(@NotNull final Long codUnidade,
                                                        @NotNull final AfericaoPlaca afericao) {
        final TipoMedicaoAfericaoRodoparHorizonte tipoMedicao =
                TipoMedicaoAfericaoRodoparHorizonte.fromString(afericao.getTipoMedicaoColetadaAfericao().asString());
        return new AfericaoPlacaRodoparHorizonte(
                afericao.getVeiculo().getPlaca(),
                codUnidade,
                afericao.getColaborador().getCpfAsString(),
                afericao.getKmMomentoAfericao(),
                afericao.getTempoRealizacaoAfericaoInMillis(),
                afericao.getDataHora(),
                tipoMedicao,
                convertMedicoes(tipoMedicao, afericao));
    }

    @NotNull
    public static AfericaoAvulsaRodoparHorizonte convert(@NotNull final Long codUnidade,
                                                         @NotNull final AfericaoAvulsa afericao) {
        final TipoMedicaoAfericaoRodoparHorizonte tipoMedicao =
                TipoMedicaoAfericaoRodoparHorizonte.fromString(afericao.getTipoMedicaoColetadaAfericao().asString());
        if (tipoMedicao != TipoMedicaoAfericaoRodoparHorizonte.SULCO) {
            throw new IllegalStateException("[INTEGRACAO - RODOPAR] Aferição de Pneu avulso só pode medir SULCO");
        }
        return new AfericaoAvulsaRodoparHorizonte(
                codUnidade,
                afericao.getColaborador().getCpfAsString(),
                afericao.getTempoRealizacaoAfericaoInMillis(),
                afericao.getDataHora(),
                tipoMedicao,
                createMedidaFrom(tipoMedicao, afericao.getPneuAferido()));
    }

    @NotNull
    private static List<MedicaoAfericaoRodoparHorizonte> convertMedicoes(
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoMedicao,
            @NotNull final AfericaoPlaca afericao) {
        final List<MedicaoAfericaoRodoparHorizonte> medidas = new ArrayList<>();
        for (final Pneu pneuAferido : afericao.getPneusAferidos()) {
            medidas.add(createMedidaFrom(tipoMedicao, pneuAferido));
        }
        return medidas;
    }

    @NotNull
    private static MedicaoAfericaoRodoparHorizonte createMedidaFrom(
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoMedicao,
            @NotNull final Pneu pneuAferido) {
        switch (tipoMedicao) {
            case SULCO: {
                final Sulcos sulcosAtuais = pneuAferido.getSulcosAtuais();
                if (sulcosAtuais == null) {
                    throw new IllegalStateException(
                            "[INTEGRACAO - RODOPAR] O tipo de medição é SULCO, mas os sulcos medidos são nulos.");
                }
                return new MedicaoAfericaoRodoparHorizonte(
                        pneuAferido.getCodigoCliente(),
                        pneuAferido.getCodigo(),
                        pneuAferido.getVidaAtual(),
                        null,
                        sulcosAtuais.getInterno(),
                        sulcosAtuais.getCentralInterno(),
                        sulcosAtuais.getCentralExterno(),
                        sulcosAtuais.getExterno());
            }
            case PRESSAO: {
                return new MedicaoAfericaoRodoparHorizonte(
                        pneuAferido.getCodigoCliente(),
                        pneuAferido.getCodigo(),
                        pneuAferido.getVidaAtual(),
                        pneuAferido.getPressaoAtual(),
                        null,
                        null,
                        null,
                        null);
            }
            case SULCO_PRESSAO: {
                final Sulcos sulcosAtuais = pneuAferido.getSulcosAtuais();
                if (sulcosAtuais == null) {
                    throw new IllegalStateException(
                            "[INTEGRACAO - RODOPAR] O tipo de medição é SULCO, mas os sulcos medidos são nulos.");
                }
                return new MedicaoAfericaoRodoparHorizonte(
                        pneuAferido.getCodigoCliente(),
                        pneuAferido.getCodigo(),
                        pneuAferido.getVidaAtual(),
                        pneuAferido.getPressaoAtual(),
                        sulcosAtuais.getInterno(),
                        sulcosAtuais.getCentralInterno(),
                        sulcosAtuais.getCentralExterno(),
                        sulcosAtuais.getExterno());
            }
            default:
                throw new IllegalStateException("Nenhum tipo de Aferição mapeado para o tipo: " + tipoMedicao);
        }
    }
}
