package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuNomenclaturaDao {

    void insertOrUpdateNomenclatura(@NotNull final PneuNomenclaturaCadastro pneuNomenclaturaCadastro,
                                    @NotNull final String userToken) throws Throwable;

    @NotNull
    List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(@NotNull final Long codEmpresa,
                                                                               @NotNull final Long codDiagrama)
            throws Throwable;
}