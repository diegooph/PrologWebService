package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.imports.TrackingImport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jean on 18/01/16.
 */
public class Import {

	/*
	Tornar essa classe uma ImportUtils, com os métodos usados tanto pelo import do mapa
	quanto pelo import do tracking
	 */

	public static final Time EMPTY_TIME = new Time(0L);


	/**
 	* Converte um arquivo e .csv para uma lista de Tracking
 	* @param path um arquivo .csv com os dados do tracking
 	* @return uma lista de Tracking
 	* @see TrackingImport
 	*/
	public static List<TrackingImport> tracking (String path){

		List<TrackingImport> listTracking = new ArrayList<>();
		try {
			Reader in = new FileReader(path);
			//List<CSVRecord> tabela = CSVFormat.DEFAULT.parse(in).getRecords();
			List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
			for (int i = 1; i < tabela.size(); i++) {
				TrackingImport tracking = new TrackingImport();
				CSVRecord linha = tabela.get(i);
				if(!String.valueOf(linha.get(0)).trim().isEmpty()){
					tracking.mapa = Integer.parseInt(linha.get(0));
				}
				if(!String.valueOf(linha.get(1)).trim().isEmpty()){
					tracking.data = toTimestamp(linha.get(1));
				}
				if(!String.valueOf(linha.get(2)).trim().isEmpty()){
					tracking.mapa = Integer.parseInt(linha.get(2));
				}
				if(!String.valueOf(linha.get(3)).trim().isEmpty()){
					tracking.placa = String.valueOf(linha.get(3));
				}
				if(!String.valueOf(linha.get(4)).trim().isEmpty()){
					tracking.codCliente = Integer.parseInt(linha.get(4));
				}
				if(!String.valueOf(linha.get(5)).trim().isEmpty()){
					tracking.seqReal = Integer.parseInt(linha.get(5));
				}
				if(!String.valueOf(linha.get(6)).trim().isEmpty()){
					tracking.seqPlan = Integer.parseInt(linha.get(6));
				}
//				if(!String.valueOf(linha.get(7)).trim().equals(NAO_RELATADO)){
//					tracking.inicioRota = toTime(linha.get(7));
//				}
				if(containsNumber(linha.get(7))){
					tracking.inicioRota = toTime(linha.get(7));
				}
				if(!String.valueOf(linha.get(8)).trim().isEmpty()){
					tracking.horarioMatinal = toTime(linha.get(8));
				}
				if(!String.valueOf(linha.get(9)).trim().isEmpty()){
					tracking.saidaCDD = toTime(linha.get(9));
				}
//				if(!String.valueOf(linha.get(10)).trim().equals(NAO_RELATADO)){
//					tracking.chegadaPDV = toTime(linha.get(10));
//				}
				if(containsNumber(linha.get(10))){
					tracking.chegadaPDV = toTime(linha.get(10));
				}
//				if(!String.valueOf(linha.get(11)).trim().equals(NAO_RELATADO)){
//					tracking.tempoPrevRetorno = toTime(linha.get(11));
//				}
				if(containsNumber(linha.get(11))){
					tracking.tempoPrevRetorno = toTime(linha.get(11));
				}
//				if(!String.valueOf(linha.get(12)).trim().equals(NAO_RELATADO)){
//					tracking.tempoRetorno = toTime(linha.get(12));
//				}
				if(containsNumber(linha.get(12))){
					tracking.tempoRetorno = toTime(linha.get(12));
				}
//				if(!String.valueOf(linha.get(13)).trim().equals(NAO_RELATADO)){
//					tracking.distPrevRetorno = Double.parseDouble(linha.get(13).replace(",", "."));
//				}
				if(containsNumber(linha.get(13))){
					tracking.distPrevRetorno = Double.parseDouble(linha.get(13).replace(",", "."));
				}
//				if(!String.valueOf(linha.get(14)).trim().equals(NAO_RELATADO)){
//					tracking.distPercRetorno = Double.parseDouble(linha.get(14).replace(",", "."));
//				}
				if(containsNumber(linha.get(14))){
					tracking.distPercRetorno = Double.parseDouble(linha.get(14).replace(",", "."));
				}
//				if(!String.valueOf(linha.get(15)).trim().equals(NAO_RELATADO)){
//					tracking.inicioEntrega = toTime(linha.get(15));
//				}
				if(containsNumber(linha.get(15))){
					tracking.inicioEntrega = toTime(linha.get(15));
				}
//				if(!String.valueOf(linha.get(16)).trim().equals(NAO_RELATADO)){
//					tracking.fimEntrega = toTime(linha.get(16));
//				}
				if(containsNumber(linha.get(16))){
					tracking.fimEntrega = toTime(linha.get(16));
				}
//				if(!String.valueOf(linha.get(17)).trim().equals(NAO_RELATADO)){
//					tracking.fimRota = toTime(linha.get(17));
//				}
				if(containsNumber(linha.get(17))){
					tracking.fimRota = toTime(linha.get(17));
				}
//				if(!String.valueOf(linha.get(18)).trim().equals(NAO_RELATADO)){
//					tracking.entradaCDD = toTime(linha.get(18));
//				}
				if(containsNumber(linha.get(18))){
					tracking.entradaCDD = toTime(linha.get(18));
				}
				if(!String.valueOf(linha.get(19)).trim().isEmpty()){
					tracking.caixasCarregadas = Double.parseDouble(linha.get(19).replace(",", "."));
				}
				if(!String.valueOf(linha.get(20)).trim().isEmpty()){
					tracking.caixasDevolvidas = Double.parseDouble(linha.get(20).replace(",", "."));
				}
				if(!String.valueOf(linha.get(21)).trim().isEmpty()){
					tracking.repasse = Double.parseDouble(linha.get(21).replace(",", "."));
				}
				if(!String.valueOf(linha.get(22)).trim().isEmpty()){
					tracking.tempoEntrega = toTime(linha.get(22));
				}
				if(!String.valueOf(linha.get(23)).trim().isEmpty()){
					tracking.tempoDescarga = toTime(linha.get(23));
				}
				if(!String.valueOf(linha.get(24)).trim().isEmpty()){
					tracking.tempoEspera = toTime(linha.get(24));
				}
				if(!String.valueOf(linha.get(25)).trim().isEmpty()){
					tracking.tempoAlmoco = toTime(linha.get(25));
				}
				if(!String.valueOf(linha.get(26)).trim().isEmpty()){
					tracking.tempoTotalRota = toTime(linha.get(26));
				}
//				if(!String.valueOf(linha.get(27)).trim().equals(NAO_RELATADO)){
//					tracking.dispApontCadastrado = Double.parseDouble(linha.get(27).replace(",", "."));
//				}
				if(containsNumber(linha.get(27))){
					tracking.dispApontCadastrado = Double.parseDouble(linha.get(27).replace(",", "."));
				}
				if(!String.valueOf(linha.get(28)).trim().isEmpty()){
					tracking.latEntrega = linha.get(28).replace(",", ".");
				}
				if(!String.valueOf(linha.get(29)).trim().isEmpty()){
					tracking.lonEntrega = linha.get(29).replace(",", ".");
				}
				if(!String.valueOf(linha.get(30)).trim().isEmpty()){
					tracking.unidadeNegocio = Integer.parseInt(linha.get(30));
				}
				if(!String.valueOf(linha.get(31)).trim().isEmpty()){
					tracking.transportadora = linha.get(31).trim();
				}
				if(!String.valueOf(linha.get(32)).trim().isEmpty()){
					tracking.latClienteApontamento = linha.get(32).replace(",", ".");
				}
				if(!String.valueOf(linha.get(33)).trim().isEmpty()){
					tracking.lonClienteApontamento = linha.get(33).replace(",", ".");
				}
				if(!String.valueOf(linha.get(34)).trim().isEmpty()){
					tracking.latAtualCliente = linha.get(34).replace(",", ".");
				}
				if(!String.valueOf(linha.get(35)).trim().isEmpty()){
					tracking.lonAtualCliente = linha.get(35).replace(",", ".");
				}
				if(!String.valueOf(linha.get(36)).trim().isEmpty()){
					tracking.distanciaPrev = Double.parseDouble(linha.get(36).replace(",", "."));
				}
//				if(!String.valueOf(linha.get(37)).trim().equals(NAO_RELATADO)){
//					tracking.tempoDeslocamento = toTime(linha.get(37));
//				}
				if(containsNumber(linha.get(37))){
					tracking.tempoDeslocamento = toTime(linha.get(37));
				}
				if(!String.valueOf(linha.get(38)).trim().isEmpty()){
					tracking.velMedia = Double.parseDouble(linha.get(38).replace(",", "."));
				}
				if(!String.valueOf(linha.get(39)).trim().isEmpty()){
					tracking.distanciaPercApontamento = Double.parseDouble(linha.get(39).replace(",", "."));
				}
				if(!String.valueOf(linha.get(40)).trim().isEmpty()){
					tracking.aderenciaSequenciaEntrega = linha.get(40).trim();
				}
				if(!String.valueOf(linha.get(41)).trim().isEmpty()){
					tracking.aderenciaJanelaEntrega = linha.get(41).trim();
				}
				if(!String.valueOf(linha.get(42)).trim().isEmpty()){
					tracking.pdvLacrado = linha.get(42).trim();
				}
				listTracking.add(tracking);
			}
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
		return listTracking;

	}

	/**
	 * Método usado para verificar se uma string contém algum número
	 * @param str uma String
	 * @return um boolean
	 */
	public static boolean containsNumber(String str) {
        return str.matches(".*\\d+.*");
    }

	/**
	 * Converte uma string para Date
	 * @param data uma String contendo uma data
	 * @return um Date
	 */
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
			}else if(data.trim().length() == 10){
				dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			}else{ dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");}
			date = dateFormat.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Converte uma String para um Time
	 * @param hora uma String contendo uma hora
	 * @return um Time
	 */
	public static Time toTime(String hora){
		//// FIXME: 18/01/16
		//fazer replace de espaço e aspas simples por vazio
		// verificar zero adicional
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		hora = hora.replace(" ","");
		// verifica quando tem 2 espaços extras na hora (tabela mapa)
		if(hora.length() == 6){
			hora = hora.substring(1,6);
			dateFormat = new SimpleDateFormat("HH:mm");
		}
		if(hora.length() == 4 || hora.length() == 5 || hora.length() == 8){
			dateFormat = new SimpleDateFormat("HH:mm");
		}
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
