package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.webservice.commons.network.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

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

    ContrachequeDaoImpl dao = new ContrachequeDaoImpl();

    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes){
        try{
            return dao.getPreContracheque(cpf, codUnidade, ano, mes);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Response insertOrUpdateContracheque(String path, int ano, int mes, Long codUnidade){
        List<ItemImportContracheque> itens = new ArrayList<>();
        try{
            Reader in = new FileReader(path);
            List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            for (int i = 1; i < tabela.size(); i++) {
                itens.add(createItemImportContracheque(tabela.get(i)));
            }
            if(dao.insertOrUpdateItemImportContracheque(itens, ano, mes, codUnidade)){
                return Response.Ok("Dados inseridos com sucesso");
            }
        }catch (SQLException e){
            e.printStackTrace();
            return Response.Error("Erro relacionado ao banco de dados");
        }catch (IOException e){
            e.printStackTrace();
            return Response.Error("Erro no processamento do arquivo");
        }
        return Response.Error("Erro ao inserir os dados");
    }

    private ItemImportContracheque createItemImportContracheque(CSVRecord linha){
        ItemImportContracheque item = new ItemImportContracheque();
        if(!linha.get(0).trim().replaceAll( "[^\\d]", "").isEmpty()) {
            item.setCpf(Long.parseLong(linha.get(0)));
        }
        if(!linha.get(1).trim().isEmpty()) {
            item.setCodigo(linha.get(1));
        }
        if(!linha.get(2).trim().isEmpty()){
            item.setDescricao(linha.get(2).trim());
        }
        if(!linha.get(3).trim().isEmpty()){
            item.setSubDescricao(linha.get(3).trim());
        }
        if(!linha.get(4).trim().isEmpty()){
            item.setValor(Double.parseDouble(linha.get(4).replace(",", ".")));
        }
        return item;
    }

    public List<ItemImportContracheque> getItemImportContracheque (Long codUnidade, int ano, int mes, String cpf){
        try{
            return dao.getItemImportContracheque(codUnidade, ano, mes, cpf);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade){
        try{
            return dao.updateItemImportContracheque(item, ano, mes, codUnidade);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade){
        try{
            return dao.deleteItemImportContracheque(item, ano, mes, codUnidade);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
