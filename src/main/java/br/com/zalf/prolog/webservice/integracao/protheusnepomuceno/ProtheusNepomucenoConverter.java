package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoConstants.*;

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
        final String[] empresaUnidade = codAuxiliarUnidade.split(DEFAULT_EMPRESA_FILIAL_SEPARERTOR);

        final List<MedicaoAfericaoProtheusNepomuceno> medicoes = new ArrayList<>();
        for (Pneu pneu : afericaoPlaca.getPneusAferidos()) {
            //noinspection ConstantConditions
            medicoes.add(new MedicaoAfericaoProtheusNepomuceno(
                    pneu.getCodigoCliente(),
                    pneu.getCodigo(),
                    pneu.getVidaAtual(),
                    pneu.getPressaoAtual(),
                    pneu.getSulcosAtuais().getInterno(),
                    pneu.getSulcosAtuais().getCentralInterno(),
                    pneu.getSulcosAtuais().getCentralExterno(),
                    pneu.getSulcosAtuais().getExterno()));
        }
        return new AfericaoPlacaProtheusNepomuceno(
                empresaUnidade[COD_EMPRESA_INDEX],
                empresaUnidade[COD_UNIDADE_INDEX],
                afericaoPlaca.getVeiculo().getPlaca(),
                Colaborador.formatCpf(afericaoPlaca.getColaborador().getCpf()),
                afericaoPlaca.getKmMomentoAfericao(),
                afericaoPlaca.getTempoRealizacaoAfericaoInMillis(),
                afericaoPlaca.getDataHora(),
                afericaoPlaca.getTipoMedicaoColetadaAfericao(),
                medicoes);
    }

    @NotNull
    public static AfericaoAvulsaProtheusNepomuceno convert(@NotNull final String codAuxiliarUnidade,
                                                           @NotNull final AfericaoAvulsa afericaoAvulsa) {
        // Separa o código de empresa e unidade do campo auxiliar.
        final String[] empresaUnidade = codAuxiliarUnidade.split(DEFAULT_EMPRESA_FILIAL_SEPARERTOR);

        final Pneu pneu = afericaoAvulsa.getPneuAferido();
        //noinspection ConstantConditions
        final MedicaoAfericaoProtheusNepomuceno medicao = new MedicaoAfericaoProtheusNepomuceno(
                pneu.getCodigoCliente(),
                pneu.getCodigo(),
                pneu.getVidaAtual(),
                pneu.getPressaoAtual(),
                pneu.getSulcosAtuais().getInterno(),
                pneu.getSulcosAtuais().getCentralInterno(),
                pneu.getSulcosAtuais().getCentralExterno(),
                pneu.getSulcosAtuais().getExterno());
        return new AfericaoAvulsaProtheusNepomuceno(
                empresaUnidade[COD_EMPRESA_INDEX],
                empresaUnidade[COD_UNIDADE_INDEX],
                Colaborador.formatCpf(afericaoAvulsa.getColaborador().getCpf()),
                afericaoAvulsa.getTempoRealizacaoAfericaoInMillis(),
                afericaoAvulsa.getDataHora(),
                afericaoAvulsa.getTipoMedicaoColetadaAfericao(),
                Collections.singletonList(medicao));
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
        placaAfericao.setQuantidadePneus(veiculo.getQtdPneusAplicadosVeiculo());

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

    @NotNull
    public static Veiculo createVeiculoProlog(@NotNull final Long codUnidadeProlog,
                                              @NotNull final Pair<Long, Short> codTipoVeiculoCodDiagramaProlog,
                                              @NotNull final VeiculoAfericaoProtheusNepomuceno veiculoAfericao,
                                              @NotNull final PosicaoPneuMapper posicaoPneuMapper) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(veiculoAfericao.getPlacaVeiculo());
        veiculo.setKmAtual(veiculoAfericao.getKmAtualVeiculo());
        veiculo.setCodUnidadeAlocado(codUnidadeProlog);
        veiculo.setDiagrama(createDiagramaProlog(codTipoVeiculoCodDiagramaProlog));
        veiculo.setListPneus(
                createPneusProlog(codUnidadeProlog, veiculoAfericao.getPneusAplicados(), posicaoPneuMapper));
        return veiculo;
    }

    @NotNull
    public static NovaAfericaoPlaca createNovaAfericaoPlacaProlog(
            @NotNull final Veiculo veiculo,
            @NotNull final ConfiguracaoNovaAfericaoPlaca configuracaoAfericao) {
        final NovaAfericaoPlaca novaAfericaoPlaca = new NovaAfericaoPlaca();
        novaAfericaoPlaca.setVeiculo(veiculo);
        novaAfericaoPlaca.setEstepesVeiculo(veiculo.getEstepes());
        novaAfericaoPlaca.setRestricao(Restricao.createRestricaoFrom(configuracaoAfericao));
        novaAfericaoPlaca.setVariacaoAceitaSulcoMaiorMilimetros(
                configuracaoAfericao.getVariacaoAceitaSulcoMaiorMilimetros());
        novaAfericaoPlaca.setVariacaoAceitaSulcoMenorMilimetros(
                configuracaoAfericao.getVariacaoAceitaSulcoMenorMilimetros());
        novaAfericaoPlaca.setBloqueiaValoresMaiores(configuracaoAfericao.isBloqueiaValoresMaiores());
        novaAfericaoPlaca.setBloqueiaValoresMenores(configuracaoAfericao.isBloqueiaValoresMenores());
        novaAfericaoPlaca.setDeveAferirEstepes(configuracaoAfericao.isPodeAferirEstepe());
        return novaAfericaoPlaca;
    }

    @NotNull
    public static NovaAfericaoAvulsa createNovaAfericaoAvulsaProlog(
            @NotNull final Long codUnidadePneuAlocado,
            @NotNull final PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno,
            @NotNull final ConfiguracaoNovaAfericaoAvulsa configuracaoAfericao,
            @Nullable final InfosAfericaoAvulsa pneuInfoAfericaoAvulsa) {
        final NovaAfericaoAvulsa novaAfericaoAvulsa = new NovaAfericaoAvulsa();
        novaAfericaoAvulsa.setPneuParaAferir(ProtheusNepomucenoConverter
                .createPneuAfericaoAvulsaProlog(codUnidadePneuAlocado, pneuEstoqueNepomuceno, pneuInfoAfericaoAvulsa));
        novaAfericaoAvulsa.setRestricao(Restricao.createRestricaoFrom(configuracaoAfericao));
        novaAfericaoAvulsa.setBloqueiaValoresMaiores(configuracaoAfericao.isBloqueiaValoresMaiores());
        novaAfericaoAvulsa.setBloqueiaValoresMenores(configuracaoAfericao.isBloqueiaValoresMenores());
        novaAfericaoAvulsa.setVariacaoAceitaSulcoMaiorMilimetros(
                configuracaoAfericao.getVariacaoAceitaSulcoMaiorMilimetros());
        novaAfericaoAvulsa.setVariacaoAceitaSulcoMenorMilimetros(
                configuracaoAfericao.getVariacaoAceitaSulcoMenorMilimetros());
        return novaAfericaoAvulsa;
    }

    @NotNull
    public static PneuAfericaoAvulsa createPneuAfericaoAvulsaProlog(
            @NotNull final Long codUnidadePneuAlocado,
            @NotNull final PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno,
            @Nullable final InfosAfericaoAvulsa pneuInfoAfericaoAvulsa) {
        final PneuAfericaoAvulsa pneuAfericaoAvulsa = new PneuAfericaoAvulsa();
        pneuAfericaoAvulsa.setPneu(createPneuEstoqueProlog(codUnidadePneuAlocado, pneuEstoqueNepomuceno));
        if (pneuInfoAfericaoAvulsa != null) {
            pneuAfericaoAvulsa.setDataHoraUltimaAfericao(pneuInfoAfericaoAvulsa.getDataHoraUltimaAfericao());
            pneuAfericaoAvulsa.setNomeColaboradorAfericao(pneuInfoAfericaoAvulsa.getNomeColaboradorAfericao());
            pneuAfericaoAvulsa.setTipoMedicaoColetadaUltimaAfericao(
                    pneuInfoAfericaoAvulsa.getTipoMedicaoColetadaAfericao());
            pneuAfericaoAvulsa.setCodigoUltimaAfericao(pneuInfoAfericaoAvulsa.getCodUltimaAfericao());
            pneuAfericaoAvulsa.setTipoProcessoAfericao(pneuInfoAfericaoAvulsa.getTipoProcessoColetaAfericao());
            pneuAfericaoAvulsa.setPlacaAplicadoQuandoAferido(pneuInfoAfericaoAvulsa.getPlacaAplicadoQuandoAferido());
        }
        return pneuAfericaoAvulsa;
    }

    @NotNull
    private static PneuEstoque createPneuEstoqueProlog(
            @NotNull final Long codUnidadePneuAlocado,
            @NotNull final PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno) {
        final PneuEstoque pneu = new PneuEstoque();
        pneu.setCodigo(ProtheusNepomucenoEncoderDecoder.encode(pneuEstoqueNepomuceno.getCodigoCliente()));
        pneu.setCodigoCliente(pneuEstoqueNepomuceno.getCodigoCliente());
        pneu.setPressaoCorreta(pneuEstoqueNepomuceno.getPressaoRecomendadaPneu());
        pneu.setPressaoAtual(pneuEstoqueNepomuceno.getPressaoAtualPneu());
        pneu.setVidaAtual(pneuEstoqueNepomuceno.getVidaAtualPneu());
        pneu.setVidasTotal(10);
        pneu.setCodUnidadeAlocado(codUnidadePneuAlocado);
        pneu.setDimensao(new Pneu.Dimensao());

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(DEFAULT_COD_MARCA_PNEU);
        marcaPneu.setNome("");
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(DEFAULT_COD_MODELO_PNEU);
        modeloPneu.setNome(pneuEstoqueNepomuceno.getNomeModeloPneu());
        modeloPneu.setQuantidadeSulcos(pneuEstoqueNepomuceno.getQtdSulcosModeloPneu());
        pneu.setModelo(modeloPneu);

        if (pneuEstoqueNepomuceno.isRecapado()) {
            final Banda banda = new Banda();
            final Marca marcaBanda = new Marca();
            marcaBanda.setCodigo(DEFAULT_COD_MARCA_PNEU);
            marcaBanda.setNome("");
            banda.setMarca(marcaBanda);

            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(DEFAULT_COD_MARCA_PNEU);
            modeloBanda.setNome(pneuEstoqueNepomuceno.getNomeModeloBanda());
            //noinspection ConstantConditions
            modeloBanda.setQuantidadeSulcos(pneuEstoqueNepomuceno.getQtdSulcosModeloBanda());
            banda.setModelo(modeloBanda);
            pneu.setBanda(banda);
        } else {
            // Caso o pneu é novo então as informações da Banda são reflexo da Marca e Modelo do pneu.
            final Banda banda = new Banda();
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(modeloPneu.getCodigo());
            modeloBanda.setNome(modeloPneu.getNome());
            modeloBanda.setQuantidadeSulcos(modeloPneu.getQuantidadeSulcos());
            banda.setModelo(modeloBanda);
            banda.setMarca(marcaPneu);
            pneu.setBanda(banda);
        }

        final Sulcos sulcos = new Sulcos();
        sulcos.setInterno(pneuEstoqueNepomuceno.getSulcoInternoPneu());
        sulcos.setCentralInterno(pneuEstoqueNepomuceno.getSulcoCentralInternoPneu());
        sulcos.setCentralExterno(pneuEstoqueNepomuceno.getSulcoCentralExternoPneu());
        sulcos.setExterno(pneuEstoqueNepomuceno.getSulcoExternoPneu());
        pneu.setSulcosAtuais(sulcos);
        return pneu;
    }

    @NotNull
    private static List<Pneu> createPneusProlog(@NotNull final Long codUnidadeProlog,
                                                @NotNull final List<PneuAplicadoProtheusNepomuceno> pneusAplicados,
                                                @NotNull final PosicaoPneuMapper posicaoPneuMapper) {
        final List<Pneu> pneus = new ArrayList<>();
        for (final PneuAplicadoProtheusNepomuceno pneuAplicado : pneusAplicados) {
            pneus.add(createPneuProlog(codUnidadeProlog, pneuAplicado, posicaoPneuMapper));
        }
        pneus.sort(Pneu.POSICAO_PNEU_COMPARATOR);
        return pneus;
    }

    @NotNull
    private static Pneu createPneuProlog(@NotNull final Long codUnidadeProlog,
                                         @NotNull final PneuAplicadoProtheusNepomuceno pneuAplicado,
                                         @NotNull final PosicaoPneuMapper posicaoPneuMapper) {
        final Pneu pneu = new PneuComum();
        pneu.setCodigoCliente(pneuAplicado.getCodigoCliente());
        pneu.setCodigo(ProtheusNepomucenoEncoderDecoder.encode(pneuAplicado.getCodigoCliente()));
        pneu.setCodUnidadeAlocado(codUnidadeProlog);
        pneu.setVidaAtual(pneuAplicado.getVidaAtualPneu());
        pneu.setVidasTotal(pneuAplicado.getVidaTotalPneu());
        pneu.setPosicao(posicaoPneuMapper.mapToProLog(pneuAplicado.getPosicaoAplicado()));
        pneu.setPressaoAtual(pneuAplicado.getPressaoAtualPneu());
        pneu.setPressaoCorreta(pneuAplicado.getPressaoRecomendadaPneu());
        final Sulcos sulcosAtuais = new Sulcos();
        sulcosAtuais.setInterno(pneuAplicado.getSulcoInternoPneu());
        sulcosAtuais.setCentralInterno(pneuAplicado.getSulcoCentralInternoPneu());
        sulcosAtuais.setCentralExterno(pneuAplicado.getSulcoCentralExternoPneu());
        sulcosAtuais.setExterno(pneuAplicado.getSulcoExternoPneu());
        pneu.setSulcosAtuais(sulcosAtuais);
        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(DEFAULT_COD_MODELO_PNEU);
        modeloPneu.setQuantidadeSulcos(pneuAplicado.getQtdSulcosModeloPneu());
        pneu.setModelo(modeloPneu);
        if (pneuAplicado.isRecapado()) {
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(DEFAULT_COD_MODELO_BANDA);
            modeloBanda.setQuantidadeSulcos(pneuAplicado.getQtdSulcosModeloPneu());
            final Banda banda = new Banda();
            banda.setModelo(modeloBanda);
            pneu.setBanda(banda);
        }
        return pneu;
    }

    @NotNull
    private static DiagramaVeiculo createDiagramaProlog(
            @NotNull final Pair<Long, Short> codTipoVeiculoCodDiagramaProlog) {
        return new DiagramaVeiculo(
                codTipoVeiculoCodDiagramaProlog.getRight(),
                String.valueOf(codTipoVeiculoCodDiagramaProlog.getLeft()),
                new HashSet<>(),
                "");
    }
}
