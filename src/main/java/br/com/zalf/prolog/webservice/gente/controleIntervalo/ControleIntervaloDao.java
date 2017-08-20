package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 18/08/2017.
 */
public interface ControleIntervaloDao {

    List<TipoIntervalo> getTiposIntervalos (Long cpf, boolean withCargos) throws SQLException;

    Intervalo getIntervaloAberto (Long cpf, TipoIntervalo tipoInvervalo) throws SQLException;

    /**
     * Inicia um novo intervalo
     * @param codUnidade
     * @param cpf
     * @param codTipo
     * @return
     * @throws SQLException
     */
    Long iniciaIntervalo (Long codUnidade, Long cpf, Long codTipo) throws SQLException;

    /**
     * Finaliza um intervalo que está em aberto
     * @param intervalo
     * @return
     * @throws SQLException
     */
    boolean finalizaIntervaloEmAberto (Intervalo intervalo) throws SQLException;

    /**
     * Insere um intervalo que não teve início
     * @param intervalo
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    boolean insereFinalizacaoIntervalo (Intervalo intervalo, Long codUnidade) throws SQLException;

    /**
     * Insere um intervalo
     * @param intervalo
     * @param codUnidade
     * @param conn
     * @return
     * @throws SQLException
     */
    Long insertIntervalo(Intervalo intervalo, Long codUnidade, Connection conn) throws SQLException;

    List<Intervalo> getIntervalosColaborador (Long cpf) throws SQLException;

}
