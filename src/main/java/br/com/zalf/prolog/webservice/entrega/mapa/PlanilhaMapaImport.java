package br.com.zalf.prolog.webservice.entrega.mapa;

import com.univocity.parsers.annotations.Parsed;
import lombok.Getter;
import lombok.Setter;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@Setter
public class PlanilhaMapaImport {
    @Parsed(index = 0)
    private String data;

    @Parsed(index = 1)
    private String transp;

    @Parsed(index = 2)
    private String entrega;

    @Parsed(index = 3)
    private String cargaAtual;

    @Parsed(index = 4)
    private String frota;

    @Parsed(index = 5)
    private String custoSpot;

    @Parsed(index = 6)
    private String regiao;

    @Parsed(index = 7)
    private String veiculo;

    @Parsed(index = 8)
    private String placa;

    @Parsed(index = 9)
    private String veiculoIndisp;

    @Parsed(index = 10)
    private String placaIndisp;

    @Parsed(index = 11)
    private String frotaIndisp;

    @Parsed(index = 12)
    private String tipoIndisp;

    @Parsed(index = 13)
    private String mapa;

    @Parsed(index = 14)
    private String entregas;

    @Parsed(index = 15)
    private String cxCarreg;

    @Parsed(index = 16)
    private String cxEntreg;

    @Parsed(index = 17)
    private String ocupacao;

    @Parsed(index = 18)
    private String cxRota;

    @Parsed(index = 19)
    private String cxAs;

    @Parsed(index = 20)
    private String veicBM;

    @Parsed(index = 21)
    private String rShow;

    @Parsed(index = 22)
    private String entrVol;

    @Parsed(index = 23)
    private String hrSai;

    @Parsed(index = 24)
    private String hrEntr;

    @Parsed(index = 25)
    private String kmSai;

    @Parsed(index = 26)
    private String kmEntr;

    @Parsed(index = 27)
    private String custoVariavel;

    @Parsed(index = 28)
    private String lucro;

    @Parsed(index = 29)
    private String lucroUnit;

    @Parsed(index = 30)
    private String valorFrete;

    @Parsed(index = 31)
    private String tipoImposto;

    @Parsed(index = 32)
    private String percImposto;

    @Parsed(index = 33)
    private String valorImposto;

    @Parsed(index = 34)
    private String valorFaturado;

    @Parsed(index = 35)
    private String valorUnitCxEntregue;

    @Parsed(index = 36)
    private String valorPgCxEntregSemImp;

    @Parsed(index = 37)
    private String valorPgCxEntregComImp;

    @Parsed(index = 38)
    private String tempoPrevistoRoad;

    @Parsed(index = 39)
    private String kmPrevistoRoad;

    @Parsed(index = 40)
    private String valorUnitPontoMot;

    @Parsed(index = 41)
    private String valorUnitPontoAjd;

    @Parsed(index = 42)
    private String valorEquipeEntrMot;

    @Parsed(index = 43)
    private String valorEquipeEntrAjd;

    @Parsed(index = 44)
    private String custoVLC;

    @Parsed(index = 45)
    private String lucroUnitCEDBZ;

    @Parsed(index = 46)
    private String CustoVlcCxEntr;

    @Parsed(index = 47)
    private String tempoInterno;

    @Parsed(index = 48)
    private String valorDropDown;

    @Parsed(index = 49)
    private String veicCadDD;

    @Parsed(index = 50)
    private String kmLaco;

    @Parsed(index = 51)
    private String kmDeslocamento;

    @Parsed(index = 52)
    private String tempoLaco;

    @Parsed(index = 53)
    private String tempoDeslocamento;

    @Parsed(index = 54)
    private String sitMultiCDD;

    @Parsed(index = 55)
    private String unbOrigem;

    @Parsed(index = 56)
    private String matricMotorista;

    @Parsed(index = 57)
    private String matricAjud1;

    @Parsed(index = 58)
    private String matricAjud2;

    @Parsed(index = 59)
    private String valorCTEDifere;

    @Parsed(index = 60)
    private String qtNfCarregadas;

    @Parsed(index = 61)
    private String qtNfEntregues;

    @Parsed(index = 62)
    private String indDevCx;

    @Parsed(index = 63)
    private String indDevNf;

    @Parsed(index = 64)
    private String fator;

    @Parsed(index = 65)
    private String recarga;

    @Parsed(index = 66)
    private String hrMatinal;

    @Parsed(index = 67)
    private String hrJornadaLiq;

    @Parsed(index = 68)
    private String hrMetaJornada;

    @Parsed(index = 69)
    private String vlBateuJornMot;

    @Parsed(index = 70)
    private String vlNaoBateuJornMot;

    @Parsed(index = 71)
    private String vlRecargaMot;

    @Parsed(index = 72)
    private String vlBateuJornAju;

    @Parsed(index = 73)
    private String vlNaoBateuJornAju;

    @Parsed(index = 74)
    private String vlRecargaAju;

    @Parsed(index = 75)
    private String vlTotalMapa;

    @Parsed(index = 76)
    private String qtHlCarregados;

    @Parsed(index = 77)
    private String qtHlEntregues;

    @Parsed(index = 78)
    private String indiceDevHl;

    @Parsed(index = 79)
    private String regiao2;

    @Parsed(index = 80)
    private String qtNfCarregGeral;

    @Parsed(index = 81)
    private String qtNfEntregGeral;

    @Parsed(index = 82)
    private String capacidadeVeiculoKg;

    @Parsed(index = 83)
    private String pesoCargaKg;

    @Parsed(index = 84)
    private String capacVeiculoCx;

    @Parsed(index = 85)
    private String entregasCompletas;

    @Parsed(index = 86)
    private String entregasParciais;

    @Parsed(index = 87)
    private String entregasNaoRealizadas;

    @Parsed(index = 88)
    private String codFilial;

    @Parsed(index = 89)
    private String nomeFilial;

    @Parsed(index = 90)
    private String codSupervTrs;

    @Parsed(index = 91)
    private String nomeSupervTrs;

    @Parsed(index = 92)
    private String codSpot;

    @Parsed(index = 93)
    private String nomeSpot;

    @Parsed(index = 94)
    private String equipCarregados;

    @Parsed(index = 95)
    private String equipDevolvidos;

    @Parsed(index = 96)
    private String equipRecolhidos;

    @Parsed(index = 97)
    private String cxEntregTracking;

    @Parsed(index = 98)
    private String hrCarreg;

    @Parsed(index = 99)
    private String hrPCFisica;

    @Parsed(index = 100)
    private String hrPCFinanceira;

    @Parsed(index = 101)
    private String stMapa;

    @Parsed(index = 102)
    private String classificacaoRoadShow;

    @Parsed(index = 103)
    private String dataEntrega;

    @Parsed(index = 104)
    private String qtdEntregasCarregRv;

    @Parsed(index = 105)
    private String qtdEntregasEntregRv;

    @Parsed(index = 106)
    private String indiceDevEntregas;

    @Parsed(index = 107)
    private String cpfMotorista;

    @Parsed(index = 108)
    private String cpfAjudante1;

    @Parsed(index = 109)
    private String cpfAjudante2;

    @Parsed(index = 110)
    private String inicioRota;

    @Parsed(index = 111)
    private String terminoRota;

    @Parsed(index = 112)
    private String motoristaJt12x36;

    @Parsed(index = 113)
    private String retira;
}
