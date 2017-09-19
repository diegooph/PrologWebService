package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public class ProntuarioCondutorService {

    ProntuarioCondutorDao dao = new ProntuarioCondutorDaoImpl();

    public ProntuarioCondutor getProntuario(Long cpf) {
        try {
            return dao.getProntuario(cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getPontuacaoProntuario(Long cpf) {
        try {
            return dao.getPontuacaoProntuario(cpf);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProntuarioCondutor> getResumoProntuarios(Long codUnidade, String codEquipe) {
        try {
            return dao.getResumoProntuarios(codUnidade, codEquipe);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response insertOrUpdate(String path) {
        try {
            if (dao.insertOrUpdate(path)) {
                return Response.ok("Prontuários inseridos com sucesso.");
            } else {
                return Response.error("Erro ao inserir os prontuários");
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
