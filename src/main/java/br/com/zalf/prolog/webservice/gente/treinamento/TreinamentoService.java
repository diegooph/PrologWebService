package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe TreinamentoService responsavel por comunicar-se com a interface DAO
 */
public class TreinamentoService {
    private static final String TAG = TreinamentoService.class.getSimpleName();
    private final TreinamentoDao dao = Injection.provideTreinamentoDao();

    public List<Treinamento> getVistosByColaborador(final Long codColaborador) {
        try {
            return dao.getVistosColaborador(codColaborador);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os treinamentos vistos do colaborador. \n" +
                    "código: %d", codColaborador), e);
            throw new RuntimeException("Erro ao buscar treinamentos vistos pelo colaborador " + codColaborador);
        }
    }

    public List<Treinamento> getAll(final Long dataInicial, final Long dataFinal, final String codFuncao,
                                    final Long codUnidade, final Boolean comCargosLiberados,
                                    final boolean apenasLiberados, final long limit, final long offset) {
        try {
            return dao.getAll(dataInicial, dataFinal, codFuncao, codUnidade, comCargosLiberados,
                    apenasLiberados, limit, offset);
        } catch (final SQLException e) {
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

    public Treinamento getByCod(final Long codTreinamento, final Long codUnidade) {
        try {
            return dao.getTreinamentoByCod(codTreinamento, codUnidade, true);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o treinamento. \n" +
                    "codUnidade: %d \n" +
                    "codTreinamento: %d", codUnidade, codTreinamento), e);
            throw new RuntimeException("Erro ao buscar treinamento com código: " + codTreinamento);
        }
    }

    public List<Treinamento> getNaoVistosByColaborador(final Long codColaborador) {
        try {
            return dao.getNaoVistosColaborador(codColaborador);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os treinamentos não vistos do colaborador. \n" +
                    "código: %d", codColaborador), e);
            throw new RuntimeException("Erro ao buscar treinamentos não vistos pelo colaborador " + codColaborador);
        }
    }

    public boolean marcarTreinamentoComoVisto(final Long codTreinamento, final Long codColaborador) {
        try {
            return dao.marcarTreinamentoComoVisto(codTreinamento, codColaborador);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao marcar o treinamento como visto. \n" +
                    "cpf: %d \n" +
                    "codTreinamento: %d", codColaborador, codTreinamento), e);
            return false;
        }
    }

    public Long insert(final InputStream file, final Treinamento treinamento) {
        try {
            final PDFTransformer transformer = new PDFTransformer();
            final UploadTreinamentoHelper helper = new UploadTreinamentoHelper(transformer);
            return dao.insert(helper.upload(treinamento, file));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir o treinamento", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao cadastrar treinamento, tente novamente");
        }
    }

    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(final Long codTreinamento, final Long codUnidade) {
        try {
            return dao.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscas os treinamentos vistos por colaborador. \n" +
                    "codUnidade: %d \n" +
                    "codTreinamento: %d", codUnidade, codTreinamento), e);
            return null;
        }
    }

    public boolean updateTreinamento(final Treinamento treinamento) {
        try {
            return dao.updateTreinamento(treinamento);
        }catch (final SQLException e){
            Log.e(TAG, "Erro ao atualizar o treinamento", e);
            return false;
        }
    }

    public boolean updateUrlImagensTreinamento(final List<String> urls, final Long codTreinamento) {
        try {
            return dao.updateUrlImagensTreinamento(urls, codTreinamento);
        }catch (final SQLException e){
            Log.e(TAG, String.format("Erro ao atualizar as url das imagens do treinamento. \n" +
                    "codTreinamento: %d", codTreinamento), e);
            return false;
        }
    }

    public boolean deleteTreinamento(final Long codTreinamento) {
        try {
            return dao.deleteTreinamento(codTreinamento);
        }catch (final SQLException e){
            Log.e(TAG, String .format("Erro ao deletar o treinamento. \n" +
                    "codTreinamento: %d", codTreinamento), e);
            return false;
        }
    }
}
