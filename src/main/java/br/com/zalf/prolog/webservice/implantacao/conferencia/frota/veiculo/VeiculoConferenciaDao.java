package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.*;

import java.util.List;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    /**
     * Confere veiculos em lote.
     *
     * @param veiculoPLanilha planilha para conferência.
     * @return Planilha com erros encontrados.
     * @throws Throwable Caso ocorra erro no banco.
     */
    @NotNull
    List<Long> insert(@NotNull final List<VeiculoPlanilha> veiculoPLanilha) throws Throwable;
}
