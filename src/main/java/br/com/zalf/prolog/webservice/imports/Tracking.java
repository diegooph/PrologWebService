package br.com.zalf.prolog.webservice.imports;

import java.sql.Time;
import java.util.Date;

/**
 * Created by jean on 19/01/16.
 * Contém os dados de uma linha da tabela tracking
 */
public class Tracking {

    public int classe;
    public Date data;
    public int mapa;
    public String placa;
    public int codCliente;
    public int seqReal;
    public int seqPlan;
    public Time inicioRota;
    public Time horarioMatinal;
    public Time saidaCDD;
    public Time chegadaPDV;
    public Time tempoPrevRetorno;
    public Time tempoRetorno;
    public double distPrevRetorno;
    public double distPercRetorno;
    public Time inicioEntrega;
    public Time fimEntrega;
    public Time fimRota;
    public Time entradaCDD;
    public double caixasCarregadas;
    public double caixasDevolvidas;
    public double repasse;
    public Time tempoEntrega;
    public Time tempoDescarga;
    public Time tempoEspera;
    public Time tempoAlmoco;
    public Time tempoTotalRota;
    public double dispApontCadastrado;
    public String latEntrega;
    public String lonEntrega;
    public int unidadeNegocio;
    public String transportadora;
    public String latClienteApontamento;
    public String lonClienteApontamento;
    public String latAtualCliente;
    public String lonAtualCliente;
    public double distanciaPrev;
    public Time tempoDeslocamento;
    public double velMedia;
    public double distanciaPercApontamento;
    public String aderenciaSequenciaEntrega;
    public String aderenciaJanelaEntrega;
    public String pdvLacrado;
    public int codigoTransportadora;

    @Override
    public String toString() {
        return "Tracking{" +
                "classe=" + classe +
                ", data=" + data +
                ", mapa=" + mapa +
                ", placa='" + placa + '\'' +
                ", codCliente=" + codCliente +
                ", seqReal=" + seqReal +
                ", seqPlan=" + seqPlan +
                ", inicioRota=" + inicioRota +
                ", horarioMatinal=" + horarioMatinal +
                ", saidaCDD=" + saidaCDD +
                ", chegadaPDV=" + chegadaPDV +
                ", tempoPrevRetorno=" + tempoPrevRetorno +
                ", tempoRetorno=" + tempoRetorno +
                ", distPrevRetorno=" + distPrevRetorno +
                ", distPercRetorno=" + distPercRetorno +
                ", inicioEntrega=" + inicioEntrega +
                ", fimEntrega=" + fimEntrega +
                ", fimRota=" + fimRota +
                ", entradaCDD=" + entradaCDD +
                ", caixasCarregadas=" + caixasCarregadas +
                ", caixasDevolvidas=" + caixasDevolvidas +
                ", repasse=" + repasse +
                ", tempoEntrega=" + tempoEntrega +
                ", tempoDescarga=" + tempoDescarga +
                ", tempoEspera=" + tempoEspera +
                ", tempoAlmoco=" + tempoAlmoco +
                ", tempoTotalRota=" + tempoTotalRota +
                ", dispApontCadastrado=" + dispApontCadastrado +
                ", latEntrega='" + latEntrega + '\'' +
                ", lonEntrega='" + lonEntrega + '\'' +
                ", unidadeNegocio=" + unidadeNegocio +
                ", transportadora='" + transportadora + '\'' +
                ", latClienteApontamento='" + latClienteApontamento + '\'' +
                ", lonClienteApontamento='" + lonClienteApontamento + '\'' +
                ", latAtualCliente='" + latAtualCliente + '\'' +
                ", lonAtualCliente='" + lonAtualCliente + '\'' +
                ", distanciaPrev=" + distanciaPrev +
                ", tempoDeslocamento=" + tempoDeslocamento +
                ", velMedia=" + velMedia +
                ", distanciaPercApontamento=" + distanciaPercApontamento +
                ", aderenciaSequenciaEntrega='" + aderenciaSequenciaEntrega + '\'' +
                ", aderenciaJanelaEntrega='" + aderenciaJanelaEntrega + '\'' +
                ", pdvLacrado='" + pdvLacrado + '\'' +
                ", codigoTransportadora=" + codigoTransportadora +
                '}';
    }
}
