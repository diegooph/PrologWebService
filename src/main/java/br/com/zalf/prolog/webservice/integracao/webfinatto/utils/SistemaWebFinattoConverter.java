package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ModeloPlacasAfericao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class SistemaWebFinattoConverter {
    private static final String TAG = SistemaWebFinattoConverter.class.getSimpleName();

    private SistemaWebFinattoConverter() {
        throw new IllegalStateException(SistemaWebFinattoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static CronogramaAfericao createEmptyCronogramaAfericaoProlog() {
        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        cronogramaAfericao.setModelosPlacasAfericao(new ArrayList<>());
        // É necessário realizar as chamadas de cálculos para setar algumas variáveis.
        cronogramaAfericao.calcularQuatidadeSulcosPressaoOk(true);
        cronogramaAfericao.calcularTotalVeiculos();
        return cronogramaAfericao;
    }

    @NotNull
    public static CronogramaAfericao createCronogramaAfericaoProlog(
            @NotNull final List<VeiculoWebFinatto> veiculosByFiliais,
            @NotNull final UnidadeRestricaoHolder unidadeRestricaoHolder,
            @NotNull final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder,
            @NotNull final AfericaoRealizadaPlacaHolder afericaoRealizadaPlacaHolder) {
        final Map<String, ModeloPlacasAfericao> modelosEstruturaVeiculo = new HashMap<>();
        final Map<String, List<ModeloPlacasAfericao.PlacaAfericao>> placasEstruturaVeiculo = new HashMap<>();
        final Set<String> estruturasNaoMapeadas = new HashSet<>();
        for (final VeiculoWebFinatto veiculo : veiculosByFiliais) {
            if (!tipoVeiculoConfigAfericaoHolder.contains(veiculo.getCodEmpresaFilialVeiculo(),
                                                          veiculo.getCodEstruturaVeiculo())) {
                // Adicionamos a estrutura não mapeada em uma estrutura para logar no sentry.
                estruturasNaoMapeadas.add(veiculo.getCodEstruturaVeiculo());
                continue;
            }

            if (!placasEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                placasEstruturaVeiculo.put(veiculo.getCodModeloVeiculo(), new ArrayList<>());
            }
            final TipoVeiculoConfigAfericao configAfericao =
                    tipoVeiculoConfigAfericaoHolder.get(veiculo.getCodEmpresaFilialVeiculo(),
                                                        veiculo.getCodEstruturaVeiculo());
            placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo())
                    .add(createPlacaAfericaoProlog(veiculo,
                                                   unidadeRestricaoHolder.get(veiculo.getCodEmpresaFilialVeiculo()),
                                                   configAfericao,
                                                   afericaoRealizadaPlacaHolder.get(veiculo.getPlacaVeiculo())));
            if (!modelosEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                modelosEstruturaVeiculo.put(veiculo.getCodModeloVeiculo(),
                                            createModeloPlacasAfericaoProlog(
                                                    veiculo,
                                                    placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo())));
            }
        }

        if (!estruturasNaoMapeadas.isEmpty()) {
            logEstruturasNaoMapeadas(estruturasNaoMapeadas);
        }

        return internalCreateCronogramaAfericaoProlog(modelosEstruturaVeiculo);
    }

    @NotNull
    private static CronogramaAfericao internalCreateCronogramaAfericaoProlog(
            @NotNull final Map<String, ModeloPlacasAfericao> modelosEstruturaVeiculo) {
        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        final ArrayList<ModeloPlacasAfericao> modelosPlacasAfericao = new ArrayList<>(modelosEstruturaVeiculo.values());
        cronogramaAfericao.setModelosPlacasAfericao(modelosPlacasAfericao);
        cronogramaAfericao.calcularQuatidadeSulcosPressaoOk(true);
        cronogramaAfericao.calcularTotalVeiculos();
        cronogramaAfericao.removerModelosSemPlacas();
        cronogramaAfericao.removerPlacasNaoAferiveis();
        return cronogramaAfericao;
    }

    @NotNull
    private static ModeloPlacasAfericao.PlacaAfericao createPlacaAfericaoProlog(
            @NotNull final VeiculoWebFinatto veiculo,
            @NotNull final UnidadeRestricao unidadeRestricao,
            @NotNull final TipoVeiculoConfigAfericao tipoVeiculoConfigAfericao,
            @NotNull final AfericaoRealizadaPlaca afericaoRealizadaPlaca) {
        final ModeloPlacasAfericao.PlacaAfericao placaAfericao = new ModeloPlacasAfericao.PlacaAfericao();
        placaAfericao.setPlaca(veiculo.getPlacaVeiculo());
        placaAfericao.setIdentificadorFrota(veiculo.getCodigoFrota());

        placaAfericao.setIntervaloUltimaAfericaoPressao(afericaoRealizadaPlaca.getDiasUltimaAfericaoPressao());
        placaAfericao.setIntervaloUltimaAfericaoSulco(afericaoRealizadaPlaca.getDiasUltimaAfericaoSulco());
        placaAfericao.setQuantidadePneus(veiculo.getQtdPneusAplicadosVeiculo());

        placaAfericao.setFormaColetaDadosSulco(tipoVeiculoConfigAfericao.getFormaColetaDadosSulco());
        placaAfericao.setFormaColetaDadosPressao(tipoVeiculoConfigAfericao.getFormaColetaDadosPressao());
        placaAfericao.setFormaColetaDadosSulcoPressao(tipoVeiculoConfigAfericao.getFormaColetaDadosSulcoPressao());
        placaAfericao.setPodeAferirEstepe(tipoVeiculoConfigAfericao.isPodeAferirEstepes());

        placaAfericao.setMetaAfericaoSulco(unidadeRestricao.getPeriodoDiasAfericaoSulco());
        placaAfericao.setMetaAfericaoPressao(unidadeRestricao.getPeriodoDiasAfericaoPressao());
        placaAfericao.setCodUnidadePlaca(unidadeRestricao.getCodUnidade());
        return placaAfericao;
    }

    @NotNull
    private static ModeloPlacasAfericao createModeloPlacasAfericaoProlog(
            @NotNull final VeiculoWebFinatto veiculo,
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
            } else if (placa.isAfericaoPressaoNoPrazo(placa.getMetaAfericaoPressao())) {
                qtdModeloPressaoOk++;
            }
        }
        modeloPlacasAfericao.setQtdModeloSulcoOk(qtdModeloSulcoOk);
        modeloPlacasAfericao.setQtdModeloPressaoOk(qtdModeloPressaoOk);
        modeloPlacasAfericao.setQtdModeloSulcoPressaoOk(qtdModeloSulcoPressaoOk);
        modeloPlacasAfericao.setTotalVeiculosModelo(placasAfericao.size());
        return modeloPlacasAfericao;
    }

    private static void logEstruturasNaoMapeadas(@NotNull final Set<String> estruturasNaoMapeadas) {
        final String message = "Estruturas não mapeadas: " + estruturasNaoMapeadas;
        Log.i(TAG, message);
        ErrorReportSystem.logMessage(message);
    }
}
