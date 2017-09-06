package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 19/08/2017.
 */
public class ControleIntervaloService {

    private ControleIntervaloDao dao = new ControleIntervaloDaoImpl();

    public List<TipoIntervalo> getTiposIntervalos(Long cpf, boolean withCargos) {
        try {
            return dao.getTiposIntervalos(cpf, withCargos);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Intervalo getIntervaloAberto(Long cpf, TipoIntervalo tipoInvervalo) throws Exception {
        try {
            return dao.getIntervaloAberto(cpf, tipoInvervalo);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    boolean insertIntervalo(@NotNull final Intervalo intervalo) {
        try {
            dao.insertIntervalo(intervalo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) {
        try {
            return dao.iniciaIntervalo(codUnidade, cpf, codTipo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insereFinalizacaoIntervalo(Intervalo intervalo, Long codUnidade) {
        try {
            return dao.insereFinalizacaoIntervalo(intervalo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Intervalo> getIntervalosColaborador(Long cpf, String codTipo,long limit ,long offset) {
        try {
            return dao.getIntervalosColaborador(cpf, codTipo, limit, offset);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
