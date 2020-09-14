package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu;

import br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 31/08/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VinculoVeiculoPneuDao {
    /**
     * Método para retornar a conferência da planilha de import de veículos.
     *
     * @param codEmpresa        Código da empresa para qual as informações serão conferidas.
     * @param codUnidade        Código da unidade para a qual as informações serão conferidas.
     * @param usuario           Usuário que está realizando o vínculo.
     * @param jsonPlanilha      Informações da planilha de vínculo entre veículos e pneus em formato Json.
     * @param tipoImportVinculo Informação do tipo de import.
     * @throws Throwable Se algum erro ocorrer.
     */
    void importPlanilhaVinculoVeiculoPneu(@NotNull final Long codEmpresa,
                                          @NotNull final Long codUnidade,
                                          @NotNull final String usuario,
                                          @NotNull final String jsonPlanilha,
                                          @NotNull final TipoImport tipoImportVinculo) throws Throwable;
}
