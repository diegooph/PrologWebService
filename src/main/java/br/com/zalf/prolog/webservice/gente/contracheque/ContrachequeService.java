package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.contracheque.model.Contracheque;
import br.com.zalf.prolog.webservice.gente.contracheque.model.ItemImportContracheque;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalf on 23/11/16.
 */
public class ContrachequeService {
    private static final String TAG = ContrachequeService.class.getSimpleName();
    private final ContrachequeDao dao = Injection.provideContrachequeDao();

    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) {
        try {
            return dao.getPreContracheque(cpf, codUnidade, ano, mes);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o contracheque de um colaborador.\n" +
                    "codUnidade: %d\n" +
                    "cpf: %d\n" +
                    "ano: %d\n" +
                    "mes: %d", codUnidade, cpf, ano, mes), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar pré contracheque, tente novamente");
        }
    }

    public Response insertOrUpdateContracheque(String path, int ano, int mes, Long codUnidade) {
        List<ItemImportContracheque> itens = new ArrayList<>();
        try {
            Reader in = new FileReader(path);
            List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            for (int i = 1; i < tabela.size(); i++) {
                ItemImportContracheque item = createItemImportContracheque(tabela.get(i));
                if (item != null) {
                    itens.add(item);
                }
            }
            if (dao.insertOrUpdateItemImportContracheque(itens, ano, mes, codUnidade)) {
                return Response.ok("Dados inseridos com sucesso");
            }
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao relacionado ao banco de dados ao inserir ou atualizar os dados " +
                    "do contracheque. \n" +
                    "codUnidade: %d \n" +
                    "ano: %d \n" +
                    "mes: %d", codUnidade, ano, mes), e);
            return Response.error("Erro relacionado ao banco de dados");
        } catch (IOException e) {
            Log.e(TAG, String.format("Erro ao relacionado ao processamento do arquivo ao inserir ou atualizar os dados " +
                    "do contracheque. \n" +
                    "codUnidade: %d \n" +
                    "ano: %d \n" +
                    "mes: %d", codUnidade, ano, mes), e);
            return Response.error("Erro no processamento do arquivo");
        }
        return Response.error("Erro ao inserir os dados");
    }

    private ItemImportContracheque createItemImportContracheque(CSVRecord linha) {
        if (linha.get(0).isEmpty()) {
            return null;
        }
        linha.get(1);

        ItemImportContracheque item = new ItemImportContracheque();
        if (!linha.get(0).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpf(Long.parseLong(linha.get(0).trim().replaceAll("[^\\d]", "")));
        }
        if (!linha.get(1).trim().isEmpty()) {
            item.setCodigoItem(linha.get(1));
        }
        if (!linha.get(2).trim().isEmpty()) {
            item.setDescricao(linha.get(2).trim());
        }
        if (!linha.get(3).trim().isEmpty()) {
            item.setSubDescricao(linha.get(3).trim());
        }
        if (!linha.get(4).trim().isEmpty()) {
            item.setValor(Double.parseDouble(linha.get(4).replace(",", ".")));
        }
        return item;
    }

    public List<ItemImportContracheque> getItemImportContracheque(Long codUnidade, int ano, int mes, String cpf) {
        try {
            return dao.getItemImportContracheque(codUnidade, ano, mes, cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os itens importados do contracheque. \n" +
                    "codUnidade: %d \n" +
                    "ano: %d \n" +
                    "mes: %d \n" +
                    "cpf: %s", codUnidade, ano, mes, cpf), e);
            return null;
        }
    }

    public boolean updateItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) {
        try {
            return dao.updateItemImportContracheque(item, ano, mes, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o item do contracheque. \n" +
                    "codUnidade: %d \n" +
                    "ano: %d \n" +
                    "mes: %d", codUnidade, ano, mes), e);
            return false;
        }
    }

    public boolean deleteItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade, Long cpf, String codItem) {
        try {
            return dao.deleteItemImportContracheque(item, ano, mes, codUnidade, cpf, codItem);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o item do contracheque. \n" +
                    "codUnidade: %d \n" +
                    "codItem: %s \n" +
                    "cpf: %d \n" +
                    "ano: %d \n" +
                    "mes: %d", codUnidade, codItem, cpf, ano, mes), e);
            return false;
        }
    }


    @NotNull
    public void deleteItensImportPreContracheque(final List<Long> codItensDelecao) throws ProLogException {
        if (codItensDelecao.isEmpty()) {
            return;
        }

        try {
            dao.deleteItensImportPreContracheque(codItensDelecao);
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível deletar esses itens, tente novamente";
            Log.e(TAG, errorMessage, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, errorMessage);
        }
    }
}
