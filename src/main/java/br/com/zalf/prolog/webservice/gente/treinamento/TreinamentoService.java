package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.S3FileSender;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe TreinamentoService responsavel por comunicar-se com a interface DAO
 */
public class TreinamentoService {
    private static final String TAG = TreinamentoService.class.getSimpleName();
    private final TreinamentoDao dao = Injection.provideTreinamentoDao();

    public List<Treinamento> getVistosByColaborador(Long cpf) {
        try {
            return dao.getVistosColaborador(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os treinamentos vistos do colaborador. \n" +
                    "cpf: %d", cpf), e);
            throw new RuntimeException("Erro ao buscar treinamentos vistos pelo colaborador " + cpf);
        }
    }

    public List<Treinamento> getAll(Long dataInicial, Long dataFinal, String codFuncao,
                                    Long codUnidade, Boolean comCargosLiberados,
                                    boolean apenasLiberados, long limit, long offset) {
        try {
            return dao.getAll(dataInicial, dataFinal, codFuncao, codUnidade, comCargosLiberados,
                    apenasLiberados, limit, offset);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os treinamentos. \n" +
                    "codUnidade: %d \n" +
                    "codFuncao: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s \n" +
                    "comCargosLiberador: %b \n" +
                    "apenasLiberador: %b \n" +
                    "limit: %d \n" +
                    "offset: %d", codUnidade, codFuncao, dataInicial, dataFinal, comCargosLiberados, apenasLiberados, limit, offset), e);
            throw new RuntimeException("Erro ao buscar treinamentos");
        }
    }

    public Treinamento getByCod(Long codTreinamento, Long codUnidade) {
        try {
            return dao.getTreinamentoByCod(codTreinamento, codUnidade, true);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o treinamento. \n" +
                    "codUnidade: %d \n" +
                    "codTreinamento: %d", codUnidade, codTreinamento), e);
            throw new RuntimeException("Erro ao buscar treinamento com código: " + codTreinamento);
        }
    }

    public List<Treinamento> getNaoVistosByColaborador(Long cpf) {
        try {
            return dao.getNaoVistosColaborador(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os treinamentos não vistos do colaborador. \n" +
                    "cpf: %d", cpf), e);
            throw new RuntimeException("Erro ao buscar treinamentos não vistos pelo colaborador " + cpf);
        }
    }

    public boolean marcarTreinamentoComoVisto(Long codTreinamento, Long cpf) {
        try {
            return dao.marcarTreinamentoComoVisto(codTreinamento, cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao marcar o treinamento como visto. \n" +
                    "cpf: %d \n" +
                    "codTreinamento: %d", cpf, codTreinamento), e);
            return false;
        }
    }

    public Long insert(InputStream file, Treinamento treinamento) {
        try {
            PDFTransformer transformer = new PDFTransformer();
            UploadTreinamentoHelper helper = new UploadTreinamentoHelper(transformer);
            return dao.insert(helper.upload(treinamento, file));
        } catch (SQLException | IOException | S3FileSender.S3FileSenderException e) {
            Log.e(TAG, "Erro ao inserir o treinamento.", e);
            return null;
        }
    }

    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) {
        try {
            return dao.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscas os treinamentos vistos por colaborador. \n" +
                    "codUnidade: %d \n" +
                    "codTreinamento: %d", codUnidade, codTreinamento), e);
            return null;
        }
    }

    public boolean updateTreinamento(Treinamento treinamento) {
        try {
            return dao.updateTreinamento(treinamento);
        }catch (SQLException e){
            Log.e(TAG, "Erro ao atualizar o treinamento", e);
            return false;
        }
    }

    public boolean updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) {
        try {
            return dao.updateUrlImagensTreinamento(urls, codTreinamento);
        }catch (SQLException e){
            Log.e(TAG, String.format("Erro ao atualizar as url das imagens do treinamento. \n" +
                    "codTreinamento: %d", codTreinamento), e);
            return false;
        }
    }

    public boolean deleteTreinamento(Long codTreinamento) {
        try {
            return dao.deleteTreinamento(codTreinamento);
        }catch (SQLException e){
            Log.e(TAG, String .format("Erro ao deletar o treinamento. \n" +
                    "codTreinamento: %d", codTreinamento), e);
            return false;
        }
    }
}
