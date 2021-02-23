package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ModeloPlacasAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.NovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.IntegracaoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.PneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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
    public static NovaAfericaoPlaca createNovaAfericaoPlacaProlog(
            @NotNull final Long codUnidade,
            @NotNull final Short codDiagramaProlog,
            @NotNull final VeiculoWebFinatto veiculoByPlaca,
            @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper,
            @NotNull final ConfiguracaoNovaAfericaoPlaca configNovaAfericaoPlaca) {
        final Veiculo veiculo = createVeiculoProlog(codUnidade, codDiagramaProlog, veiculoByPlaca, posicaoPneuMapper);
        return internalCreateNovaAfericaoPlacaProlog(veiculo, configNovaAfericaoPlaca);
    }

    @NotNull
    private static NovaAfericaoPlaca internalCreateNovaAfericaoPlacaProlog(
            @NotNull final Veiculo veiculo,
            @NotNull final ConfiguracaoNovaAfericaoPlaca configNovaAfericaoPlaca) {
        final NovaAfericaoPlaca novaAfericaoPlaca = new NovaAfericaoPlaca();
        novaAfericaoPlaca.setVeiculo(veiculo);
        novaAfericaoPlaca.setEstepesVeiculo(veiculo.getEstepes());
        novaAfericaoPlaca.setRestricao(Restricao.createRestricaoFrom(configNovaAfericaoPlaca));
        novaAfericaoPlaca.setVariacaoAceitaSulcoMaiorMilimetros(
                configNovaAfericaoPlaca.getVariacaoAceitaSulcoMaiorMilimetros());
        novaAfericaoPlaca.setVariacaoAceitaSulcoMenorMilimetros(
                configNovaAfericaoPlaca.getVariacaoAceitaSulcoMenorMilimetros());
        novaAfericaoPlaca.setBloqueiaValoresMaiores(configNovaAfericaoPlaca.isBloqueiaValoresMaiores());
        novaAfericaoPlaca.setBloqueiaValoresMenores(configNovaAfericaoPlaca.isBloqueiaValoresMenores());
        novaAfericaoPlaca.setDeveAferirEstepes(configNovaAfericaoPlaca.isPodeAferirEstepe());
        return novaAfericaoPlaca;
    }

    @NotNull
    private static Veiculo createVeiculoProlog(@NotNull final Long codUnidadeProlog,
                                               @NotNull final Short codDiagramaProlog,
                                               @NotNull final VeiculoWebFinatto veiculoByPlaca,
                                               @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodigo(Long.parseLong(veiculoByPlaca.getCodVeiculo()));
        veiculo.setPlaca(veiculoByPlaca.getPlacaVeiculo());
        veiculo.setIdentificadorFrota(veiculoByPlaca.getCodigoFrota());
        veiculo.setKmAtual(veiculoByPlaca.getKmAtualVeiculo());
        veiculo.setCodUnidadeAlocado(codUnidadeProlog);
        veiculo.setDiagrama(createDiagramaProlog(codDiagramaProlog, veiculoByPlaca.getCodEstruturaVeiculo()));
        veiculo.setListPneus(createPneusProlog(codUnidadeProlog,
                                               veiculoByPlaca.getPneusAplicados(),
                                               posicaoPneuMapper));
        return veiculo;
    }

    @NotNull
    private static List<Pneu> createPneusProlog(@NotNull final Long codUnidadeProlog,
                                                @NotNull final List<PneuWebFinatto> pneusAplicados,
                                                @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
        return pneusAplicados.stream()
                .map(pneuAplicado -> createPneuProlog(codUnidadeProlog, pneuAplicado, posicaoPneuMapper))
                .sorted(Pneu.POSICAO_PNEU_COMPARATOR)
                .collect(Collectors.toList());
    }

    @NotNull
    private static Pneu createPneuProlog(@NotNull final Long codUnidadeProlog,
                                         @NotNull final PneuWebFinatto pneuAplicado,
                                         @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
        final Pneu pneu = new PneuComum();
        pneu.setCodigoCliente(pneuAplicado.getCodigoCliente());
        pneu.setCodigo(Long.parseLong(pneuAplicado.getCodPneu()));
        pneu.setCodUnidadeAlocado(codUnidadeProlog);
        pneu.setVidaAtual(pneuAplicado.getVidaAtualPneu());
        pneu.setVidasTotal(pneuAplicado.getVidaTotalPneu());
        final Integer posicaoProlog = posicaoPneuMapper.mapPosicaoToProlog(pneuAplicado.getPosicaoAplicado());
        if (posicaoProlog == null || posicaoProlog <= 0) {
            // Antes de criar o pneu fazemos uma validação em todas as posições e identificamos se existe algo não
            // mapeado. É 'quase' impossível essa exception estourar, porém, preferimos pecar pelo excesso.
            throw new IllegalStateException("Posição de pneu não mapeada:" +
                                                    "\nposicaoNaoMapeada: " + pneuAplicado.getPosicaoAplicado() +
                                                    "\nposicaoProlog: " + posicaoProlog);
        }
        pneu.setPosicao(posicaoProlog);
        pneu.setPressaoAtual(pneuAplicado.getPressaoAtualPneuEmPsi());
        pneu.setPressaoCorreta(pneuAplicado.getPressaoRecomendadaPneuEmPsi());
        final Sulcos sulcosAtuais = new Sulcos();
        sulcosAtuais.setInterno(pneuAplicado.getSulcoInternoPneuEmMilimetros());
        sulcosAtuais.setCentralInterno(pneuAplicado.getSulcoCentralInternoPneuEmMilimetros());
        sulcosAtuais.setCentralExterno(pneuAplicado.getSulcoCentralExternoPneuEmMilimetros());
        sulcosAtuais.setExterno(pneuAplicado.getSulcoExternoPneuEmMilimetros());
        pneu.setSulcosAtuais(sulcosAtuais);
        pneu.setDot(pneuAplicado.getDotPneu());
        final Pneu.Dimensao dimensao = new Pneu.Dimensao();
        dimensao.setCodigo(pneuAplicado.getCodEstruturaPneu());
        dimensao.setAltura(pneuAplicado.getAlturaEstruturaPneu().intValue());
        dimensao.setLargura(pneuAplicado.getLarguraEstruturaPneu().intValue());
        dimensao.setAro(pneuAplicado.getAroEstruturaPneu());
        pneu.setDimensao(dimensao);
        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(Long.parseLong(pneuAplicado.getCodModeloBanda()));
        modeloPneu.setNome(pneuAplicado.getNomeModeloPneu());
        modeloPneu.setQuantidadeSulcos(pneuAplicado.getQtdSulcosModeloPneu());
        modeloPneu.setAlturaSulcos(pneuAplicado.getAlturaSulcosModeloPneuEmMilimetros().doubleValue());
        pneu.setModelo(modeloPneu);
        if (pneuAplicado.isRecapado()) {
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(Long.parseLong(pneuAplicado.getCodModeloBanda()));
            modeloBanda.setNome(pneuAplicado.getNomeModeloBanda());
            modeloBanda.setQuantidadeSulcos(pneuAplicado.getQtdSulcosModeloBanda());
            modeloBanda.setAlturaSulcos(pneuAplicado.getAlturaSulcosModeloBandaEmMilimetros().doubleValue());
            final Banda banda = new Banda();
            banda.setModelo(modeloBanda);
            pneu.setBanda(banda);
        }
        return pneu;
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

    @NotNull
    private static DiagramaVeiculo createDiagramaProlog(@NotNull final Short codDiagramaProlog,
                                                        @NotNull final String codEstruturaVeiculo) {
        return new DiagramaVeiculo(
                codDiagramaProlog,
                // Utilizamos a propriedade 'nome' como metadata para repassar o codEstruturaVeiculo.
                codEstruturaVeiculo,
                new HashSet<>(),
                "");
    }

    private static void logEstruturasNaoMapeadas(@NotNull final Set<String> estruturasNaoMapeadas) {
        final String message = "Estruturas não mapeadas: " + estruturasNaoMapeadas;
        Log.i(TAG, message);
        ErrorReportSystem.logMessage(message);
    }
}
