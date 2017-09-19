package br.com.zalf.prolog.webservice.imports.mapa;

import br.com.zalf.prolog.webservice.commons.network.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Classe MapaService responsavel por comunicar-se com a interface DAO
 */
public class MapaService {

    private MapaDao dao = new MapaDaoImpl();

    public Response insertOrUpdateMapa(String path, Long codUnidade) {
        try {
            if (dao.insertOrUpdateMapa(path, codUnidade)) {
                return Response.ok("Arquivo do mapa inserido com sucesso");
            } else {
                return Response.error("Problema ao inserir o arquivo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Erro relacionado ao banco de dados ao inserir o arquivo");
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("Erro relacionado ao processamento do arquivo");
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.error("Erro nos dados da planilha");
        }
    }

}
