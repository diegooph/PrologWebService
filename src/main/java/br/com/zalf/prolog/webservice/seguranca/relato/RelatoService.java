package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe RelatoService responsavel por comunicar-se com a interface DAO
 */
public class RelatoService {

    private RelatoDao dao = new RelatoDaoImpl();
    private static final String TAG = RelatoService.class.getSimpleName();

    public boolean insert(Relato relato) {
        try {
            return dao.insert(relato);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir o relato", e);
            return false;
        }
    }

    public boolean delete(Long codRelato) {
        try {
            return dao.delete(codRelato);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o relato. \n" +
                    "codRelato: %d", codRelato), e);
            return false;
        }
    }

    public Relato getByCod(Long codigo) {
        try {
            return dao.getByCod(codigo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relato. \n" +
                    "codigo: %d", codigo), e);
            return null;
        }
    }

    public List<Relato> getAll(Long codUnidade, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) {
        try {
            return dao.getAll(codUnidade, limit, offset, latitude, longitude, isOrderByDate, status);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os relatos. \n" +
                    "codUnidade: %d \n" +
                    "status: %s \n" +
                    "limit: %d \n" +
                    "offset: %d \n" +
                    "latitude: %f \n" +
                    "longitude: %f \n" +
                    "isOrderByDate: %b", codUnidade, status, limit, offset, latitude, longitude, isOrderByDate), e);
            throw new RuntimeException("Erro ao buscar os relatos para unidade: " + codUnidade + " e status: " + status);
        }
    }

    public List<Relato> getRealizadosByColaborador(Long cpf, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status, String campoFiltro) {
        try {
            return dao.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status, campoFiltro);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os relatos. \n" +
                    "cpf: %d \n" +
                    "status: %s \n" +
                    "limit: %d \n" +
                    "offset: %d \n" +
                    "latitude: %f \n" +
                    "longitude: %f \n" +
                    "isOrderByDate: %b", cpf, status, limit, offset, latitude, longitude, isOrderByDate), e);
            throw new RuntimeException("Erro ao buscar os relatos realizados pelo colaborador: " + cpf + " e status: " + status);
        }
    }

    public boolean classificaRelato(Relato relato) {
        try {
            return dao.classificaRelato(relato);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao classificar o relato", e);
            return false;
        }
    }

    public boolean fechaRelato(Relato relato) {
        try {
            return dao.fechaRelato(relato);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao fechar o relato", e);
            return false;
        }
    }

    public List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
                                        Long codUnidade, long limit, long offset, String status) {
        try {
            return dao.getAllByUnidade(dataInicial, dataFinal, equipe, codUnidade, limit, offset, status);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar os relatos unidade: " + codUnidade + " e status: " + status);
        }
    }

    public List<Relato> getAllExcetoColaborador(Long cpf, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) {
        try {
            return dao.getAllExcetoColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar os relatos exceto colaborador: " + cpf + " e status: " + status);
        }
    }
}
