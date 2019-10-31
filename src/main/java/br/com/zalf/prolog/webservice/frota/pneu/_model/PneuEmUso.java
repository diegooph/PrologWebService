package br.com.zalf.prolog.webservice.frota.pneu._model;

import com.google.common.base.Preconditions;

/**
 * Created on 31/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuEmUso extends Pneu {
    private String placa;
    private Long codVeiculo;
    private String posicaoAplicado;

    public PneuEmUso() {
        super(PneuTipo.PNEU_EM_USO);
        setStatus(StatusPneu.EM_USO);
    }

    @Override
    public void setStatus(final StatusPneu status) {
        Preconditions.checkArgument(status == StatusPneu.EM_USO);
        super.setStatus(status);
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Long getCodVeiculo() {
        return codVeiculo;
    }

    public void setCodVeiculo(Long codVeiculo) {
        this.codVeiculo = codVeiculo;
    }

    public String getPosicaoAplicado() {
        return posicaoAplicado;
    }

    public void setPosicaoAplicado(String posicaoAplicado) {
        this.posicaoAplicado = posicaoAplicado;
    }
}