package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    void verificarPlanilha(Long codUnidade, List<VeiculoPlanilha> veiculoPlanilha) throws SQLException;
}
