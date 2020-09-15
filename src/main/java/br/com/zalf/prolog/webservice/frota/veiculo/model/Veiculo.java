package br.com.zalf.prolog.webservice.frota.veiculo.model;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jean on 04/04/16.
 */
@Deprecated
public class Veiculo {

    private Long codigo;
    private String placa;
    private Marca marca;
    private ModeloVeiculo modelo;
    private TipoVeiculo tipo;
    private String identificadorFrota;

    /**
     * Depreciado em 22/05/2017. O aplicativo utilizava esse campo nessa época para desenhar o veículo na tela.
     */
    @Deprecated
    private Eixos eixos;
    private Long kmAtual;
    private boolean ativo;
    private List<Pneu> listPneus;
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

    public Long getCodTipo() {
        return tipo.getCodigo();
    }

    public Long getCodModelo() {
        return modelo.getCodigo();
    }

    public boolean temEstepe() {
        if (listPneus == null) {
            return false;
        }

        for (int i = 0; i < listPneus.size(); i++) {
            if (listPneus.get(i).getPosicao() >= 900) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    public List<Pneu> getEstepes() {
        final List<Pneu> estepes = new ArrayList<>();

        for (int i = 0; i < listPneus.size(); i++) {
            final Pneu pneu = listPneus.get(i);
            if (pneu.isEstepe()) {
                estepes.add(pneu);
            }
        }

        return estepes;
    }

    public void removeEstepes() {
        listPneus.removeIf(Pneu::isEstepe);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(final Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(final ModeloVeiculo modelo) {
        this.modelo = modelo;
    }

    public List<Pneu> getListPneus() {
        return listPneus;
    }

    public void setListPneus(final List<Pneu> listPneus) {
        this.listPneus = listPneus;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(final String placa) {
        this.placa = placa;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(final TipoVeiculo tipo) {
        this.tipo = tipo;
    }

    public String getIdentificadorFrota() {
        return identificadorFrota;
    }

    public void setIdentificadorFrota(final String identificadorFrota) {
        this.identificadorFrota = identificadorFrota;
    }

    public Eixos getEixos() {
        return eixos;
    }

    public void setEixos(final Eixos eixos) {
        this.eixos = eixos;
    }

    public Long getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(final Long km) {
        this.kmAtual = km;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
    }

    public DiagramaVeiculo getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(final DiagramaVeiculo diagrama) {
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

    @Override
    public String toString() {
        return "Veiculo{" +
                "codigo=" + codigo +
                ", placa='" + placa + '\'' +
                ", identificadorFrota=" + identificadorFrota +
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