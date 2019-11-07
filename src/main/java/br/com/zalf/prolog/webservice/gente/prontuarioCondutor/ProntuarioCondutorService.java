package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public final class ProntuarioCondutorService {
    private static final String TAG = ProntuarioCondutorService.class.getSimpleName();
    private final ProntuarioCondutorDao dao = Injection.provideProntuarioCondutorDao();

    public ProntuarioCondutor getProntuario(@NotNull final Long cpf) {
        try {
            return dao.getProntuario(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o prontuário do colaborador. \n" +
                    "cpf: %d", cpf), e);
            return null;
        }
    }

    public Double getPontuacaoProntuario(@NotNull final Long cpf) {
        try {
            return dao.getPontuacaoProntuario(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a pontuação do prontuário do colaborador. \n" +
                    "cpf: %d", cpf), e);
            return null;
        }
    }

    public List<ProntuarioCondutor> getResumoProntuarios(@NotNull final Long codUnidade,
                                                         @NotNull final String codEquipe) {
        try {
            return dao.getResumoProntuarios(codUnidade, codEquipe);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o resumo dos prontuários. \n" +
                    "codUnidade: %d \n" +
                    "codEquipe: %s", codUnidade, codEquipe), e);
            return null;
        }
    }

    public Response insertOrUpdate(@NotNull final String path) {
        try {
            if (dao.insertOrUpdate(path)) {
                return Response.ok("Prontuários inseridos com sucesso.");
            } else {
                return Response.error("Erro ao inserir os prontuários");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Erro relacionado ao banco de dados ao inserir ou atualizar o prontuário", e);
            return Response.error("Erro relacionado ao banco de dados ao inserir o arquivo");
        } catch (IOException e) {
            Log.e(TAG, "Erro relacionado ao processamento do arquivo ao inserir ou atualizar o prontuário", e);
            return Response.error("Erro relacionado ao processamento do arquivo");
        } catch (ParseException e) {
            Log.e(TAG, "Erro nos dados da planilha ao inserir ou atualizar o prontuário", e);
            return Response.error("Erro nos dados da planilha");
        }
    }
}