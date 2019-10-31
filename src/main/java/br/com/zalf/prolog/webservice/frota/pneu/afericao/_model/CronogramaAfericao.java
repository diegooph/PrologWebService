package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import java.util.ArrayList;
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

    public CronogramaAfericao() {}

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

    public void calcularQuatidadeSulcosPressaoOk(CronogramaAfericao cronogramaAfericao) {
        int qtdSulcosOk = 0;
        int qtdPressaoOk = 0;
        int qtdSulcosPressaook = 0;

        int qtdModeloSulcosOk = 0;
        int qtdModeloPressaoOk = 0;
        int qtdModeloSulcosPressaoOk = 0;

        final int metaAfericaoSulco = cronogramaAfericao.getMetaAfericaoSulco();
        final int metaAfericaoPressao = cronogramaAfericao.getMetaAfericaoPressao();

        final List<ModeloPlacasAfericao> modelos = cronogramaAfericao.getModelosPlacasAfericao();
        for (final ModeloPlacasAfericao modelo : modelos) {
            for (final ModeloPlacasAfericao.PlacaAfericao placaAfericao : modelo.getPlacasAfericao()) {
                if (isAfericaoSulcoOk(placaAfericao, metaAfericaoSulco)) {
                    qtdSulcosOk++;
                    qtdModeloSulcosOk++;
                }
                if (isAfericaoPressaoOk(placaAfericao, metaAfericaoPressao)) {
                    qtdPressaoOk++;
                    qtdModeloPressaoOk++;
                }
                if (isAfericaoSulcoOk(placaAfericao, metaAfericaoSulco)
                        && isAfericaoPressaoOk(placaAfericao, metaAfericaoPressao)) {
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
        cronogramaAfericao.setTotalSulcosOk(qtdSulcosOk);
        cronogramaAfericao.setTotalPressaoOk(qtdPressaoOk);
        cronogramaAfericao.setTotalSulcoPressaoOk(qtdSulcosPressaook);
    }

    public void calcularTotalVeiculos(CronogramaAfericao cronogramaAfericao) {
        int totalVeiculos = 0;
        for (final ModeloPlacasAfericao modelo : cronogramaAfericao.getModelosPlacasAfericao()) {
            modelo.setTotalVeiculosModelo(modelo.getPlacasAfericao().size());
            totalVeiculos += modelo.getPlacasAfericao().size();
        }
        cronogramaAfericao.setTotalVeiculos(totalVeiculos);
    }

    private boolean isAfericaoPressaoOk(ModeloPlacasAfericao.PlacaAfericao placaAfericao, int metaAfericaoPressao) {
        return placaAfericao.getIntervaloUltimaAfericaoPressao() <= metaAfericaoPressao
                && placaAfericao.getIntervaloUltimaAfericaoPressao() != ModeloPlacasAfericao.PlacaAfericao.INTERVALO_INVALIDO;
    }

    private boolean isAfericaoSulcoOk(ModeloPlacasAfericao.PlacaAfericao placaAfericao, int metaAfericaoSulco) {
        return placaAfericao.getIntervaloUltimaAfericaoSulco() <= metaAfericaoSulco
                && placaAfericao.getIntervaloUltimaAfericaoSulco() != ModeloPlacasAfericao.PlacaAfericao.INTERVALO_INVALIDO;
    }

    public void removerPlacasNaoAferiveis(final CronogramaAfericao cronogramaAfericao) {
        final List<ModeloPlacasAfericao.PlacaAfericao> placasNaoAferiveis = new ArrayList<>();
        for (final ModeloPlacasAfericao modelo : cronogramaAfericao.getModelosPlacasAfericao()) {
            for (final ModeloPlacasAfericao.PlacaAfericao placaAfericao : modelo.getPlacasAfericao()) {
                // Se não pode aferir nem SULCO nem PRESSAO e nem SULCO_PRESSAO, removemos essa placa da listagem.
                if (!placaAfericao.isPodeAferirPressao()
                        && !placaAfericao.isPodeAferirSulco()
                        && !placaAfericao.isPodeAferirSulcoPressao()) {
                    placasNaoAferiveis.add(placaAfericao);
                }
            }
            modelo.getPlacasAfericao().removeAll(placasNaoAferiveis);
        }
    }

    public void removerModelosSemPlacas(final CronogramaAfericao cronogramaAfericao) {
        final List<ModeloPlacasAfericao> modelosSemPlacas = new ArrayList<>();
        for (final ModeloPlacasAfericao modeloPlacasAfericao : cronogramaAfericao.getModelosPlacasAfericao()) {
            if (modeloPlacasAfericao.getPlacasAfericao().isEmpty()) {
                modelosSemPlacas.add(modeloPlacasAfericao);
            }
        }
        if (!modelosSemPlacas.isEmpty()) {
            cronogramaAfericao.getModelosPlacasAfericao().removeAll(modelosSemPlacas);
        }
    }
}
