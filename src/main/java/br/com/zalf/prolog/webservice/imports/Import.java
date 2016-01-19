package br.com.zalf.prolog.webservice.imports;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Created by jean on 18/01/16.
 */
public class Import {


	public static List<Mapa> mapa (String path){


		List<Mapa> listMapa = new ArrayList<>();

		try {
			Reader in = new FileReader(path);


			List<CSVRecord> tabela = CSVFormat.DEFAULT.parse(in).getRecords();

			for (int i = 1; i < tabela.size(); i++) {
				Mapa mapa = new Mapa();
				CSVRecord linha = tabela.get(i);
				try {
					mapa.data = toDate(linha.get(0));
					mapa.transp = Integer.parseInt(linha.get(1));
					mapa.entrega = linha.get(2).replace(" ","");
					mapa.cargaAtual = linha.get(3).replace(" ","");
					mapa.frota = linha.get(4).replace(" ","");
					mapa.custoSpot = Double.parseDouble(linha.get(5).replace(",","."));
					mapa.regiao = Integer.parseInt(linha.get(6));
					mapa.veiculo = Integer.parseInt(linha.get(7));
					mapa.placa = linha.get(8).replace(" ","");
					mapa.veiculoIndisp = Double.parseDouble(linha.get(9).replace(",","."));

					// inserir 0 caso venha em branco
					if(linha.get(10).trim().isEmpty()){
						mapa.placaIndisp = 0;
					}else{
						mapa.placaIndisp = Double.parseDouble(linha.get(10).replace(",","."));
					}
					// inserir 0 caso venha em branco
					if(linha.get(11).trim().isEmpty()){
						mapa.frotaIndisp = 0;
					}else{
						mapa.frotaIndisp = Double.parseDouble(linha.get(11).replace(",","."));}

					mapa.tipoIndisp = Integer.parseInt(linha.get(12));
					mapa.mapa = Integer.parseInt(linha.get(13));
					mapa.entregas = Integer.parseInt(linha.get(14));
					mapa.cxCarreg = Double.parseDouble(linha.get(15).replace(",","."));
					mapa.cxEntreg = Double.parseDouble(linha.get(16).replace(",","."));
					mapa.ocupacao = Double.parseDouble(linha.get(17).replace(",","."));
					mapa.cxRota = Double.parseDouble(linha.get(18).replace(",","."));
					mapa.cxAs = Double.parseDouble(linha.get(19).replace(",","."));
					mapa.veicBM = Double.parseDouble(linha.get(20).replace(",","."));
					mapa.rShow = Integer.parseInt(linha.get(21));
					mapa.entrVol = linha.get(22).replace(" ","");
					mapa.hrSai = toTimestamp(linha.get(23));
					mapa.hrEntr = toTimestamp(linha.get(24));
					mapa.kmSai = Integer.parseInt(linha.get(25));
					mapa.kmEntr = Integer.parseInt(linha.get(26));
					mapa.custoVariavel = Double.parseDouble(linha.get(27).replace(",","."));
					mapa.lucro = Double.parseDouble(linha.get(28).replace(",","."));
					mapa.lucroUnit = Double.parseDouble(linha.get(29).replace(",","."));
					mapa.valorFrete = Double.parseDouble(linha.get(30).replace(",","."));
					mapa.tipoImposto = linha.get(31).replace(" ","");
					mapa.percImposto = Double.parseDouble(linha.get(32).replace(",","."));
					mapa.valorImposto = Double.parseDouble(linha.get(33).replace(",","."));
					mapa.valorFaturado = Double.parseDouble(linha.get(34).replace(",","."));
					mapa.valorUnitCxEntregue = Double.parseDouble(linha.get(35).replace(",","."));
					mapa.valorPgCxEntregSemImp = Double.parseDouble(linha.get(36).replace(",","."));
					mapa.valorPgCxEntregComImp = Double.parseDouble(linha.get(37).replace(",","."));
					// realizar replace de " " e " ' " por vazio
					mapa.tempoPrevistoRoad = toTime(linha.get(38));
					mapa.kmPrevistoRoad = Double.parseDouble(linha.get(39).replace(",","."));
					mapa.valorUnitPontoMot = Double.parseDouble(linha.get(40).replace(",","."));
					mapa.valorUnitPontoAjd = Double.parseDouble(linha.get(41).replace(",","."));
					mapa.valorEquipeEntrMot = Double.parseDouble(linha.get(42).replace(",","."));
					mapa.valorEquipeEntrAjd = Double.parseDouble(linha.get(43).replace(",","."));
					mapa.custoVLC = Double.parseDouble(linha.get(44).replace(",","."));
					mapa.lucroUnitCEDBZ = Double.parseDouble(linha.get(45).replace(",","."));
					mapa.CustoVlcCxEntr = Double.parseDouble(linha.get(46).replace(",","."));
					mapa.tempoInterno = toTime(linha.get(47));
					mapa.valorDropDown = Double.parseDouble(linha.get(48).replace(",","."));
					mapa.veicCadDD = linha.get(49).replace(" ","");
					mapa.kmLaco = Double.parseDouble(linha.get(50).replace(",","."));
					mapa.kmDeslocamento = Double.parseDouble(linha.get(51).replace(",","."));
					//fazer replace
					mapa.tempoLaco = toTime(linha.get(52));
					//fazer replace
					mapa.tempoDeslocamento = toTime(linha.get(53));
					mapa.sitMultiCDD = Double.parseDouble(linha.get(54).replace(",","."));
					mapa.unbOrigem = Integer.parseInt(linha.get(55));
					mapa.matricMotorista = Integer.parseInt(linha.get(56));
					mapa.matricAjud1 = Integer.parseInt(linha.get(57));
					mapa.matricAjud2 = Integer.parseInt(linha.get(58));
					mapa.valorCTEDifere = linha.get(59).replace(" ","");
					mapa.qtNfCarregadas = Integer.parseInt(linha.get(60));
					mapa.qtNfEntregues = Integer.parseInt(linha.get(61));
					mapa.indDevCx = Double.parseDouble(linha.get(62).replace(",","."));
					mapa.indDevNf = Double.parseDouble(linha.get(63).replace(",","."));;
					mapa.fator = Double.parseDouble(linha.get(64).replace(",","."));
					mapa.recarga = linha.get(65).replace(" ","");
					mapa.hrMatinal = toTime(linha.get(66));
					mapa.hrJornadaLiq = toTime(linha.get(67));
					mapa.hrMetaJornada = toTime(linha.get(68));
					mapa.vlBateuJornMot = Double.parseDouble(linha.get(69).replace(",","."));
					mapa.vlNaoBateuJornMot = Double.parseDouble(linha.get(70).replace(",","."));
					mapa.vlRecargaMot = Double.parseDouble(linha.get(71).replace(",","."));
					mapa.vlBateuJornAju = Double.parseDouble(linha.get(72).replace(",","."));
					mapa.vlNaoBateuJornAju = Double.parseDouble(linha.get(73).replace(",","."));
					mapa.vlRecargaAju = Double.parseDouble(linha.get(74).replace(",","."));
					mapa.vlTotalMapa = Double.parseDouble(linha.get(75).replace(",","."));
					mapa.qtHlCarregados = Double.parseDouble(linha.get(76).replace(",","."));
					mapa.qtHlEntregues = Double.parseDouble(linha.get(77).replace(",","."));
					mapa.indiceDevHl = Double.parseDouble(linha.get(78).replace(",","."));
					mapa.regiao2 = linha.get(79).replace(" ","");
					mapa.qtNfCarregGeral = Integer.parseInt(linha.get(80));
					mapa.qtNfEntregGeral = Integer.parseInt(linha.get(81));
					mapa.capacidadeVeiculoKg = Double.parseDouble(linha.get(82).replace(",","."));
					mapa.pesoCargaKg = Double.parseDouble(linha.get(83).replace(",","."));
					listMapa.add(mapa);
				}catch (NumberFormatException ex){
					ex.printStackTrace();
				}
			}
		}

		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		return listMapa;

	}
		
	public static Date toDate(String data){

		String date = String.valueOf(data);
		int ano;
		int mes;
		int dia;
		Calendar calendar = Calendar.getInstance();

		if(date.length() == 7){
			ano = Integer.parseInt(date.substring(3,7));
			mes = Integer.parseInt(date.substring(1,3));
			dia = Integer.parseInt(date.substring(0,1));

		}else{
			ano = Integer.parseInt(date.substring(4,8));
			mes = Integer.parseInt(date.substring(2,4));
			dia = Integer.parseInt(date.substring(0,2));
		}
		calendar.set(Calendar.YEAR, ano);
		// calendario no java começa em 0, no 2art o mês começa em 1
		calendar.set(Calendar.MONTH, mes-1);
		calendar.set(Calendar.DAY_OF_MONTH, dia);

		return calendar.getTime();
	}

	public static Date toTimestamp(String data){
		DateFormat dateFormat;
		Date date = null;
		try {
			if(data.trim().replace(" ", "").length() == 13){
				dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
			}else{ dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");}
			date = dateFormat.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Time toTime(String hora){
		//// FIXME: 18/01/16
		//fazer replace de espaço e aspas simples por vazio
		// verificar zero adicional
		hora = hora.replace(" ","");
		if(hora.length() == 6){
			hora = hora.substring(1,6);
		}
		DateFormat dateFormat = new SimpleDateFormat("hh:mm");
		Time time = null;
		try {
			Date date = dateFormat.parse(hora);
			time = new Time(date.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

}
