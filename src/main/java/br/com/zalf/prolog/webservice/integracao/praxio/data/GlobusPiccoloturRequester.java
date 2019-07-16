package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface GlobusPiccoloturRequester {

    Long insertItensNok(
            @NotNull final OrdemDeServicoCorretivaPrologVO ordemDeServicoCorretivaPrologVO) throws Throwable;
}
