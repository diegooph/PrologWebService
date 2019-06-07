package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by web on 03/03/17.
 */
public interface ContrachequeDao {

    boolean insertOrUpdateItemImportContracheque(List<ItemImportContracheque> itens,
                                                 int ano,
                                                 int mes,
                                                 Long codUnidade) throws SQLException;

    /**
     * Método que busca os itens importados previamente
     *
     * @param codUnidade código da unidade
     * @param ano        da competancia
     * @param mes        da competencia
     * @param cpf        especifico a ser buscado, parâmetro opcional
     * @return
     * @throws SQLException
     */
    @NotNull
    List<ItemImportContracheque> getItemImportContracheque(Long codUnidade,
                                                           int ano,
                                                           int mes,
                                                           String cpf) throws SQLException;

    boolean updateItemImportContracheque(ItemImportContracheque item,
                                         int ano,
                                         int mes,
                                         Long codUnidade) throws SQLException;

    @NotNull
    Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) throws SQLException;

    boolean deleteItemImportContracheque(ItemImportContracheque item,
                                         int ano,
                                         int mes,
                                         Long codUnidade, Long cpf, String codItem) throws SQLException;

    /**
     * Esse método recebe uma lista de códigos de itens de pré contracheque para deleção. Caso a execução da deleção
     * não afete nenhuma linha da tabela, um erro será retornado para o usuário. Se afetar pelo menos uma, não será
     * retornado erro. Mesmo que de 10 códigos passados apenas 1 tenha sido encontrado e deletado.
     *
     * @param codItensDelecao Código dos itens de pré contracheque que devem ser deletados.
     * @throws Throwable Caso ocorra algum erro.
     */
    void deleteItensImportPreContracheque(@NotNull final List<Long> codItensDelecao) throws Throwable;
}
