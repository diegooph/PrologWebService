package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.AfericaoAvulsaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.MedicaoAfericaoProtheusNepomuceno;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ProtheusNepomucenoConverter {

    private ProtheusNepomucenoConverter() {
        throw new IllegalStateException(ProtheusNepomucenoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static AfericaoPlacaProtheusNepomuceno convert(@NotNull final String codAuxiliarUnidade,
                                                          @NotNull final AfericaoPlaca afericaoPlaca) {
        // Separa o código de empresa e unidade do campo auxiliar.
        final String[] empresaUnidade = codAuxiliarUnidade.split(":");

        // Cria a variável que conterá a listagem de medições.
        final List<MedicaoAfericaoProtheusNepomuceno> medicoes = new ArrayList<>();

        // Percorre a lista de pneus aferidos e cria a lista de objetos de medição.
        for (Pneu pneu : afericaoPlaca.getPneusAferidos()) {
            final MedicaoAfericaoProtheusNepomuceno medicao = new MedicaoAfericaoProtheusNepomuceno(
                    pneu.getCodigoCliente(),
                    pneu.getCodigo(),
                    pneu.getVidaAtual(),
                    pneu.getPressaoAtual(),
                    pneu.getSulcosAtuais().getInterno(),
                    pneu.getSulcosAtuais().getCentralInterno(),
                    pneu.getSulcosAtuais().getCentralExterno(),
                    pneu.getSulcosAtuais().getExterno()
            );
            medicoes.add(medicao);
        }

        // Cria o objeto de aferição de placa que será enviado na integração.
        final AfericaoPlacaProtheusNepomuceno afericaoPlacaProtheus = new AfericaoPlacaProtheusNepomuceno(
                empresaUnidade[0],
                empresaUnidade[1],
                afericaoPlaca.getVeiculo().getPlaca(),
                String.valueOf(afericaoPlaca.getColaborador().getCpf()),
                afericaoPlaca.getKmMomentoAfericao(),
                afericaoPlaca.getTempoRealizacaoAfericaoInMillis(),
                afericaoPlaca.getDataHora(),
                afericaoPlaca.getTipoMedicaoColetadaAfericao(),
                medicoes
        );
        return afericaoPlacaProtheus;
    }

    @NotNull
    public static AfericaoAvulsaProtheusNepomuceno convert(@NotNull final String codAuxiliarUnidade,
                                                           @NotNull final AfericaoAvulsa afericaoAvulsa) {
        // Separa o código de empresa e unidade do campo auxiliar.
        final String[] empresaUnidade = codAuxiliarUnidade.split(":");

        // Cria a variável que conterá a listagem de medições.
        // Apesar de ser usado um array na estrutura, este deverá conter apenas um índice.
        final List<MedicaoAfericaoProtheusNepomuceno> medicoes = new ArrayList<>();

        // Cria a variável do pneu aferido para facilitar a manipulação.
        final Pneu pneu = afericaoAvulsa.getPneuAferido();

        // Percorre a lista de pneus aferidos e cria a lista de objetos de medição.
        final MedicaoAfericaoProtheusNepomuceno medicao = new MedicaoAfericaoProtheusNepomuceno(
                pneu.getCodigoCliente(),
                pneu.getCodigo(),
                pneu.getVidaAtual(),
                pneu.getPressaoAtual(),
                pneu.getSulcosAtuais().getInterno(),
                pneu.getSulcosAtuais().getCentralInterno(),
                pneu.getSulcosAtuais().getCentralExterno(),
                pneu.getSulcosAtuais().getExterno()
        );
        medicoes.add(medicao);

        // Cria o objeto de aferição de placa que será enviado na integração.
        final AfericaoAvulsaProtheusNepomuceno afericaoPlacaProtheus = new AfericaoAvulsaProtheusNepomuceno(
                empresaUnidade[0],
                empresaUnidade[1],
                String.valueOf(afericaoAvulsa.getColaborador().getCpf()),
                afericaoAvulsa.getTempoRealizacaoAfericaoInMillis(),
                afericaoAvulsa.getDataHora(),
                afericaoAvulsa.getTipoMedicaoColetadaAfericao(),
                medicoes
        );
        return afericaoPlacaProtheus;
    }
}
