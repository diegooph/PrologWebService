package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import java.util.List;

/**
 * Created on 12/6/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ServicosAbertosHolder {
    private List<QuantidadeServicos> servicosAbertos;
    private int qtdTotalCalibragensAbertas;
    private int qtdTotalInspecoesAbertas;
    private int qtdTotalMovimentacoesAbertas;

    public List<QuantidadeServicos> getServicosAbertos() {
        return servicosAbertos;
    }

    public void setServicosAbertos(List<QuantidadeServicos> servicosAbertos) {
        this.servicosAbertos = servicosAbertos;
    }

    public int getQtdTotalCalibragensAbertas() {
        return qtdTotalCalibragensAbertas;
    }

    public void setQtdTotalCalibragensAbertas(int qtdTotalCalibragensAbertas) {
        this.qtdTotalCalibragensAbertas = qtdTotalCalibragensAbertas;
    }

    public int getQtdTotalInspecoesAbertas() {
        return qtdTotalInspecoesAbertas;
    }

    public void setQtdTotalInspecoesAbertas(int qtdTotalInspecoesAbertas) {
        this.qtdTotalInspecoesAbertas = qtdTotalInspecoesAbertas;
    }

    public int getQtdTotalMovimentacoesAbertas() {
        return qtdTotalMovimentacoesAbertas;
    }

    public void setQtdTotalMovimentacoesAbertas(int qtdTotalMovimentacoesAbertas) {
        this.qtdTotalMovimentacoesAbertas = qtdTotalMovimentacoesAbertas;
    }
}