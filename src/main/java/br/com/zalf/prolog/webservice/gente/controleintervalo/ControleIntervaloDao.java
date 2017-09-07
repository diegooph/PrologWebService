package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 18/08/2017.
 */
public interface ControleIntervaloDao {

    List<TipoIntervalo> getTiposIntervalos (Long cpf, boolean withCargos) throws SQLException;

    Intervalo getIntervaloAberto (Long cpf, TipoIntervalo tipoInvervalo) throws SQLException;

    void insertIntervalo(Intervalo intervalo) throws SQLException;

    void updateIntervalo(Intervalo intervalo) throws SQLException;

    void insertOrUpdateIntervalo(Intervalo intervalo) throws SQLException;

    List<Intervalo> getIntervalosColaborador (Long cpf, String codTipo, long limit, long offset) throws SQLException;

    @NotNull
    long getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException;
}