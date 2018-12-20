package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.gente.controlejornada.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 18/08/2017.
 */
@Deprecated
public interface DeprecatedControleIntervaloDao_1 {

    Intervalo getIntervaloAberto(Long cpf, TipoMarcacao tipoInvervalo) throws SQLException;

    void insertIntervalo(Intervalo intervalo) throws SQLException;

    void updateIntervalo(Intervalo intervalo) throws SQLException;

    void insertOrUpdateIntervalo(Intervalo intervalo) throws SQLException;

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