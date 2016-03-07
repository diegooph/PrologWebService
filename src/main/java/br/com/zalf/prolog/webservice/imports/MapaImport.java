package br.com.zalf.prolog.webservice.imports;

import java.sql.Time;
import java.util.Date;

/**
 * Created by jean on 18/01/16.
 * Contém os dados de uma linha da tabela 2art (mapa)
 */
public class MapaImport {

    public Date data;
    public int transp;
    public String entrega;
    public String cargaAtual;
    public String frota;
    public double custoSpot;
    public int regiao;
    public int veiculo;
    public String placa;
    public double veiculoIndisp;
    public double placaIndisp;
    public double frotaIndisp;
    public int tipoIndisp;
    public int mapa;
    public int entregas;
    public double cxCarreg;
    public double cxEntreg;
    public double ocupacao;
    public double cxRota;
    public double cxAs;
    public double veicBM;
    public int rShow;
    public String entrVol;
    public Date hrSai;
    public Date hrEntr;
    public int kmSai;
    public int kmEntr;
    public double custoVariavel;
    public double lucro;
    public double lucroUnit;
    public double valorFrete;
    public String tipoImposto;
    public double percImposto;
    public double valorImposto;
    public double valorFaturado;
    public double valorUnitCxEntregue;
    public double valorPgCxEntregSemImp;
    public double valorPgCxEntregComImp;
    public Time tempoPrevistoRoad;
    public double kmPrevistoRoad;
    public double valorUnitPontoMot;
    public double valorUnitPontoAjd;
    public double valorEquipeEntrMot;
    public double valorEquipeEntrAjd;
    public double custoVLC;
    public double lucroUnitCEDBZ;
    public double CustoVlcCxEntr;
    public Time tempoInterno;
    public double valorDropDown;
    public String veicCadDD;
    public double kmLaco;
    public double kmDeslocamento;
    public Time tempoLaco;
    public Time tempoDeslocamento;
    public double sitMultiCDD;
    public int unbOrigem;
    public int matricMotorista;
    public int matricAjud1;
    public int matricAjud2;
    public String valorCTEDifere;
    public int qtNfCarregadas;
    public int qtNfEntregues;
    public double indDevCx;
    public double indDevNf;
    public double fator;
    public String recarga;
    public Time hrMatinal;
    public Time hrJornadaLiq;
    public Time hrMetaJornada;
    public double vlBateuJornMot;
    public double vlNaoBateuJornMot;
    public double vlRecargaMot;
    public double vlBateuJornAju;
    public double vlNaoBateuJornAju;
    public double vlRecargaAju;
    public double vlTotalMapa;
    public double qtHlCarregados;
    public double qtHlEntregues;
    public double indiceDevHl;
    public String regiao2;
    public int qtNfCarregGeral;
    public int qtNfEntregGeral;
    public double capacidadeVeiculoKg;
    public double pesoCargaKg;


    @Override
    public String toString() {
        return "Mapa{" +
                "data=" + data +
                ", transp=" + transp +
                ", entrega='" + entrega + '\'' +
                ", cargaAtual='" + cargaAtual + '\'' +
                ", frota='" + frota + '\'' +
                ", custoSpot=" + custoSpot +
                ", regiao=" + regiao +
                ", veiculo=" + veiculo +
                ", placa='" + placa + '\'' +
                ", veiculoIndisp=" + veiculoIndisp +
                ", placaIndisp=" + placaIndisp +
                ", frotaIndisp=" + frotaIndisp +
                ", tipoIndisp=" + tipoIndisp +
                ", mapa=" + mapa +
                ", entregas=" + entregas +
                ", cxCarreg=" + cxCarreg +
                ", cxEntreg=" + cxEntreg +
                ", ocupacao=" + ocupacao +
                ", cxRota=" + cxRota +
                ", cxAs=" + cxAs +
                ", veicBM=" + veicBM +
                ", rShow=" + rShow +
                ", entrVol='" + entrVol + '\'' +
                ", hrSai=" + hrSai +
                ", hrEntr=" + hrEntr +
                ", kmSai=" + kmSai +
                ", kmEntr=" + kmEntr +
                ", custoVariavel=" + custoVariavel +
                ", lucro=" + lucro +
                ", lucroUnit=" + lucroUnit +
                ", valorFrete=" + valorFrete +
                ", tipoImposto='" + tipoImposto + '\'' +
                ", percImposto=" + percImposto +
                ", valorImposto=" + valorImposto +
                ", valorFaturado=" + valorFaturado +
                ", valorUnitCxEntregue=" + valorUnitCxEntregue +
                ", valorPgCxEntregSemImp=" + valorPgCxEntregSemImp +
                ", valorPgCxEntregComImp=" + valorPgCxEntregComImp +
                ", tempoPrevistoRoad=" + tempoPrevistoRoad +
                ", kmPrevistoRoad=" + kmPrevistoRoad +
                ", valorUnitPontoMot=" + valorUnitPontoMot +
                ", valorUnitPontoAjd=" + valorUnitPontoAjd +
                ", valorEquipeEntrMot=" + valorEquipeEntrMot +
                ", valorEquipeEntrAjd=" + valorEquipeEntrAjd +
                ", custoVLC=" + custoVLC +
                ", lucroUnitCEDBZ=" + lucroUnitCEDBZ +
                ", CustoVlcCxEntr=" + CustoVlcCxEntr +
                ", tempoInterno=" + tempoInterno +
                ", valorDropDown=" + valorDropDown +
                ", veicCadDD='" + veicCadDD + '\'' +
                ", kmLaco=" + kmLaco +
                ", kmDeslocamento=" + kmDeslocamento +
                ", tempoLaco=" + tempoLaco +
                ", tempoDeslocamento=" + tempoDeslocamento +
                ", sitMultiCDD=" + sitMultiCDD +
                ", unbOrigem=" + unbOrigem +
                ", matricMotorista=" + matricMotorista +
                ", matricAjud1=" + matricAjud1 +
                ", matricAjud2=" + matricAjud2 +
                ", valorCTEDifere='" + valorCTEDifere + '\'' +
                ", qtNfCarregadas=" + qtNfCarregadas +
                ", qtNfEntregues=" + qtNfEntregues +
                ", indDevCx=" + indDevCx +
                ", indDevNf=" + indDevNf +
                ", fator=" + fator +
                ", recarga='" + recarga + '\'' +
                ", hrMatinal=" + hrMatinal +
                ", hrJornadaLiq=" + hrJornadaLiq +
                ", hrMetaJornada=" + hrMetaJornada +
                ", vlBateuJornMot=" + vlBateuJornMot +
                ", vlNaoBateuJornMot=" + vlNaoBateuJornMot +
                ", vlRecargaMot=" + vlRecargaMot +
                ", vlBateuJornAju=" + vlBateuJornAju +
                ", vlNaoBateuJornAju=" + vlNaoBateuJornAju +
                ", vlRecargaAju=" + vlRecargaAju +
                ", vlTotalMapa=" + vlTotalMapa +
                ", qtHlCarregados=" + qtHlCarregados +
                ", qtHlEntregues=" + qtHlEntregues +
                ", indiceDevHl=" + indiceDevHl +
                ", regiao2='" + regiao2 + '\'' +
                ", qtNfCarregGeral=" + qtNfCarregGeral +
                ", qtNfEntregGeral=" + qtNfEntregGeral +
                ", capacidadeVeiculoKg=" + capacidadeVeiculoKg +
                ", pesoCargaKg=" + pesoCargaKg +
                '}';
    }
}