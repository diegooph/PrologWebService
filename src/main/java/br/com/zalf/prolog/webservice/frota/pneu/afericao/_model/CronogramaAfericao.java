package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public class CronogramaAfericao {
    private List<ModeloPlacasAfericao> modelosPlacasAfericao;
    private int metaAfericaoSulco;
    private int metaAfericaoPressao;
    private int totalSulcosOk;
    private int totalPressaoOk;
    private int totalSulcoPressaoOk;
    private int totalVeiculos;

    public CronogramaAfericao() {
    }

    public List<ModeloPlacasAfericao> getModelosPlacasAfericao() {
        return modelosPlacasAfericao;
    }

    public void setModelosPlacasAfericao(List<ModeloPlacasAfericao> modelosPlacasAfericao) {
        this.modelosPlacasAfericao = modelosPlacasAfericao;
    }

    public int getMetaAfericaoSulco() {
        return metaAfericaoSulco;
    }

    public void setMetaAfericaoSulco(int metaAfericaoSulco) {
        this.metaAfericaoSulco = metaAfericaoSulco;
    }

    public int getMetaAfericaoPressao() {
        return metaAfericaoPressao;
    }

    public void setMetaAfericaoPressao(int metaAfericaoPressao) {
        this.metaAfericaoPressao = metaAfericaoPressao;
    }

    public int getTotalSulcosOk() {
        return totalSulcosOk;
    }

    public void setTotalSulcosOk(int totalSulcosOk) {
        this.totalSulcosOk = totalSulcosOk;
    }

    public int getTotalPressaoOk() {
        return totalPressaoOk;
    }

    public void setTotalPressaoOk(int totalPressaoOk) {
        this.totalPressaoOk = totalPressaoOk;
    }

    public int getTotalSulcoPressaoOk() {
        return totalSulcoPressaoOk;
    }

    public void setTotalSulcoPressaoOk(int totalSulcoPressaoOk) {
        this.totalSulcoPressaoOk = totalSulcoPressaoOk;
    }

    public int getTotalVeiculos() {
        return totalVeiculos;
    }

    public void setTotalVeiculos(int totalVeiculos) {
        this.totalVeiculos = totalVeiculos;
    }

    @Override
    public String toString() {
        return "CronogramaAfericao{" +
                "modelosPlacasAfericao=" + modelosPlacasAfericao +
                ", metaAfericaoSulco=" + metaAfericaoSulco +
                ", metaAfericaoPressao=" + metaAfericaoPressao +
                ", totalSulcosOk=" + totalSulcosOk +
                ", totalPressaoOk=" + totalPressaoOk +
                ", totalSulcoPressaoOk=" + totalSulcoPressaoOk +
                ", totalVeiculos=" + totalVeiculos +
                '}';
    }

    public void calcularQuatidadeSulcosPressaoOk(final boolean useMetaAfericaoFromPlaca) {
        int qtdSulcosOk = 0;
        int qtdPressaoOk = 0;
        int qtdSulcosPressaook = 0;

        int qtdModeloSulcosOk = 0;
        int qtdModeloPressaoOk = 0;
        int qtdModeloSulcosPressaoOk = 0;

        for (final ModeloPlacasAfericao modelo : getModelosPlacasAfericao()) {
            for (final ModeloPlacasAfericao.PlacaAfericao placaAfericao : modelo.getPlacasAfericao()) {
                if (isAfericaoSulcoOk(placaAfericao, useMetaAfericaoFromPlaca)) {
                    qtdSulcosOk++;
                    qtdModeloSulcosOk++;
                }
                if (isAfericaoPressaoOk(placaAfericao, useMetaAfericaoFromPlaca)) {
                    qtdPressaoOk++;
                    qtdModeloPressaoOk++;
                }
                if (isAfericaoSulcoOk(placaAfericao, useMetaAfericaoFromPlaca)
                        && isAfericaoPressaoOk(placaAfericao, useMetaAfericaoFromPlaca)) {
                    qtdSulcosPressaook++;
                    qtdModeloSulcosPressaoOk++;
                }
            }
            // Devemos setar em cada modelo a quantidade de Sulco/Pressao.
            modelo.setQtdModeloSulcoOk(qtdModeloSulcosOk);
            modelo.setQtdModeloPressaoOk(qtdModeloPressaoOk);
            modelo.setQtdModeloSulcoPressaoOk(qtdModeloSulcosPressaoOk);
            qtdModeloSulcosOk = 0;
            qtdModeloPressaoOk = 0;
            qtdModeloSulcosPressaoOk = 0;
        }
        // Devemos setar a quatidade total de sulcos/pressões no cronograma.
        setTotalSulcosOk(qtdSulcosOk);
        setTotalPressaoOk(qtdPressaoOk);
        setTotalSulcoPressaoOk(qtdSulcosPressaook);
    }

    public void calcularTotalVeiculos() {
        int totalVeiculos = 0;
        for (final ModeloPlacasAfericao modelo : getModelosPlacasAfericao()) {
            modelo.setTotalVeiculosModelo(modelo.getPlacasAfericao().size());
            totalVeiculos += modelo.getPlacasAfericao().size();
        }
        setTotalVeiculos(totalVeiculos);
    }

    private boolean isAfericaoPressaoOk(ModeloPlacasAfericao.PlacaAfericao placaAfericao,
                                        final boolean useMetaAfericaoFromPlaca) {
        final int metaAfericao = useMetaAfericaoFromPlaca ? placaAfericao.getMetaAfericaoPressao() : metaAfericaoPressao;
        return placaAfericao.getIntervaloUltimaAfericaoPressao() <= metaAfericao
                && placaAfericao.getIntervaloUltimaAfericaoPressao() != ModeloPlacasAfericao.PlacaAfericao.INTERVALO_INVALIDO;
    }

    private boolean isAfericaoSulcoOk(ModeloPlacasAfericao.PlacaAfericao placaAfericao,
                                      final boolean useMetaAfericaoFromPlaca) {
        final int metaAfericao = useMetaAfericaoFromPlaca ? placaAfericao.getMetaAfericaoSulco() : metaAfericaoSulco;
        return placaAfericao.getIntervaloUltimaAfericaoSulco() <= metaAfericao
                && placaAfericao.getIntervaloUltimaAfericaoSulco() != ModeloPlacasAfericao.PlacaAfericao.INTERVALO_INVALIDO;
    }

    public void removerPlacasNaoAferiveis() {
        getModelosPlacasAfericao()
                .forEach(m -> m
                        .getPlacasAfericao()
                        .removeIf(p ->
                                // Se não pode aferir nem SULCO nem PRESSAO e nem SULCO_PRESSAO, removemos essa placa
                                // da listagem.
                                !p.isPodeAferirPressao()
                                        && !p.isPodeAferirSulco()
                                        && !p.isPodeAferirSulcoPressao()));
    }

    public void removerModelosSemPlacas() {
        getModelosPlacasAfericao()
                .removeIf(m -> m.getPlacasAfericao().isEmpty());
    }
}
