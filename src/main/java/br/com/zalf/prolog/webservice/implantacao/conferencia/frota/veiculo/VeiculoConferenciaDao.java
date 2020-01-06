package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.implantacao.conferencia._model.TipoImport;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    /**
     * Método para retornar a conferência da planilha de import de veículos.
     *
     * @param codUnidade   Código da unidade para a qual as informações serão conferidas.
     * @param jsonPlanilha Informações da planilha de import de veículos em formato Json.
     * @param tipoImportVeiculo Informação do tipo de import.
     * @throws Throwable Se algum erro ocorrer.
     */
    void importPlanilhaVeiculos(@NotNull final Long codEmpresa,
                                @NotNull final Long codUnidade,
                                @NotNull final String usuario,
                                @NotNull final String jsonPlanilha,
                                @NotNull final TipoImport tipoImportVeiculo) throws Throwable;
}