package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe RelatoService responsavel por comunicar-se com a interface DAO
 */
public class RelatoService {
    private static final String TAG = RelatoService.class.getSimpleName();
    @NotNull
    private final RelatoDao dao = Injection.provideRelatoDao();

    public boolean insert(@NotNull final Relato relato,
                          @Nullable final Integer versaoApp) {
        try {
            return dao.insert(relato, versaoApp);
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

    public Relato getByCod(@NotNull final Long codigo, @NotNull final String userToken) {
        try {
            return dao.getByCod(codigo, userToken);
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

    public List<Relato> getRealizadosByColaborador(@NotNull final Long codColaborador,
                                                   final int limit,
                                                   final long offset,
                                                   final double latitude,
                                                   final double longitude,
                                                   final boolean isOrderByDate,
                                                   @NotNull final String status,
                                                   @NotNull final String campoFiltro) {
        try {
            return dao.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  campoFiltro);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os relatos. \n" +
                                             "codColaborador: %d \n" +
                                             "status: %s \n" +
                                             "limit: %d \n" +
                                             "offset: %d \n" +
                                             "latitude: %f \n" +
                                             "longitude: %f \n" +
                                             "isOrderByDate: %b",
                                     codColaborador,
                                     status,
                                     limit,
                                     offset,
                                     latitude,
                                     longitude,
                                     isOrderByDate), e);
            throw new RuntimeException("Erro ao buscar os relatos realizados pelo colaborador: " + codColaborador + " e status: " + status);
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

    public List<Relato> getAllExcetoColaborador(@NotNull final Long codColaborador,
                                                final int limit,
                                                final long offset,
                                                final double latitude,
                                                final double longitude,
                                                final boolean isOrderByDate, String status) {
        try {
            return dao.getAllExcetoColaborador(codColaborador, limit, offset, latitude, longitude, isOrderByDate, status);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar os relatos exceto colaborador: " + codColaborador + " e status: " + status);
        }
    }
}
