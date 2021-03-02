package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacaoPneu;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.integracao.IntegracaoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.EmpresaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.PneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.UnidadeWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.AfericaoPlacaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.AfericaoPneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.MedicaoAfericaoWebFinatto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class SistemaWebFinattoConverter {
    @NotNull
    private static final String TAG = SistemaWebFinattoConverter.class.getSimpleName();
    private static final double VALOR_NAO_COLETADO = 0.0;

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
    public static List<PneuAfericaoAvulsa> createPneusAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final List<PneuWebFinatto> pneusByFiliais,
            @NotNull final AfericaoRealizadaAvulsaHolder afericaoRealizadaAvulsaHolder) {
        final List<PneuAfericaoAvulsa> pneusAfericaoAvulsa = new ArrayList<>();
        for (final PneuWebFinatto pneuWebFinatto : pneusByFiliais) {
            final AfericaoRealizadaAvulsa pneuInfoAfericaoAvulsa =
                    afericaoRealizadaAvulsaHolder
                            .getAfericoesRealizadasAvulsas()
                            .stream()
                            .filter(infoPneu ->
                                            infoPneu.getCodPneuCliente().equals(pneuWebFinatto.getCodigoCliente()))
                            .findFirst()
                            .orElse(null);
            pneusAfericaoAvulsa.add(createPneuAfericaoAvulsaProlog(codUnidade,
                                                                   pneuWebFinatto,
                                                                   pneuInfoAfericaoAvulsa));
        }
        return pneusAfericaoAvulsa;
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
    public static NovaAfericaoAvulsa createNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final PneuWebFinatto pneuByCodigo,
            @NotNull final ConfiguracaoNovaAfericaoAvulsa configuracaoAfericao,
            @NotNull final AfericaoRealizadaAvulsaHolder afericaoRealizadaAvulsaHolder) {
        final AfericaoRealizadaAvulsa afericaoRealizadaAvulsa = afericaoRealizadaAvulsaHolder
                .getAfericoesRealizadasAvulsas()
                .stream()
                .filter(infoPneu -> infoPneu.getCodPneuCliente().equals(pneuByCodigo.getCodigoCliente()))
                .findFirst()
                .orElse(null);

        final NovaAfericaoAvulsa novaAfericaoAvulsa = new NovaAfericaoAvulsa();
        novaAfericaoAvulsa.setPneuParaAferir(createPneuAfericaoAvulsaProlog(codUnidade,
                                                                            pneuByCodigo,
                                                                            afericaoRealizadaAvulsa));
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
    public static List<VeiculoListagem> createVeiculosListagem(
            @NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
            @NotNull final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder,
            @NotNull final List<VeiculoWebFinatto> veiculosByFiliais) {
        final List<VeiculoListagem> veiculosListagem = new ArrayList<>();
        for (final VeiculoWebFinatto veiculo : veiculosByFiliais) {
            final UnidadeDePara unidadeDePara =
                    unidadeDeParaHolder.getByCodAuxiliar(veiculo.getCodEmpresaFilialVeiculo());
            if (unidadeDePara == null) {
                throw new IllegalStateException("Nenhum código de unidade mapeado para o código auxiliar:" +
                                                        "\ncodAuxiliar: " + veiculo.getCodEmpresaFilialVeiculo());
            }
            final TipoVeiculoConfigAfericao tipoVeiculoConfigAfericao =
                    tipoVeiculoConfigAfericaoHolder.get(veiculo.getCodEmpresaFilialVeiculo(),
                                                        veiculo.getCodEstruturaVeiculo());
            veiculosListagem.add(createVeiculoListagem(unidadeDePara, tipoVeiculoConfigAfericao, veiculo));
        }
        return veiculosListagem;
    }

    @NotNull
    public static VeiculoVisualizacao createVeiculoVisualizacao(
            @NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
            @NotNull final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder,
            @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper,
            @NotNull final VeiculoWebFinatto veiculo) {
        final UnidadeDePara unidadeDePara =
                unidadeDeParaHolder.getByCodAuxiliar(veiculo.getCodEmpresaFilialVeiculo());
        if (unidadeDePara == null) {
            throw new IllegalStateException("Nenhum código de unidade mapeado para o código auxiliar:" +
                                                    "\ncodAuxiliar: " + veiculo.getCodEmpresaFilialVeiculo());
        }
        final TipoVeiculoConfigAfericao tipoVeiculoConfigAfericao =
                tipoVeiculoConfigAfericaoHolder.get(veiculo.getCodEmpresaFilialVeiculo(),
                                                    veiculo.getCodEstruturaVeiculo());

        return new VeiculoVisualizacao(Long.valueOf(veiculo.getCodVeiculo()),
                                       veiculo.getPlacaVeiculo(),
                                       unidadeDePara.getCodUnidadeProlog(),
                                       unidadeDeParaHolder.getCodEmpresaProlog(),
                                       veiculo.getKmAtualVeiculo(),
                                       true,
                                       tipoVeiculoConfigAfericao.getCodTipoVeiculo(),
                                       Long.valueOf(veiculo.getCodModeloVeiculo()),
                                       Long.valueOf(tipoVeiculoConfigAfericao.getCodDiagramaVeiculo()),
                                       veiculo.getCodigoFrota(),
                                       unidadeDePara.getCodRegionalProlog(),
                                       veiculo.getNomeModeloVeiculo(),
                                       tipoVeiculoConfigAfericao.getNomeDiagramaVeiculo(),
                                       tipoVeiculoConfigAfericao.getQtdEixoDianteiro(),
                                       tipoVeiculoConfigAfericao.getQtdEixoTraseiro(),
                                       tipoVeiculoConfigAfericao.getNomeDiagramaVeiculo(),
                                       veiculo.getNomeMarcaVeiculo(),
                                       Long.valueOf(veiculo.getCodMarcaVeiculo()),
                                       true,
                                       false,
                                       createPneusVisualizacao(unidadeDePara,
                                                               posicaoPneuMapper,
                                                               veiculo,
                                                               veiculo.getPneusAplicados()),
                                       null);
    }

    @NotNull
    public static List<Pneu> createPneusByStatus(@NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
                                                 @NotNull final StatusPneu status,
                                                 @NotNull final List<PneuWebFinatto> pneusByFiliais) {
        final List<Pneu> pneus = new ArrayList<>();
        for (final PneuWebFinatto pneuWebFinatto : pneusByFiliais) {
            pneus.add(createPneuByStatus(unidadeDeParaHolder, status, pneuWebFinatto));
        }
        return pneus;
    }

    @NotNull
    private static Pneu createPneuByStatus(@NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
                                           @NotNull final StatusPneu status,
                                           @NotNull final PneuWebFinatto pneuWebFinatto) {
        final UnidadeDePara unidadeDePara = unidadeDeParaHolder.getByCodAuxiliar(pneuWebFinatto.getCodEmpresaFilial());
        final Pneu pneu = status.toPneuTipo().createNew();
        pneu.setCodigo(Long.valueOf(pneuWebFinatto.getCodPneu()));
        pneu.setCodigoCliente(pneuWebFinatto.getCodigoCliente());
        pneu.setDot(pneuWebFinatto.getDotPneu());
        pneu.setValor(new BigDecimal("0.0"));
        pneu.setCodUnidadeAlocado(unidadeDePara.getCodUnidadeProlog());
        pneu.setCodRegionalAlocado(unidadeDePara.getCodRegionalProlog());
        pneu.setNomeUnidadeAlocado(unidadeDePara.getNomeUnidadeProlog());
        pneu.setNomeRegionalAlocado(unidadeDePara.getNomeRegionalProlog());

        pneu.setPneuNovoNuncaRodado(false);

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(Long.parseLong(pneuWebFinatto.getCodMarcaPneu()));
        marcaPneu.setNome(pneuWebFinatto.getNomeMarcaPneu());
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(Long.valueOf(pneuWebFinatto.getCodModeloPneu()));
        modeloPneu.setNome(pneuWebFinatto.getNomeModeloPneu());
        modeloPneu.setQuantidadeSulcos(pneuWebFinatto.getQtdSulcosModeloPneu());
        modeloPneu.setAlturaSulcos(pneuWebFinatto.getAlturaSulcosModeloPneuEmMilimetros().doubleValue());
        pneu.setModelo(modeloPneu);

        if (pneuWebFinatto.isRecapado()) {
            final Banda banda = new Banda();
            final Marca marcaBanda = new Marca();
            marcaBanda.setCodigo(Long.valueOf(pneuWebFinatto.getCodMarcaBanda()));
            marcaBanda.setNome(pneuWebFinatto.getNomeMarcaBanda());
            banda.setMarca(marcaBanda);
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(Long.valueOf(pneuWebFinatto.getCodModeloBanda()));
            modeloBanda.setNome(pneuWebFinatto.getNomeModeloBanda());
            modeloBanda.setQuantidadeSulcos(pneuWebFinatto.getQtdSulcosModeloBanda());
            modeloBanda.setAlturaSulcos(pneuWebFinatto.getAlturaSulcosModeloBandaEmMilimetros().doubleValue());
            banda.setModelo(modeloBanda);
            banda.setValor(new BigDecimal("0.0"));
            pneu.setBanda(banda);
        } else {
            final Banda banda = new Banda();
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setQuantidadeSulcos(pneu.getModelo().getQuantidadeSulcos());
            modeloBanda.setCodigo(pneu.getModelo().getCodigo());
            modeloBanda.setNome(pneu.getModelo().getNome());
            modeloBanda.setAlturaSulcos(pneu.getModelo().getAlturaSulcos());
            banda.setModelo(modeloBanda);
            banda.setMarca(pneu.getMarca());
            pneu.setBanda(banda);
        }

        final PneuComum.Dimensao dimensao = new PneuComum.Dimensao();
        dimensao.codigo = pneuWebFinatto.getCodEstruturaPneu();
        dimensao.altura = pneuWebFinatto.getAlturaEstruturaPneu().intValue();
        dimensao.largura = pneuWebFinatto.getLarguraEstruturaPneu().intValue();
        dimensao.aro = pneuWebFinatto.getAroEstruturaPneu();
        pneu.setDimensao(dimensao);

        final Sulcos sulcosAtuais = new Sulcos();
        sulcosAtuais.setCentralInterno(pneuWebFinatto.getSulcoCentralInternoPneuEmMilimetros());
        sulcosAtuais.setCentralExterno(pneuWebFinatto.getSulcoCentralExternoPneuEmMilimetros());
        sulcosAtuais.setExterno(pneuWebFinatto.getSulcoExternoPneuEmMilimetros());
        sulcosAtuais.setInterno(pneuWebFinatto.getSulcoInternoPneuEmMilimetros());
        pneu.setSulcosAtuais(sulcosAtuais);

        pneu.setPressaoCorreta(pneuWebFinatto.getPressaoRecomendadaPneuEmPsi());
        pneu.setPressaoAtual(pneuWebFinatto.getPressaoAtualPneuEmPsi());
        pneu.setStatus(status);
        pneu.setVidaAtual(pneuWebFinatto.getVidaAtualPneu());
        pneu.setVidasTotal(pneuWebFinatto.getVidaTotalPneu());

        return pneu;
    }

    @NotNull
    private static List<VeiculoVisualizacaoPneu> createPneusVisualizacao(
            @NotNull final UnidadeDePara unidadeDePara,
            @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper,
            @NotNull final VeiculoWebFinatto veiculo,
            @NotNull final List<PneuWebFinatto> pneusAplicados) {
        final List<VeiculoVisualizacaoPneu> pneusVisualizacao = new ArrayList<>();
        for (final PneuWebFinatto pneusAplicado : pneusAplicados) {
            pneusVisualizacao.add(createPneuVisualizacao(unidadeDePara, posicaoPneuMapper, veiculo, pneusAplicado));
        }
        return pneusVisualizacao;
    }

    @NotNull
    private static VeiculoVisualizacaoPneu createPneuVisualizacao(
            @NotNull final UnidadeDePara unidadeDePara,
            @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper,
            @NotNull final VeiculoWebFinatto veiculo,
            @NotNull final PneuWebFinatto pneuAplicado) {
        final Integer posicaoProlog = posicaoPneuMapper.mapPosicaoToProlog(pneuAplicado.getPosicaoAplicado());
        if (posicaoProlog == null || posicaoProlog <= 0) {
            // Antes de criar o pneu fazemos uma validação em todas as posições e identificamos se existe algo não
            // mapeado. É 'quase' impossível essa exception estourar, porém, preferimos pecar pelo excesso.
            throw new IllegalStateException("Posição de pneu não mapeada:" +
                                                    "\nposicaoNaoMapeada: " + pneuAplicado.getPosicaoAplicado() +
                                                    "\nposicaoProlog: " + posicaoProlog);
        }
        return new VeiculoVisualizacaoPneu(Long.valueOf(pneuAplicado.getCodPneu()),
                                           pneuAplicado.getCodigoCliente(),
                                           pneuAplicado.getNomeMarcaPneu(),
                                           Long.valueOf(pneuAplicado.getCodMarcaPneu()),
                                           unidadeDePara.getCodUnidadeProlog(),
                                           unidadeDePara.getCodRegionalProlog(),
                                           pneuAplicado.getPressaoAtualPneuEmPsi(),
                                           pneuAplicado.getVidaAtualPneu(),
                                           pneuAplicado.getVidaTotalPneu(),
                                           false,
                                           pneuAplicado.getNomeModeloPneu(),
                                           Long.valueOf(pneuAplicado.getCodModeloPneu()),
                                           pneuAplicado.getQtdSulcosModeloPneu(),
                                           pneuAplicado.getAlturaSulcosModeloPneuEmMilimetros().doubleValue(),
                                           pneuAplicado.getAlturaEstruturaPneu().intValue(),
                                           pneuAplicado.getLarguraEstruturaPneu().intValue(),
                                           pneuAplicado.getAroEstruturaPneu(),
                                           pneuAplicado.getCodEstruturaPneu(),
                                           pneuAplicado.getPressaoRecomendadaPneuEmPsi(),
                                           pneuAplicado.getSulcoCentralInternoPneuEmMilimetros(),
                                           pneuAplicado.getSulcoCentralExternoPneuEmMilimetros(),
                                           pneuAplicado.getSulcoInternoPneuEmMilimetros(),
                                           pneuAplicado.getSulcoExternoPneuEmMilimetros(),
                                           pneuAplicado.getDotPneu(),
                                           0.0,
                                           pneuAplicado.getCodModeloBanda() == null
                                                   ? null
                                                   : Long.valueOf(pneuAplicado.getCodModeloBanda()),
                                           pneuAplicado.getNomeModeloBanda(),
                                           pneuAplicado.getQtdSulcosModeloBanda() == null
                                                   ? 0
                                                   : pneuAplicado.getQtdSulcosModeloBanda(),
                                           pneuAplicado.getAlturaSulcosModeloBandaEmMilimetros() == null
                                                   ? null
                                                   : pneuAplicado.getAlturaSulcosModeloBandaEmMilimetros()
                                                           .doubleValue(),
                                           pneuAplicado.getCodMarcaBanda() == null
                                                   ? null
                                                   : Long.valueOf(pneuAplicado.getCodMarcaBanda()),
                                           pneuAplicado.getNomeMarcaBanda(),
                                           0.0,
                                           posicaoProlog,
                                           "DE",
                                           Long.valueOf(veiculo.getCodVeiculo()),
                                           veiculo.getPlacaVeiculo());
    }

    @NotNull
    public static AfericaoPlacaWebFinatto createAfericaoPlaca(@NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
                                                              @NotNull final ZoneId zoneId,
                                                              @NotNull final AfericaoPlaca afericaoPlaca) {
        final List<MedicaoAfericaoWebFinatto> medicoes = afericaoPlaca.getPneusAferidos()
                .stream()
                .map(pneu -> createMedicaoAfericao(pneu, afericaoPlaca.getTipoMedicaoColetadaAfericao()))
                .collect(Collectors.toList());
        final String[] codEmpresaFilial = unidadeDeParaHolder.getCodFiliais().split(":");
        return new AfericaoPlacaWebFinatto(
                codEmpresaFilial[0],
                codEmpresaFilial[1],
                afericaoPlaca.getVeiculo().getPlaca(),
                Colaborador.formatCpf(afericaoPlaca.getColaborador().getCpf()),
                afericaoPlaca.getKmMomentoAfericao(),
                afericaoPlaca.getTempoRealizacaoAfericaoInMillis(),
                afericaoPlaca.getDataHora(),
                afericaoPlaca.getDataHora().atOffset(ZoneOffset.UTC).atZoneSameInstant(zoneId).toLocalDateTime(),
                afericaoPlaca.getTipoMedicaoColetadaAfericao().asString(),
                medicoes);
    }

    @NotNull
    public static AfericaoPneuWebFinatto createAfericaoAvulsa(@NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
                                                              @NotNull final ZoneId zoneId,
                                                              @NotNull final AfericaoAvulsa afericaoAvulsa) {
        return new AfericaoPneuWebFinatto(
                unidadeDeParaHolder.getCodFiliais(),
                unidadeDeParaHolder.getCodFiliais(),
                Colaborador.formatCpf(afericaoAvulsa.getColaborador().getCpf()),
                afericaoAvulsa.getTempoRealizacaoAfericaoInMillis(),
                afericaoAvulsa.getDataHora(),
                afericaoAvulsa.getDataHora().atOffset(ZoneOffset.UTC).atZoneSameInstant(zoneId).toLocalDateTime(),
                afericaoAvulsa.getTipoMedicaoColetadaAfericao().asString(),
                Collections.singletonList(createMedicaoAfericao(afericaoAvulsa.getPneuAferido(),
                                                                afericaoAvulsa.getTipoMedicaoColetadaAfericao())));
    }

    @NotNull
    public static List<Empresa> createEmpresa(@NotNull final List<EmpresaWebFinatto> filtrosClientes) {
        final Empresa empresa = new Empresa();
        empresa.setCodigo(3L);
        empresa.setNome("Finatto");
        empresa.setListRegional(createRegionaisUnidades(filtrosClientes));
        return Collections.singletonList(empresa);
    }

    @NotNull
    private static List<Regional> createRegionaisUnidades(@NotNull final List<EmpresaWebFinatto> filtrosClientes) {
        final List<Regional> regionais = new ArrayList<>();
        for (final EmpresaWebFinatto empresaFinatto : filtrosClientes) {
            final List<Unidade> unidades = new ArrayList<>();
            for (final UnidadeWebFinatto unidadeFinatto : empresaFinatto.getUnidades()) {
                final Unidade unidade = new Unidade();
                unidade.setCodigo(unidadeFinatto.getIdUnidade());
                unidade.setNome(unidadeFinatto.getDescricaoUnidade());
                unidades.add(unidade);
            }

            final Regional regional = new Regional();
            regional.setCodigo(empresaFinatto.getIdCliente());
            regional.setNome(empresaFinatto.getNomeCliente());
            regional.setListUnidade(unidades);

            regionais.add(regional);
        }
        return regionais;
    }

    @NotNull
    private static VeiculoListagem createVeiculoListagem(@NotNull final UnidadeDePara unidadeDePara,
                                                         @NotNull final TipoVeiculoConfigAfericao tipoVeiculoConfigAfericao,
                                                         @NotNull final VeiculoWebFinatto veiculo) {
        final Long codVeiculo = WebFinattoEncoderDecoder.generateCodVeiculo(unidadeDePara.getCodUnidadeProlog(),
                                                                            Long.valueOf(veiculo.getCodVeiculo()));
        return new VeiculoListagem(codVeiculo,
                                   veiculo.getPlacaVeiculo(),
                                   unidadeDePara.getCodRegionalProlog(),
                                   unidadeDePara.getNomeRegionalProlog(),
                                   unidadeDePara.getCodUnidadeProlog(),
                                   unidadeDePara.getNomeUnidadeProlog(),
                                   veiculo.getKmAtualVeiculo(),
                                   true,
                                   tipoVeiculoConfigAfericao.getCodTipoVeiculo(),
                                   Long.valueOf(veiculo.getCodModeloVeiculo()),
                                   Long.valueOf(tipoVeiculoConfigAfericao.getCodDiagramaVeiculo()),
                                   veiculo.getCodigoFrota(),
                                   veiculo.getNomeModeloVeiculo(),
                                   tipoVeiculoConfigAfericao.getNomeDiagramaVeiculo(),
                                   tipoVeiculoConfigAfericao.getQtdEixoDianteiro(),
                                   tipoVeiculoConfigAfericao.getQtdEixoTraseiro(),
                                   tipoVeiculoConfigAfericao.getNomeDiagramaVeiculo(),
                                   veiculo.getNomeMarcaVeiculo(),
                                   Long.valueOf(veiculo.getCodMarcaVeiculo()),
                                   true,
                                   false,
                                   false,
                                   null);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private static MedicaoAfericaoWebFinatto createMedicaoAfericao(
            @NotNull final Pneu pneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
        switch (tipoMedicaoColetadaAfericao) {
            case SULCO:
                return new MedicaoAfericaoWebFinatto(
                        pneu.getCodigo().toString(),
                        pneu.getCodigoCliente(),
                        pneu.getVidaAtual(),
                        VALOR_NAO_COLETADO,
                        pneu.getSulcosAtuais().getInterno(),
                        pneu.getSulcosAtuais().getCentralInterno(),
                        pneu.getSulcosAtuais().getCentralExterno(),
                        pneu.getSulcosAtuais().getExterno());
            case PRESSAO:
                return new MedicaoAfericaoWebFinatto(
                        pneu.getCodigo().toString(),
                        pneu.getCodigoCliente(),
                        pneu.getVidaAtual(),
                        pneu.getPressaoAtual(),
                        VALOR_NAO_COLETADO,
                        VALOR_NAO_COLETADO,
                        VALOR_NAO_COLETADO,
                        VALOR_NAO_COLETADO);
            case SULCO_PRESSAO:
                return new MedicaoAfericaoWebFinatto(
                        pneu.getCodigo().toString(),
                        pneu.getCodigoCliente(),
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
    private static PneuAfericaoAvulsa createPneuAfericaoAvulsaProlog(
            @NotNull final Long codUnidade,
            @NotNull final PneuWebFinatto pneuWebFinatto,
            @Nullable final AfericaoRealizadaAvulsa pneuInfoAfericaoAvulsa) {
        final PneuAfericaoAvulsa pneuAfericaoAvulsa = new PneuAfericaoAvulsa();
        pneuAfericaoAvulsa.setPneu(createPneuEstoqueProlog(codUnidade, pneuWebFinatto));
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
    private static Pneu createPneuEstoqueProlog(
            @NotNull final Long codUnidade,
            @NotNull final PneuWebFinatto pneuWebFinatto) {
        final PneuEstoque pneu = new PneuEstoque();
        pneu.setCodigo(Long.parseLong(pneuWebFinatto.getCodPneu()));
        pneu.setCodigoCliente(pneuWebFinatto.getCodigoCliente());
        pneu.setPressaoCorreta(pneuWebFinatto.getPressaoRecomendadaPneuEmPsi());
        pneu.setPressaoAtual(pneuWebFinatto.getPressaoAtualPneuEmPsi());
        pneu.setVidaAtual(pneuWebFinatto.getVidaAtualPneu());
        pneu.setVidasTotal(pneuWebFinatto.getVidaTotalPneu());
        pneu.setCodUnidadeAlocado(codUnidade);
        pneu.setDimensao(new Pneu.Dimensao());

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(Long.parseLong(pneuWebFinatto.getCodMarcaPneu()));
        marcaPneu.setNome(pneuWebFinatto.getNomeMarcaPneu());
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(Long.valueOf(pneuWebFinatto.getCodModeloPneu()));
        modeloPneu.setNome(pneuWebFinatto.getNomeModeloPneu());
        modeloPneu.setQuantidadeSulcos(pneuWebFinatto.getQtdSulcosModeloPneu());
        pneu.setModelo(modeloPneu);

        if (pneuWebFinatto.isRecapado()) {
            final Banda banda = new Banda();
            final Marca marcaBanda = new Marca();
            marcaBanda.setCodigo(Long.parseLong(pneuWebFinatto.getCodMarcaBanda()));
            marcaBanda.setNome(pneuWebFinatto.getNomeMarcaBanda());
            banda.setMarca(marcaBanda);

            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(Long.parseLong(pneuWebFinatto.getCodModeloBanda()));
            modeloBanda.setNome(pneuWebFinatto.getNomeModeloBanda());
            modeloBanda.setQuantidadeSulcos(pneuWebFinatto.getQtdSulcosModeloBanda());
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
        sulcos.setInterno(pneuWebFinatto.getSulcoInternoPneuEmMilimetros());
        sulcos.setCentralInterno(pneuWebFinatto.getSulcoCentralInternoPneuEmMilimetros());
        sulcos.setCentralExterno(pneuWebFinatto.getSulcoCentralExternoPneuEmMilimetros());
        sulcos.setExterno(pneuWebFinatto.getSulcoExternoPneuEmMilimetros());
        pneu.setSulcosAtuais(sulcos);
        return pneu;
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
    private static Veiculo createVeiculoProlog(
            @NotNull final Long codUnidadeProlog,
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
    private static List<Pneu> createPneusProlog(
            @NotNull final Long codUnidadeProlog,
            @NotNull final List<PneuWebFinatto> pneusAplicados,
            @NotNull final IntegracaoPosicaoPneuMapper posicaoPneuMapper) {
        return pneusAplicados.stream()
                .map(pneuAplicado -> createPneuProlog(codUnidadeProlog, pneuAplicado, posicaoPneuMapper))
                .sorted(Pneu.POSICAO_PNEU_COMPARATOR)
                .collect(Collectors.toList());
    }

    @NotNull
    private static Pneu createPneuProlog(
            @NotNull final Long codUnidadeProlog,
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
        modeloPneu.setCodigo(Long.parseLong(pneuAplicado.getCodModeloPneu()));
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
        final ArrayList<ModeloPlacasAfericao> modelosPlacasAfericao =
                new ArrayList<>(modelosEstruturaVeiculo.values());
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
    private static DiagramaVeiculo createDiagramaProlog(
            @NotNull final Short codDiagramaProlog,
            @NotNull final String codEstruturaVeiculo) {
        return new DiagramaVeiculo(
                codDiagramaProlog,
                // Utilizamos a propriedade 'nome' como metadata para repassar o codEstruturaVeiculo.
                codEstruturaVeiculo,
                new HashSet<>(),
                "");
    }

    private static void logEstruturasNaoMapeadas(
            @NotNull final Set<String> estruturasNaoMapeadas) {
        final String message = "Estruturas não mapeadas: " + estruturasNaoMapeadas;
        Log.i(TAG, message);
        ErrorReportSystem.logMessage(message);
    }
}
