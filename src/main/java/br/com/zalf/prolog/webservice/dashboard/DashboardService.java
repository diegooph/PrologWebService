package br.com.zalf.prolog.webservice.dashboard;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DashboardService {
    private final String TAG = DashboardService.class.getSimpleName();
    private final DashboardDao dao = Injection.provideDashboardDao();

    public ComponentDataHolder getComponentByCodigo(@NotNull final Integer codigo) {
        try {
            return dao.getComponentByCodigo(codigo);
        } catch (SQLException ex) {
            Log.e(TAG, "Erro ao buscar o componente de código: " + codigo, ex);
            throw new RuntimeException(ex);
        }
    }

    public List<DashComponentResumido> getComponentesColaborador(@NotNull final String userToken) {
        try {
            return dao.getComponentesColaborador(userToken);
        } catch (SQLException ex) {
            Log.e(TAG, "Erro ao buscar os componentes para o colaborador com token: " + userToken, ex);
            throw new RuntimeException(ex);
        }
    }
}