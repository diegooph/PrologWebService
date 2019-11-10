package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.*;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 27/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ProtheusRodalogConverter {
    @NotNull
    static AfericaoProtheusRodalog convertAfericao(@NotNull final Afericao afericao) {
        if (afericao instanceof AfericaoPlaca) {
            final AfericaoProtheusRodalog afericaoRodalog = new AfericaoProtheusRodalog();
            afericaoRodalog.setPlacaAfericao(((AfericaoPlaca) afericao).getVeiculo().getPlaca());
            afericaoRodalog.setCodUnidade(afericao.getCodUnidade());
            afericaoRodalog.setCpfColaboradorAfericao(afericao.getColaborador().getCpfAsString());
            afericaoRodalog.setKmMomentoAfericao(((AfericaoPlaca) afericao).getKmMomentoAfericao());
            afericaoRodalog.setTempoRealizacaoAfericaoInMillis(afericao.getTempoRealizacaoAfericaoInMillis());
            afericaoRodalog.setDataHora(afericao.getDataHora());
            afericaoRodalog.setTipoMedicaoColetadaAfericao(
                    convertTipoMedicao(afericao.getTipoMedicaoColetadaAfericao()));
            afericaoRodalog.setMedicoes(convertPneusAferidos(afericao.getPneusAferidos()));
            return afericaoRodalog;
        } else {
            throw new IllegalStateException("[INTEGRACAO - RODALOG] A integração suporta apenas aferição de placa");
        }
    }

    @NotNull
    static CronogramaAfericao convertCronogramaAfericao(
            @NotNull final CronogramaAfericaoProtheusRodalog cronogramaAfericaoRodalog) {
        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        cronogramaAfericao.setMetaAfericaoSulco(cronogramaAfericaoRodalog.getMetaDiasAfericaoSulco());
        cronogramaAfericao.setMetaAfericaoPressao(cronogramaAfericaoRodalog.getMetaDiasAfericaoPressao());
        cronogramaAfericao.setTotalSulcosOk(cronogramaAfericaoRodalog.getTotalPlacasSulcosOk());
        cronogramaAfericao.setTotalPressaoOk(cronogramaAfericaoRodalog.getTotalPlacasPressaoOk());
        cronogramaAfericao.setTotalSulcoPressaoOk(cronogramaAfericaoRodalog.getTotalPlacasSulcoPressaoOk());
        cronogramaAfericao.setTotalVeiculos(cronogramaAfericaoRodalog.getTotalPlacas());
        cronogramaAfericao.setModelosPlacasAfericao(
                convertModelosPlacasAfericao(cronogramaAfericaoRodalog.getModelosPlacasAfericao()));
        return cronogramaAfericao;
    }

    @NotNull
    static NovaAfericaoPlaca convertNovaAfericaoPlaca(
            @NotNull final NovaAfericaoPlacaProtheusRodalog novaAfericaoRodalog,
            @NotNull final DiagramaVeiculo diagramaVeiculo) {
        final NovaAfericaoPlaca novaAfericaoPlaca = new NovaAfericaoPlaca();
        novaAfericaoPlaca.setVariacaoAceitaSulcoMaiorMilimetros(
                novaAfericaoRodalog.getVariacaoAceitaSulcoMaiorMilimetros());
        novaAfericaoPlaca.setVariacaoAceitaSulcoMenorMilimetros(
                novaAfericaoRodalog.getVariacaoAceitaSulcoMenorMilimetros());
        novaAfericaoPlaca.setDeveAferirEstepes(novaAfericaoRodalog.getDeveAferirEstepes());
        if (novaAfericaoRodalog.getRestricao() == null) {
            throw new IllegalStateException("Nenhuma informação de restrição foi enviada");
        }
        novaAfericaoPlaca.setRestricao(convertRestricao(novaAfericaoRodalog.getRestricao()));
        novaAfericaoPlaca.setVeiculo(convertVeiculo(novaAfericaoRodalog, diagramaVeiculo));
        novaAfericaoPlaca.setEstepesVeiculo(convertPneus(novaAfericaoRodalog.getEstepesVeiculo()));
        return novaAfericaoPlaca;
    }

    @NotNull
    private static TipoMedicaoAfericaoProtheusRodalog convertTipoMedicao(
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
        return TipoMedicaoAfericaoProtheusRodalog.fromString(tipoMedicaoColetadaAfericao.asString());
    }

    @NotNull
    private static Restricao convertRestricao(@NotNull final RestricaoAfericaoProtheusRodalog restricaoRodalog) {
        final Restricao restricao = new Restricao();
        restricao.setToleranciaCalibragem(restricaoRodalog.getToleranciaCalibragem());
        restricao.setToleranciaInspecao(restricaoRodalog.getToleranciaInspecao());
        restricao.setSulcoMinimoDescarte(restricaoRodalog.getSulcoMinimoDescarte());
        restricao.setSulcoMinimoRecape(restricaoRodalog.getSulcoMinimoRecape());
        restricao.setPeriodoDiasAfericaoPressao(restricaoRodalog.getPeriodoDiasAfericaoPressao());
        restricao.setPeriodoDiasAfericaoSulco(restricaoRodalog.getPeriodoDiasAfericaoSulco());
        return restricao;
    }

    @NotNull
    private static Veiculo convertVeiculo(@NotNull final NovaAfericaoPlacaProtheusRodalog novaAfericaoRodalog,
                                          @NotNull final DiagramaVeiculo diagramaVeiculo) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodUnidadeAlocado(novaAfericaoRodalog.getCodUnidadePlacaAlocada());
        veiculo.setPlaca(novaAfericaoRodalog.getPlaca());
        veiculo.setKmAtual(novaAfericaoRodalog.getUltimoKmVeiculo());
        veiculo.setDiagrama(diagramaVeiculo);
        veiculo.setListPneus(convertPneus(novaAfericaoRodalog.getPneusVeiculo()));
        return veiculo;
    }

    @NotNull
    private static List<Pneu> convertPneus(@NotNull final List<PneuAfericaoProtheusRodalog> pneusVeiculo) {
        final List<Pneu> pneus = new ArrayList<>();
        for (final PneuAfericaoProtheusRodalog pneuRodalog : pneusVeiculo) {
            pneus.add(convertPneu(pneuRodalog));
        }
        // Ordena lista pelas posições do ProLog.
        pneus.sort(Pneu.POSICAO_PNEU_COMPARATOR);
        return pneus;
    }

    @NotNull
    private static Pneu convertPneu(@NotNull final PneuAfericaoProtheusRodalog pneuRodalog) {
        final PneuComum pneu = new PneuComum();
        pneu.setCodigoCliente(pneuRodalog.getCodigoCliente());
        pneu.setCodigo(pneuRodalog.getCodigo());
        pneu.setCodUnidadeAlocado(pneuRodalog.getCodUnidadeAlocado());
        pneu.setVidaAtual(pneuRodalog.getVidaAtual());
        pneu.setVidasTotal(pneuRodalog.getVidaTotal());
        pneu.setPosicao(pneuRodalog.getPosicao());
        pneu.setPressaoCorreta(pneuRodalog.getPressaoCorreta());
        pneu.setPressaoAtual(pneuRodalog.getPressaoAtual());
        if (pneuRodalog.temSulcosAtuais()) {
            final Sulcos sulcos = new Sulcos();
            sulcos.setInterno(pneuRodalog.getSulcoInternoAtual());
            sulcos.setCentralInterno(pneuRodalog.getSulcoCentralInternoAtual());
            sulcos.setCentralExterno(pneuRodalog.getSulcoCentralExternoAtual());
            sulcos.setExterno(pneuRodalog.getSulcoExternoAtual());
            pneu.setSulcosAtuais(sulcos);
        }
        pneu.setModelo(convertModeloPneu(pneuRodalog.getModeloPneu()));
        if (pneuRodalog.isRecapado()) {
            pneu.setBanda(convertBanda(pneuRodalog.getModeloBanda()));
        }
        return pneu;
    }

    @NotNull
    private static Banda convertBanda(@NotNull final ModeloBandaProtheusRodalog modeloBandaRodalog) {
        final Banda banda = new Banda();
        final ModeloBanda modeloBanda = new ModeloBanda();
        modeloBanda.setCodigo(modeloBandaRodalog.getCodigo());
        modeloBanda.setNome(modeloBandaRodalog.getNomeModelo());
        modeloBanda.setQuantidadeSulcos(modeloBandaRodalog.getQuantidadeSulcos());
        modeloBanda.setAlturaSulcos(modeloBandaRodalog.getAlturaSulcos());
        banda.setModelo(modeloBanda);
        return banda;
    }

    @NotNull
    private static ModeloPneu convertModeloPneu(@NotNull final ModeloPneuProtheusRodalog modeloPneuRodalog) {
        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(modeloPneuRodalog.getCodigo());
        modeloPneu.setNome(modeloPneuRodalog.getNomeModelo());
        modeloPneu.setAlturaSulcos(modeloPneuRodalog.getAlturaSulcos());
        modeloPneu.setQuantidadeSulcos(modeloPneuRodalog.getQuantidadeSulcos());
        return modeloPneu;
    }

    @NotNull
    private static List<ModeloPlacasAfericao> convertModelosPlacasAfericao(
            @NotNull final List<ModeloAfericaoProtheusRodalog> modelosPlacasAfericaoRodalog) {
        final List<ModeloPlacasAfericao> modelosPlacasAfericao = new ArrayList<>();
        for (final ModeloAfericaoProtheusRodalog modeloRodalog : modelosPlacasAfericaoRodalog) {
            modelosPlacasAfericao.add(convertModeloPlacaAfericao(modeloRodalog));
        }
        Preconditions.checkState(
                modelosPlacasAfericaoRodalog.size() == modelosPlacasAfericao.size(),
                "A conversão entre modelos do cronograma tem tamanhos diferentes, " +
                        "indicando que houve um problema na conversão dos objetos");
        return modelosPlacasAfericao;
    }

    @NotNull
    private static ModeloPlacasAfericao convertModeloPlacaAfericao(
            @NotNull final ModeloAfericaoProtheusRodalog modeloRodalog) {
        final ModeloPlacasAfericao modeloPlacasAfericao = new ModeloPlacasAfericao();
        modeloPlacasAfericao.setNomeModelo(modeloRodalog.getNomeModelo());
        modeloPlacasAfericao.setQtdModeloSulcoOk(modeloRodalog.getQtdPlacasSulcoOk());
        modeloPlacasAfericao.setQtdModeloPressaoOk(modeloRodalog.getQtdPlacasPressaoOk());
        modeloPlacasAfericao.setQtdModeloSulcoPressaoOk(modeloRodalog.getQtdPlacasSulcoPressaoOk());
        modeloPlacasAfericao.setTotalVeiculosModelo(modeloRodalog.getTotalPlacasModelo());
        modeloPlacasAfericao.setPlacasAfericao(convertPlacasAfericao(modeloRodalog.getPlacasAfericao()));
        return modeloPlacasAfericao;
    }

    @NotNull
    private static List<ModeloPlacasAfericao.PlacaAfericao> convertPlacasAfericao(
            @NotNull final List<PlacaAfericaoProtheusRodalog> placasAfericaoRodalog) {
        final List<ModeloPlacasAfericao.PlacaAfericao> placasAfericao = new ArrayList<>();
        for (final PlacaAfericaoProtheusRodalog placaRodalog : placasAfericaoRodalog) {
            placasAfericao.add(convertPlacaAfericao(placaRodalog));
        }
        Preconditions.checkState(
                placasAfericaoRodalog.size() == placasAfericao.size(),
                "A conversão entre placas do cronograma tem tamanhos diferentes, " +
                        "indicando que houve um problema na conversão dos objetos");
        return placasAfericao;
    }

    @NotNull
    private static ModeloPlacasAfericao.PlacaAfericao convertPlacaAfericao(
            @NotNull final PlacaAfericaoProtheusRodalog placaRodalog) {
        final ModeloPlacasAfericao.PlacaAfericao placa = new ModeloPlacasAfericao.PlacaAfericao();
        placa.setPlaca(placaRodalog.getPlaca());
        placa.setIntervaloUltimaAfericaoSulco(placaRodalog.getIntervaloDiasUltimaAfericaoSulco());
        placa.setIntervaloUltimaAfericaoPressao(placaRodalog.getIntervaloDiasUltimaAfericaoPressao());
        placa.setQuantidadePneus(placaRodalog.getQuantidadePneusAplicados());
        placa.setPodeAferirSulco(placaRodalog.getPodeAferirSulco());
        placa.setPodeAferirPressao(placaRodalog.getPodeAferirPressao());
        placa.setPodeAferirSulcoPressao(placaRodalog.getPodeAferirSulcoPressao());
        placa.setPodeAferirEstepe(placaRodalog.getPodeAferirEstepe());
        return placa;
    }

    @NotNull
    private static List<MedicaoAfericaoProtheusRodalog> convertPneusAferidos(@NotNull final List<Pneu> pneusAferidos) {
        if (!pneusAferidos.isEmpty()) {
            final List<MedicaoAfericaoProtheusRodalog> medicoes = new ArrayList<>();
            for (final Pneu pneuAferido : pneusAferidos) {
                medicoes.add(convertPneuAferido(pneuAferido));
            }
            Preconditions.checkState(
                    pneusAferidos.size() == medicoes.size(),
                    "A conversão entre pneus aferidos e medições resultou em tamanhos diferentes, " +
                            "indicando que houve um problema na conversão dos objetos");
            return medicoes;
        } else {
            throw new IllegalStateException("[INTEGRACAO - RODALOG] Nenhum pneu foi medido");
        }
    }

    @NotNull
    private static MedicaoAfericaoProtheusRodalog convertPneuAferido(@NotNull final Pneu pneuAferido) {
        final MedicaoAfericaoProtheusRodalog pneuRodalog = new MedicaoAfericaoProtheusRodalog();
        pneuRodalog.setCodigoCliente(pneuAferido.getCodigoCliente());
        pneuRodalog.setCodigo(pneuAferido.getCodigo());
        pneuRodalog.setVidaAtual(pneuAferido.getVidaAtual());
        pneuRodalog.setPressaoAtual(pneuAferido.getPressaoAtual());
        final Sulcos sulcosAtuais = pneuAferido.getSulcosAtuais();
        if (sulcosAtuais == null) {
            throw new IllegalStateException(
                    "[INTEGRACAO - RODALOG] Nenhuma medição para o pneu: " + pneuAferido.getCodigo());
        }
        pneuRodalog.setSulcoInterno(sulcosAtuais.getInterno());
        pneuRodalog.setSulcoCentralInterno(sulcosAtuais.getCentralInterno());
        pneuRodalog.setSulcoCentralExterno(sulcosAtuais.getCentralExterno());
        pneuRodalog.setSulcoExterno(sulcosAtuais.getExterno());
        return pneuRodalog;
    }
}
