package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils;

import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.*;

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
                                                          @NotNull final AfericaoPlaca afericaoPlaca,
                                                          @NotNull final ZoneId zoneId) {
        // Separa o código de empresa e unidade do campo auxiliar.
        final String[] empresaUnidade = codAuxiliarUnidade.split(DEFAULT_CODIGOS_SEPARATOR);

        final List<MedicaoAfericaoProtheusNepomuceno> medicoes = new ArrayList<>();
        for (final Pneu pneu : afericaoPlaca.getPneusAferidos()) {
            medicoes.add(createMedicaoAfericaoProtheusNepomuceno(pneu, afericaoPlaca.getTipoMedicaoColetadaAfericao()));
        }
        return new AfericaoPlacaProtheusNepomuceno(
                empresaUnidade[COD_EMPRESA_INDEX],
                empresaUnidade[COD_UNIDADE_INDEX],
                afericaoPlaca.getVeiculo().getPlaca(),
                Colaborador.formatCpf(afericaoPlaca.getColaborador().getCpf()),
                afericaoPlaca.getKmMomentoAfericao(),
                afericaoPlaca.getTempoRealizacaoAfericaoInMillis(),
                afericaoPlaca.getDataHora(),
                afericaoPlaca.getDataHora().atOffset(ZoneOffset.UTC).atZoneSameInstant(zoneId).toLocalDateTime(),
                afericaoPlaca.getTipoMedicaoColetadaAfericao(),
                medicoes);
    }

    @NotNull
    public static AfericaoAvulsaProtheusNepomuceno convert(@NotNull final String codAuxiliarUnidade,
                                                           @NotNull final AfericaoAvulsa afericaoAvulsa) {
        // Separa o código de empresa e unidade do campo auxiliar.
        final String[] empresaUnidade = codAuxiliarUnidade.split(DEFAULT_CODIGOS_SEPARATOR);
        return new AfericaoAvulsaProtheusNepomuceno(
                empresaUnidade[COD_EMPRESA_INDEX],
                empresaUnidade[COD_UNIDADE_INDEX],
                Colaborador.formatCpf(afericaoAvulsa.getColaborador().getCpf()),
                afericaoAvulsa.getTempoRealizacaoAfericaoInMillis(),
                afericaoAvulsa.getDataHora(),
                afericaoAvulsa.getTipoMedicaoColetadaAfericao(),
                Collections.singletonList(
                        createMedicaoAfericaoProtheusNepomuceno(
                                afericaoAvulsa.getPneuAferido(),
                                afericaoAvulsa.getTipoMedicaoColetadaAfericao())));
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
            @NotNull final InfosUnidadeRestricao infosUnidadeRestricao,
            @NotNull final InfosTipoVeiculoConfiguracaoAfericao infosTipoVeiculoConfiguracaoAfericao,
            @NotNull final InfosAfericaoRealizadaPlaca infosAfericaoRealizadaPlaca) {
        final ModeloPlacasAfericao.PlacaAfericao placaAfericao = new ModeloPlacasAfericao.PlacaAfericao();
        placaAfericao.setPlaca(veiculo.getPlacaVeiculo());
        if (!veiculo.getCodVeiculo().equals(veiculo.getPlacaVeiculo())) {
            placaAfericao.setIdentificadorFrota(veiculo.getCodVeiculo());
        }

        placaAfericao.setIntervaloUltimaAfericaoPressao(infosAfericaoRealizadaPlaca.getDiasUltimaAfericaoPressao());
        placaAfericao.setIntervaloUltimaAfericaoSulco(infosAfericaoRealizadaPlaca.getDiasUltimaAfericaoSulco());
        placaAfericao.setQuantidadePneus(veiculo.getQtdPneusAplicadosVeiculo());

        placaAfericao.setFormaColetaDadosSulco(infosTipoVeiculoConfiguracaoAfericao.getFormaColetaDadosSulco());
        placaAfericao.setFormaColetaDadosPressao(infosTipoVeiculoConfiguracaoAfericao.getFormaColetaDadosPressao());
        placaAfericao.setFormaColetaDadosSulcoPressao(infosTipoVeiculoConfiguracaoAfericao.getFormaColetaDadosSulcoPressao());
        placaAfericao.setPodeAferirEstepe(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirEstepes());

        placaAfericao.setMetaAfericaoSulco(infosUnidadeRestricao.getPeriodoDiasAfericaoSulco());
        placaAfericao.setMetaAfericaoPressao(infosUnidadeRestricao.getPeriodoDiasAfericaoPressao());
        placaAfericao.setCodUnidadePlaca(infosUnidadeRestricao.getCodUnidade());
        return placaAfericao;
    }

    @NotNull
    public static CronogramaAfericao createCronogramaAfericaoProlog(
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
    public static CronogramaAfericao createEmptyCronogramaAfericaoProlog() {
        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        cronogramaAfericao.setModelosPlacasAfericao(new ArrayList<>());
        // É necessário realizar as chamadas de cálculos para setar algumas variáveis.
        cronogramaAfericao.calcularQuatidadeSulcosPressaoOk(true);
        cronogramaAfericao.calcularTotalVeiculos();
        return cronogramaAfericao;
    }

    @NotNull
    public static Veiculo createVeiculoProlog(@NotNull final Long codUnidadeProlog,
                                              @NotNull final Short codDiagramaProlog,
                                              @NotNull final VeiculoAfericaoProtheusNepomuceno veiculoAfericao,
                                              @NotNull final ProtheusNepomucenoPosicaoPneuMapper posicaoPneuMapper) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(veiculoAfericao.getCodVeiculo());
        if (!veiculoAfericao.getCodVeiculo().equals(veiculoAfericao.getPlacaVeiculo())) {
            veiculo.setIdentificadorFrota(veiculoAfericao.getPlacaVeiculo());
        }
        veiculo.setKmAtual(veiculoAfericao.getKmAtualVeiculo());
        veiculo.setCodUnidadeAlocado(codUnidadeProlog);
        veiculo.setDiagrama(createDiagramaProlog(codDiagramaProlog, veiculoAfericao.getCodEstruturaVeiculo()));
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

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private static MedicaoAfericaoProtheusNepomuceno createMedicaoAfericaoProtheusNepomuceno(
            @NotNull final Pneu pneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
        switch (tipoMedicaoColetadaAfericao) {
            case SULCO:
                return new MedicaoAfericaoProtheusNepomuceno(
                        pneu.getCodigoCliente(),
                        pneu.getCodigo(),
                        pneu.getVidaAtual(),
                        PRESSAO_NAO_COLETADA,
                        pneu.getSulcosAtuais().getInterno(),
                        pneu.getSulcosAtuais().getCentralInterno(),
                        pneu.getSulcosAtuais().getCentralExterno(),
                        pneu.getSulcosAtuais().getExterno());
            case PRESSAO:
                return new MedicaoAfericaoProtheusNepomuceno(
                        pneu.getCodigoCliente(),
                        pneu.getCodigo(),
                        pneu.getVidaAtual(),
                        pneu.getPressaoAtual(),
                        SULCO_NAO_COLETADO,
                        SULCO_NAO_COLETADO,
                        SULCO_NAO_COLETADO,
                        SULCO_NAO_COLETADO);
            case SULCO_PRESSAO:
                return new MedicaoAfericaoProtheusNepomuceno(
                        pneu.getCodigoCliente(),
                        pneu.getCodigo(),
                        pneu.getVidaAtual(),
                        pneu.getPressaoAtual(),
                        pneu.getSulcosAtuais().getInterno(),
                        pneu.getSulcosAtuais().getCentralInterno(),
                        pneu.getSulcosAtuais().getCentralExterno(),
                        pneu.getSulcosAtuais().getExterno());
            default:
                throw new IllegalStateException("Unexpected value: " + tipoMedicaoColetadaAfericao);
        }
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
        pneu.setVidasTotal(pneuEstoqueNepomuceno.getVidaTotalPneu());
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
                                                @NotNull final ProtheusNepomucenoPosicaoPneuMapper posicaoPneuMapper) {
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
                                         @NotNull final ProtheusNepomucenoPosicaoPneuMapper posicaoPneuMapper) {
        final Pneu pneu = new PneuComum();
        pneu.setCodigoCliente(pneuAplicado.getCodigoCliente());
        pneu.setCodigo(ProtheusNepomucenoEncoderDecoder.encode(pneuAplicado.getCodigoCliente()));
        pneu.setCodUnidadeAlocado(codUnidadeProlog);
        pneu.setVidaAtual(pneuAplicado.getVidaAtualPneu());
        pneu.setVidasTotal(pneuAplicado.getVidaTotalPneu());
        final Integer posicaoProlog = posicaoPneuMapper.mapPosicaoToProlog(pneuAplicado.getPosicaoAplicado());
        if (posicaoProlog == null || posicaoProlog <= 0) {
            // Antes de criar o pneu fazemos uma validação em todas as posições e identificamos se existe algo não
            // mapeado. É 'quase' impossível essa exception estourar, porém, preferimos pecar pelo excesso.
            throw new IllegalStateException("Posição de pneu não mapeada:\n" +
                    "posicaoNaoMapeada: " + pneuAplicado.getPosicaoAplicado() + "\n" +
                    "posicaoProlog: " + posicaoProlog);
        }
        pneu.setPosicao(posicaoProlog);
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
        modeloPneu.setNome(pneuAplicado.getNomeModeloPneu());
        modeloPneu.setQuantidadeSulcos(pneuAplicado.getQtdSulcosModeloPneu());
        pneu.setModelo(modeloPneu);
        if (pneuAplicado.isRecapado()) {
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(DEFAULT_COD_MODELO_BANDA);
            modeloBanda.setNome(pneuAplicado.getNomeModeloBanda());
            modeloBanda.setQuantidadeSulcos(pneuAplicado.getQtdSulcosModeloPneu());
            final Banda banda = new Banda();
            banda.setModelo(modeloBanda);
            pneu.setBanda(banda);
        }
        return pneu;
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
}
