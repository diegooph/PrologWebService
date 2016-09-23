package br.com.zalf.prolog.webservice.imports.mapa;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.network.Response;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Classe MapaService responsavel por comunicar-se com a interface DAO
 */
public class MapaService {
	
	private MapaDao dao = new MapaDaoImpl();

	public Response insertOrUpdateMapa (String path, Colaborador colaborador){
		try {
			if(dao.insertOrUpdateMapa(path, colaborador)){
				return Response.Ok("Arquivo inserido com sucesso");
			}else{
				return Response.Error("Problema ao inserir o arquivo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.Error("Erro relacionado ao banco de dados ao inserir o arquivo");
		}catch (IOException e){
			e.printStackTrace();
			return Response.Error("Erro relacionado ao processamento do arquivo");
		}
	}

}
