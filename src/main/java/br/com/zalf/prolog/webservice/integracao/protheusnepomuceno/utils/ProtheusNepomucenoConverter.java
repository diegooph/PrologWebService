package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.IntegracaoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.integrador._model.AfericaoRealizadaAvulsa;
import br.com.zalf.prolog.webservice.integracao.integrador._model.AfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.integrador._model.TipoVeiculoConfigAfericao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeRestricao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.CamposPersonalizadosResposta;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.DeParaCamposPersonalizadosEnum;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.InspecaoRemovidoRealizada;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.PneuListagemInspecaoRemovido;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ConfigIntegracaoNepomucenoLoader.getConfigIntegracaoNepomuceno;
import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.*;
import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoUtils.getDeParaCamposPersonalizados;

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
    public static InspecaoRemovidoRealizada convert(@NotNull final AfericaoAvulsa afericaoAvulsa,
                                                    @NotNull final ZoneId zoneId) {
        if (ListUtils.hasElements(afericaoAvulsa.getRespostasCamposPersonalizados())) {
            final Map<DeParaCamposPersonalizadosEnum, Long> deParaCamposPersonalizados =
                    getDeParaCamposPersonalizados();
            final Map<DeParaCamposPersonalizadosEnum, String> respostasCamposPersonalizados = new HashMap<>();
            deParaCamposPersonalizados.keySet()
                    .forEach(key -> afericaoAvulsa.getRespostasCamposPersonalizados()
                            .stream()
                            .filter(campo -> campo.getCodCampo().equals(deParaCamposPersonalizados.get(key))
                                    && campo.temResposta())
                            .findFirst()
                            .ifPresent(campoPersonalizadoResposta ->
                                               respostasCamposPersonalizados.put(key,
                                                                                 campoPersonalizadoResposta.getRespostaAsString())));
            final String codigoDestinoPneu = getCodigoFromResposta(
                    respostasCamposPersonalizados.get(DeParaCamposPersonalizadosEnum.CODIGO_DESTINO_PNEU));
            final String codigoCausaSucataPneu = getCodigoFromRespostaIfExists(
                    respostasCamposPersonalizados.get(DeParaCamposPersonalizadosEnum.CODIGO_CAUSA_SUCATA_PNEU));

            if (codigoDestinoPneu.equals(getConfigIntegracaoNepomuceno().getCodSucataPneu())
                    && StringUtils.isNullOrEmpty(codigoCausaSucataPneu)) {
                throw new ProtheusNepomucenoException(
                        "É obrigatório fornecer a causa da sucata quando o DESTINO do pneu for D3");
            }

            return new InspecaoRemovidoRealizada(
                    afericaoAvulsa.getPneuAferido().getCodigoCliente(),
                    afericaoAvulsa.getPneuAferido().getCodigoCliente(),
                    afericaoAvulsa.getPneuAferido().getValorMenorSulcoAtual(),
                    afericaoAvulsa.getPneuAferido().getValorMaiorSulcoAtual(),
                    afericaoAvulsa.getPneuAferido().getPressaoAtual(),
                    getCodigoFromResposta(
                            respostasCamposPersonalizados.get(DeParaCamposPersonalizadosEnum.CODIGO_LIP_PNEU)),
                    getCodigoFromResposta(
                            respostasCamposPersonalizados.get(DeParaCamposPersonalizadosEnum.CODIGO_ORIGEM_FILIAL)),
                    codigoDestinoPneu,
                    codigoCausaSucataPneu,
                    afericaoAvulsa.getDataHora(),
                    afericaoAvulsa.getDataHora()
                            .atOffset(ZoneOffset.UTC)
                            .atZoneSameInstant(zoneId)
                            .toLocalDateTime(),
                    afericaoAvulsa.getColaborador().getCpfAsString(),
                    afericaoAvulsa.getColaborador().getNome(),
                    respostasCamposPersonalizados.get(DeParaCamposPersonalizadosEnum.OBSERVACAO));
        }
        throw new ProtheusNepomucenoException("Os campos personalizados devem ser preenchidos");
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
            @NotNull final UnidadeRestricao unidadeRestricao,
            @NotNull final TipoVeiculoConfigAfericao tipoVeiculoConfigAfericao,
            @NotNull final AfericaoRealizadaPlaca afericaoRealizadaPlaca) {
        final ModeloPlacasAfericao.PlacaAfericao placaAfericao = new ModeloPlacasAfericao.PlacaAfericao();
        placaAfericao.setCodigoVeiculo(-1L);
        placaAfericao.setPlaca(veiculo.getPlacaVeiculo());
        if (!veiculo.getCodVeiculo().equals(veiculo.getPlacaVeiculo())) {
            placaAfericao.setIdentificadorFrota(veiculo.getCodVeiculo());
        }

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
                                              @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodigo(-1L);
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
            @NotNull final PneuListagemInspecaoRemovido pneuInspecaoRemovido,
            @NotNull final ConfiguracaoNovaAfericaoAvulsa configuracaoAfericao,
            @Nullable final AfericaoRealizadaAvulsa pneuInfoAfericaoAvulsa) {
        final NovaAfericaoAvulsa novaAfericaoAvulsa = new NovaAfericaoAvulsa();
        novaAfericaoAvulsa.setPneuParaAferir(
                ProtheusNepomucenoConverter.createPneuAfericaoAvulsaProlog(codUnidadePneuAlocado,
                                                                           pneuInspecaoRemovido,
                                                                           pneuInfoAfericaoAvulsa));
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
            @NotNull final PneuListagemInspecaoRemovido pneuInspecaoRemovido,
            @Nullable final AfericaoRealizadaAvulsa pneuInfoAfericaoAvulsa) {
        final PneuAfericaoAvulsa pneuAfericaoAvulsa = new PneuAfericaoAvulsa();
        pneuAfericaoAvulsa.setPneu(createPneuEstoqueProlog(codUnidadePneuAlocado, pneuInspecaoRemovido));
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
    public static CampoPersonalizadoParaRealizacao createCampoPersonalizado(
            @NotNull final CampoPersonalizadoParaRealizacao campoPersonalizado,
            @NotNull final List<? extends CamposPersonalizadosResposta> resposta) {
        return new CampoPersonalizadoParaRealizacao(
                campoPersonalizado.getCodigo(),
                campoPersonalizado.getCodEmpresa(),
                campoPersonalizado.getCodFuncaoProlog(),
                campoPersonalizado.getTipoCampo(),
                campoPersonalizado.getNomeCampo(),
                campoPersonalizado.getDescricaoCampo(),
                campoPersonalizado.getTextoAuxilioPreenchimento(),
                campoPersonalizado.isPreenchimentoObrigatorio(),
                campoPersonalizado.getMensagemCasoCampoNaoPreenchido(),
                campoPersonalizado.getPermiteSelecaoMultipla(),
                resposta.stream().map(CamposPersonalizadosResposta::getRespostaFormatada).toArray(String[]::new),
                campoPersonalizado.getOrdemExibicao());
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
            @NotNull final PneuListagemInspecaoRemovido pneuInspecaoRemovido) {
        final PneuEstoque pneu = new PneuEstoque();
        pneu.setCodigo(ProtheusNepomucenoEncoderDecoder.encode(pneuInspecaoRemovido.getCodigoCliente()));
        pneu.setCodigoCliente(pneuInspecaoRemovido.getCodigoCliente());
        pneu.setPressaoCorreta(pneuInspecaoRemovido.getPressaoRecomendadaPneu());
        pneu.setPressaoAtual(pneuInspecaoRemovido.getPressaoAtualPneu());
        pneu.setVidaAtual(pneuInspecaoRemovido.getVidaAtualPneu());
        pneu.setVidasTotal(pneuInspecaoRemovido.getVidaTotalPneu());
        pneu.setCodUnidadeAlocado(codUnidadePneuAlocado);
        pneu.setDimensao(new Pneu.Dimensao());

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(DEFAULT_COD_MARCA_PNEU);
        marcaPneu.setNome("");
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(DEFAULT_COD_MODELO_PNEU);
        modeloPneu.setNome(pneuInspecaoRemovido.getNomeModeloPneu());
        modeloPneu.setQuantidadeSulcos(pneuInspecaoRemovido.getQtdSulcosModeloPneu());
        pneu.setModelo(modeloPneu);

        if (pneuInspecaoRemovido.isRecapado()) {
            final Banda banda = new Banda();
            final Marca marcaBanda = new Marca();
            marcaBanda.setCodigo(DEFAULT_COD_MARCA_PNEU);
            marcaBanda.setNome("");
            banda.setMarca(marcaBanda);

            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(DEFAULT_COD_MARCA_PNEU);
            modeloBanda.setNome(pneuInspecaoRemovido.getNomeModeloBanda());
            //noinspection ConstantConditions
            modeloBanda.setQuantidadeSulcos(pneuInspecaoRemovido.getQtdSulcosModeloBanda());
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
        sulcos.setInterno(pneuInspecaoRemovido.getSulcoInternoPneu());
        sulcos.setCentralInterno(pneuInspecaoRemovido.getSulcoCentralInternoPneu());
        sulcos.setCentralExterno(pneuInspecaoRemovido.getSulcoCentralExternoPneu());
        sulcos.setExterno(pneuInspecaoRemovido.getSulcoExternoPneu());
        pneu.setSulcosAtuais(sulcos);
        return pneu;
    }

    @NotNull
    private static List<Pneu> createPneusProlog(@NotNull final Long codUnidadeProlog,
                                                @NotNull final List<PneuAplicadoProtheusNepomuceno> pneusAplicados,
                                                @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
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
                                         @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
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

    @NotNull
    private static String getCodigoFromResposta(@NotNull final String resposta) {
        final String[] respostaSplitted = resposta.split(DEFAULT_COD_RESPOSTA_SEPARATOR);
        if (respostaSplitted.length > 0) {
            return respostaSplitted[0].trim();
        }
        throw new ProtheusNepomucenoException("A resposta não contém código!");
    }

    @Nullable
    private static String getCodigoFromRespostaIfExists(@Nullable final String resposta) {
        if (resposta == null) {
            return null;
        }
        return getCodigoFromResposta(resposta);
    }
}
