package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 07/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuTransferenciaDao {

    void insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                             @NotNull final List<String> codPneusCliente) throws Throwable;

}
