package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;

import java.sql.SQLException;

/**
 * Created by Zart on 18/08/2017.
 */
@Deprecated
public interface DeprecatedControleIntervaloDao {

    @Deprecated
    Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) throws SQLException;

    @Deprecated
    boolean insereFinalizacaoIntervalo(Intervalo intervalo, Long codUnidade) throws SQLException;
}