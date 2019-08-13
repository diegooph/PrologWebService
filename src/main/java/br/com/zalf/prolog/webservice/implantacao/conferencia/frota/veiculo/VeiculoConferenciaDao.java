package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoConferenciaDao {

    /**
     * Método para receber a planilha .csv do banco, com todas as validações.
     * <p>
     * @param out Stream onde os dados serão escritos para retorno.
     * @param codUnidade código da unidade referente ao import.
     * @param jsonPlanilha json com todos os atributos do objeto VeiculoPlanilha.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    void getVerificacaoPlanilhaCsv(@NotNull final OutputStream out,
                                   @NotNull final Long codUnidade,
                                   @NotNull final String jsonPlanilha) throws Throwable;
}


