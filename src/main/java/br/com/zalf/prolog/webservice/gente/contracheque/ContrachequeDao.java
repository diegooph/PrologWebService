package br.com.zalf.prolog.webservice.gente.contracheque;

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

    List<ItemImportContracheque> getItemImportContracheque (Long codUnidade,
                                                            int ano,
                                                            int mes,
                                                            String cpf) throws SQLException;

    boolean updateItemImportContracheque(ItemImportContracheque item,
                                         int ano,
                                         int mes,
                                         Long codUnidade) throws SQLException;

    Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) throws SQLException;

    boolean deleteItemImportContracheque(ItemImportContracheque item,
                                         int ano,
                                         int mes,
                                         Long codUnidade) throws SQLException;
}
