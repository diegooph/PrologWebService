package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import org.jetbrains.annotations.NotNull;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItem;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItemVisualizacao;

import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuNomenclaturaDao {


    /**
     * Insere ou atualiza as informações de uma {@link PneuNomenclaturaItem pneuNomenclaturaItem}.
     *
     * @param pneuNomenclaturaItem    Lista de objetos contendo as informações para a pneuNomenclaturaItem.
     * @param userToken o token do usuário que fez a requisição.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void insertOrUpdateNomenclatura(@NotNull final List<PneuNomenclaturaItem> pneuNomenclaturaItem,
                                    @NotNull final String userToken) throws Throwable;

    /**
     * Retorna as nomenclaturas de um diagrama - {@link PneuNomenclaturaItemVisualizacao pneuNomenclaturaItemVisualizacao}.
     *
     * @param codEmpresa    codigo da empresa.
     * @param codDiagrama    codigo do diagrama.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(@NotNull final Long codEmpresa,
                                                                               @NotNull final Long codDiagrama) throws Throwable;

}
