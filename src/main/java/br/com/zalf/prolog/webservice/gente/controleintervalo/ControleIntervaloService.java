package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.EstadoVersaoIntervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.ResponseIntervalo;
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

    public ResponseIntervalo insertOrUpdateIntervalo(long versaoDadosIntervalo, Intervalo intervalo) {
        EstadoVersaoIntervalo estadoVersaoIntervalo = null;
        try {
            final long codUnidade = intervalo.getColaborador().getCodUnidade();
            final long versaoDadosBanco = dao.getVersaoDadosIntervaloByUnidade(codUnidade);
            if (versaoDadosIntervalo < versaoDadosBanco) {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_DESATUALIZADA;
            } else {
                estadoVersaoIntervalo = EstadoVersaoIntervalo.VERSAO_ATUALIZADA;
            }
            dao.insertOrUpdateIntervalo(intervalo);
            return ResponseIntervalo.ok("Intervalo inserido com sucesso", estadoVersaoIntervalo);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseIntervalo.error("Erro ao inserir intervalo", estadoVersaoIntervalo);
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
