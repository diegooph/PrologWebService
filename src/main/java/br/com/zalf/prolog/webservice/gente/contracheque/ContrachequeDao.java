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

    void deleteItensImportPreContracheque(@NotNull final List<Long> codItensDelecao) throws Throwable;
}
