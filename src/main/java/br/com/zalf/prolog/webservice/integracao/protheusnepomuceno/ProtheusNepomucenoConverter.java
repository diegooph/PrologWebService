package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ModeloPlacasAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 3/10/20
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
                TipoMedicaoAfericaoProtheusNepomuceno.fromString(afericaoPlaca.getTipoMedicaoColetadaAfericao().asString()),
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
                TipoMedicaoAfericaoProtheusNepomuceno.fromString(afericaoAvulsa.getTipoMedicaoColetadaAfericao().asString()),
                medicoes
        );
        return afericaoPlacaProtheus;
    }

    @NotNull
    public static ModeloPlacasAfericao createModeloPlacasAfericaoProlog(
            @NotNull final VeiculoListagemProtheusNepomuceno veiculo,
            @NotNull final List<ModeloPlacasAfericao.PlacaAfericao> placasAfericao) {
        final ModeloPlacasAfericao modeloPlacasAfericao = new ModeloPlacasAfericao();
        modeloPlacasAfericao.setNomeModelo(veiculo.getNomeModeloVeiculo());
        modeloPlacasAfericao.setPlacasAfericao(placasAfericao);
        int qtdModeloSulcoOk = 0;
        int qtdModeloPressaoOk = 0;
        int qtdModeloSulcoPressaoOk = 0;
        for (final ModeloPlacasAfericao.PlacaAfericao placa : placasAfericao) {
            if (placa.isAfericaoPressaoNoPrazo(placa.getMetaAfericaoPressao())
                    && placa.isAfericaoSulcoNoPrazo(placa.getMetaAfericaoSulco())) {
                qtdModeloSulcoPressaoOk++;
                qtdModeloPressaoOk++;
                qtdModeloSulcoOk++;
            } else if (placa.isAfericaoSulcoNoPrazo(placa.getMetaAfericaoSulco())) {
                qtdModeloSulcoOk++;
            } else {
                qtdModeloPressaoOk++;
            }
        }
        modeloPlacasAfericao.setQtdModeloSulcoOk(qtdModeloSulcoOk);
        modeloPlacasAfericao.setQtdModeloPressaoOk(qtdModeloPressaoOk);
        modeloPlacasAfericao.setQtdModeloSulcoPressaoOk(qtdModeloSulcoPressaoOk);
        modeloPlacasAfericao.setTotalVeiculosModelo(placasAfericao.size());
        return modeloPlacasAfericao;
    }

    @NotNull
    public static ModeloPlacasAfericao.PlacaAfericao createPlacaAfericaoProlog(
            @NotNull final VeiculoListagemProtheusNepomuceno veiculo,
            @NotNull final Map<String, InfosUnidadeRestricao> unidadeRestricao,
            @NotNull final Map<String, InfosTipoVeiculoConfiguracaoAfericao> tipoVeiculoConfiguracao,
            @NotNull final Map<String, InfosAfericaoRealizadaPlaca> afericaoRealizadaPlaca) {
        final ModeloPlacasAfericao.PlacaAfericao placaAfericao = new ModeloPlacasAfericao.PlacaAfericao();
        placaAfericao.setPlaca(veiculo.getPlacaVeiculo());
        final InfosAfericaoRealizadaPlaca infosAfericaoRealizadaPlaca =
                afericaoRealizadaPlaca.get(veiculo.getPlacaVeiculo());
        placaAfericao.setIntervaloUltimaAfericaoPressao(infosAfericaoRealizadaPlaca.getDiasUltimaAfericaoPressao());
        placaAfericao.setIntervaloUltimaAfericaoSulco(infosAfericaoRealizadaPlaca.getDiasUltimaAfericaoSulco());
        placaAfericao.setQuantidadePneus(veiculo.getQtsPneusAplicadosVeiculo());
        final InfosTipoVeiculoConfiguracaoAfericao infosTipoVeiculoConfiguracaoAfericao =
                tipoVeiculoConfiguracao.get(veiculo.getCodEstruturaVeiculo());
        placaAfericao.setPodeAferirSulco(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirSulco());
        placaAfericao.setPodeAferirPressao(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirPressao());
        placaAfericao.setPodeAferirSulcoPressao(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirSulcoPressao());
        placaAfericao.setPodeAferirEstepe(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirEstepes());
        final InfosUnidadeRestricao infosUnidadeRestricao = unidadeRestricao.get(veiculo.getCodEmpresaFilialVeiculo());
        placaAfericao.setMetaAfericaoSulco(infosUnidadeRestricao.getPeriodoDiasAfericaoSulco());
        placaAfericao.setMetaAfericaoPressao(infosUnidadeRestricao.getPeriodoDiasAfericaoPressao());
        return placaAfericao;
    }

    @NotNull
    public static CronogramaAfericao createCronogramaAfericaoProlog(
            @NotNull final Map<String, ModeloPlacasAfericao> modelosEstruturaVeiculo,
            final int totalVeiculosListagem) {
        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        final ArrayList<ModeloPlacasAfericao> modelosPlacasAfericao = new ArrayList<>(modelosEstruturaVeiculo.values());
        cronogramaAfericao.setModelosPlacasAfericao(modelosPlacasAfericao);
        int totalModelosSulcoOk = 0;
        int totalModelosPressaoOk = 0;
        int totalModelosSulcoPressaoOk = 0;
        for (final ModeloPlacasAfericao modeloPlacasAfericao : modelosPlacasAfericao) {
            totalModelosSulcoOk = totalModelosSulcoOk + modeloPlacasAfericao.getQtdModeloSulcoOk();
            totalModelosPressaoOk = totalModelosPressaoOk + modeloPlacasAfericao.getQtdModeloPressaoOk();
            totalModelosSulcoPressaoOk = totalModelosSulcoPressaoOk + modeloPlacasAfericao.getQtdModeloSulcoPressaoOk();
        }
        cronogramaAfericao.setTotalSulcosOk(totalModelosSulcoOk);
        cronogramaAfericao.setTotalPressaoOk(totalModelosPressaoOk);
        cronogramaAfericao.setTotalSulcoPressaoOk(totalModelosSulcoPressaoOk);
        cronogramaAfericao.setTotalVeiculos(totalVeiculosListagem);
        return cronogramaAfericao;
    }
}
