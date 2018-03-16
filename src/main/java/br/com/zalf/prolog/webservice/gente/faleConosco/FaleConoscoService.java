package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Classe FaleConoscoService responsavel por comunicar-se com a interface DAO
 */
public class FaleConoscoService {
    private static final String TAG = FaleConoscoService.class.getSimpleName();
    private final FaleConoscoDao dao = Injection.provideFaleConoscoDao();

    public AbstractResponse insert(FaleConosco faleConosco, Long codUnidade) {
        try {
            return ResponseWithCod.ok("Fale conosco inserido com sucesso.", dao.insert(faleConosco, codUnidade));
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao inserir o fale conosco. \n" +
                    "codUnidade: %d", codUnidade), e);
            return Response.error("Erro ao inserir fale conosco.");
        }
    }

    public boolean insertFeedback(FaleConosco faleConosco, Long codUnidade) {
        try {
            return dao.insertFeedback(faleConosco, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o feedback no fale conosco. \n" +
                    "codUnidade: %d", codUnidade), e);
            return false;
        }
    }

    public List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset,
                                    String cpf, String equipe, Long codUnidade, String status, String categoria) {

        try {
            return dao.getAll(dataInicial, dataFinal, limit, offset, cpf, equipe, codUnidade, status, categoria);
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os fale conosco. \n" +
                            "codUnidade: %d \n" +
                            "equipe: %s \n" +
                            "cpf: %s \n" +
                            "status: %s \n" +
                            "categoria: %s \n" +
                            "limit: %d \n" +
                            "offset: %d \n" +
                            "dataInicial: %s \n" +
                            "dataFinal: %s", codUnidade, equipe, cpf, status, categoria, limit, offset, new Date(dataInicial).toString(),
                    new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public List<FaleConosco> getByColaborador(Long cpf, String status) {
        try {
            return dao.getByColaborador(cpf, status);
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os fale conosco do colaborador. \n" +
                    "cpf: %d \n" +
                    "status: %s", cpf, status), e);
            return null;
        }
    }
}