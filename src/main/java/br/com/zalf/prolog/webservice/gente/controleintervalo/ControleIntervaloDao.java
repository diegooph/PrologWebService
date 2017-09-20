package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 18/08/2017.
 */
public interface ControleIntervaloDao {

    List<TipoIntervalo> getTiposIntervalosByUnidade(Long cpf, boolean withCargos) throws SQLException;

    Intervalo getIntervaloAberto(Long cpf, TipoIntervalo tipoInvervalo) throws SQLException;

    void insertIntervalo(Intervalo intervalo) throws SQLException;

    void updateIntervalo(Intervalo intervalo) throws SQLException;

    void insertOrUpdateIntervalo(Intervalo intervalo) throws SQLException;

    void updateTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    List<Intervalo> getIntervalosColaborador(Long cpf, String codTipo, long limit, long offset) throws SQLException;

    @NotNull
    Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException;

    @Deprecated
    Long iniciaIntervalo (Long codUnidade, Long cpf, Long codTipo) throws SQLException;

    @Deprecated
    boolean finalizaIntervaloEmAberto (Intervalo intervalo) throws SQLException;

    @Deprecated
    boolean insereFinalizacaoIntervalo (Intervalo intervalo, Long codUnidade) throws SQLException;
}