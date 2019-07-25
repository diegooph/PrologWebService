package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.VeiculoPlanilha;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoConferenciaDaoImpl implements VeiculoConferenciaDao {


    @NotNull
    @Override
    public List<Long> insert(@NotNull List<VeiculoPlanilha> veiculoPLanilha) throws Throwable {

        Connection conn = null;
        int linha = 1;
       /* try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final List<Long> codigosPneus = new ArrayList<>(pneus.size());
            for (final Pneu pneu : pneus) {
                codigosPneus.add(internalInsert(conn, pneu, pneu.getCodUnidadeAlocado()));
                linha++;
            }
            conn.commit();
            return codigosPneus;
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new GenericException("Erro ao inserir pneu da linha: " + linha + " -- " + e.getMessage());
        } finally {
            close(conn);
        }*/
        return null;
    }
}
