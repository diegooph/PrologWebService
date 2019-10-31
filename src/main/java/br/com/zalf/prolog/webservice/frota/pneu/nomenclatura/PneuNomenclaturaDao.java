package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaCadastro;
import org.jetbrains.annotations.NotNull;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemVisualizacao;

import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuNomenclaturaDao {

    /**
     * Insere ou atualiza as informações de nomenclatura.
     *
     * @param pneuNomenclaturaCadastro Objeto contendo as informações de nomenclatura da empresa.
     * @param userToken                o token do usuário que fez a requisição.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void insertOrUpdateNomenclatura(@NotNull final PneuNomenclaturaCadastro pneuNomenclaturaCadastro,
                                    @NotNull final String userToken) throws Throwable;

    /**
     * Retorna as nomenclaturas de um diagrama - {@link PneuNomenclaturaItemVisualizacao pneuNomenclaturaItemVisualizacao}.
     *
     * @param codEmpresa  codigo da empresa.
     * @param codDiagrama codigo do diagrama.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(@NotNull final Long codEmpresa,
                                                                               @NotNull final Long codDiagrama)
            throws Throwable;

}