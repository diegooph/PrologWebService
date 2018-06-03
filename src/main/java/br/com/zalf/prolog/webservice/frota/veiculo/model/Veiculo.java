package br.com.zalf.prolog.webservice.frota.veiculo.model;


import br.com.zalf.prolog.webservice.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuComum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jean on 04/04/16.
 */
public class Veiculo {

    private String placa;
    private Marca marca;
    private ModeloVeiculo modelo;
    private TipoVeiculo tipo;

    /**
     * Depreciado em 22/05/2017. O aplicativo utilizava esse campo nessa época para desenhar o veículo na tela.
     */
    @Deprecated
    private Eixos eixos;
    private long kmAtual;
    private boolean ativo;
    private List<PneuComum> listPneus;
    private DiagramaVeiculo diagrama;

    /**
     * {@link Regional} onde o veículo se encontra.
     */
    private Long codRegionalAlocado;

    /**
     * {@link Unidade} onde o veículo se encontra.
     */
    private Long codUnidadeAlocado;

    public Veiculo() {

    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(ModeloVeiculo modelo) {
        this.modelo = modelo;
    }

    public List<PneuComum> getListPneus() {
        return listPneus;
    }

    public void setListPneus(List<PneuComum> listPneus) {
        this.listPneus = listPneus;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }

    public Eixos getEixos() {
        return eixos;
    }

    public void setEixos(Eixos eixos) {
        this.eixos = eixos;
    }

    public long getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(long km) {
        this.kmAtual = km;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public DiagramaVeiculo getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(DiagramaVeiculo diagrama) {
        this.diagrama = diagrama;
    }

    public Long getCodRegionalAlocado() {
        return codRegionalAlocado;
    }

    public void setCodRegionalAlocado(final Long codRegionalAlocado) {
        this.codRegionalAlocado = codRegionalAlocado;
    }

    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    public void setCodUnidadeAlocado(final Long codUnidadeAlocado) {
        this.codUnidadeAlocado = codUnidadeAlocado;
    }

    public boolean temEstepe() {
        if (listPneus == null)
            return false;

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < listPneus.size(); i++) {
            if (listPneus.get(i).getPosicao() >= 900) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    public List<PneuComum> getEstepes() {
        final List<PneuComum> estepes = new ArrayList<>();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < listPneus.size(); i++) {
            final PneuComum pneu = listPneus.get(i);
            if (pneu.isEstepe())
                estepes.add(pneu);
        }

        return estepes;
    }

    public void removeEstepes() {
        listPneus.removeIf(PneuComum::isEstepe);
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "placa='" + placa + '\'' +
                ", marca=" + marca +
                ", modelo=" + modelo +
                ", tipo=" + tipo +
                ", eixos=" + eixos +
                ", kmAtual=" + kmAtual +
                ", ativo=" + ativo +
                ", listPneus=" + listPneus +
                ", diagrama=" + diagrama +
                ", codRegionalAlocado=" + codRegionalAlocado +
                ", codUnidadeAlocado=" + codUnidadeAlocado +
                '}';
    }
}