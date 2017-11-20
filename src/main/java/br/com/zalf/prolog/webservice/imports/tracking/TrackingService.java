package br.com.zalf.prolog.webservice.imports.tracking;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Classe TrackingService responsavel por comunicar-se com a interface DAO
 */
public class TrackingService {
	
	private TrackingDao dao = new TrackingDaoImpl();
	private static final String TAG = TrackingService.class.getSimpleName();

	public Response insertOrUpdateTracking (String path, Long codUnidade){
		try{
			if(dao.insertOrUpdateTracking(path, codUnidade)){
				return Response.ok("Arquivo do tracking inserido com sucesso");
			}else{
				return Response.error("Problema ao inserir o arquivo");
			}
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro relacionado ao banco de dados ao inserir o arquivo. \n" +
					"codUnidade: %d", codUnidade), e);
			return Response.error("Erro relacionado ao banco de dados ao inserir o arquivo");
		}catch (IOException e){
			Log.e(TAG, String.format("Erro relacionado ao processamento arquivo. \n" +
					"codUnidade: %d", codUnidade), e);
			return Response.error("Erro relacionado ao processamento do arquivo");
		}catch (ParseException e) {
			Log.e(TAG, String.format("Erro relacionado aos dados da planilha. \n" +
					"codUnidade: %d", codUnidade), e);
			return Response.error("Erro nos dados da planilha");
		}
	}

}
