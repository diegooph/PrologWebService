package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 19/08/2017.
 */
public class ControleIntervaloService {

    private ControleIntervaloDao dao = new ControleIntervaloDaoImpl();

    public List<TipoIntervalo> getTiposIntervalos(Long codUnidade, boolean withCargos) {
        try {
            return dao.getTiposIntervalos(codUnidade, withCargos);
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

    public boolean insertOrUpdateIntervalo(long versaoDadosIntervalo, Intervalo intervalo)
            throws VersaoDadosIntervaloDesatualizadaException {
        final long codUnidade = intervalo.getColaborador().getCodUnidade();
        if (versaoDadosIntervalo <= 0) {
            throw new VersaoDadosIntervaloDesatualizadaException(codUnidade, versaoDadosIntervalo);
        }

        try {
            final long versaoDadosBanco = dao.getVersaoDadosIntervaloByUnidade(codUnidade);
            if (versaoDadosBanco > versaoDadosIntervalo) {
                throw new VersaoDadosIntervaloDesatualizadaException(codUnidade, versaoDadosIntervalo);
            }
            dao.insertOrUpdateIntervalo(intervalo);
            return true;
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


    @Deprecated
    public boolean insertOrUpdateIntervalo(Intervalo intervalo) {
        try {
            dao.insertOrUpdateIntervalo(intervalo);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
