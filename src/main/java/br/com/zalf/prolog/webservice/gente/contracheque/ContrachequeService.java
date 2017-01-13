package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.gente.contracheque.Contracheque;
import br.com.zalf.prolog.gente.contracheque.ItemImportContracheque;
import br.com.zalf.prolog.webservice.util.L;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.gente.contracheque.ContrachequeResource.TAG;

/**
 * Created by Zalf on 23/11/16.
 */
public class ContrachequeService {

    ContrachequeDaoImpl dao = new ContrachequeDaoImpl();

    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes) {
        try {
            return dao.getPreContracheque(cpf, codUnidade, ano, mes);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response insertOrUpdateContracheque(String path, int ano, int mes, Long codUnidade) {
        List<ItemImportContracheque> itens = new ArrayList<>();
        try {
            Reader in = new FileReader(path);
            List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            CSVRecord linha = tabela.get(0);
            L.d(TAG, linha.toString());
            // SE FOR A PRIMEIRA LINHA, CRIAR O ARRAY COM OS CÓDIGOS
            List<Long> codigos = new ArrayList<>();
            if(tabela.size() == 4 || linha.size() == 1){
                return Response.Error("Planilha incorreta, verifique o formato");
            }
            for(int i = 1; i < linha.size(); i++){
                if(linha.get(i).trim().isEmpty()){
                    return Response.Error("Campo código não pode estar em branco, linha: 1, coluna: " + (i+1));
                }
                codigos.add(Long.parseLong(linha.get(i)));
            }
            // SE FOR A SEGUNDA LINHA, CRIAR O ARRAY COM AS DESCRIÇÕES
            linha = tabela.get(1);
            List<String> descricoes  = new ArrayList<>();
            for(int i = 1; i < linha.size(); i++){
                if (linha.get(1).trim().isEmpty()){
                    return Response.Error("Campo descrição não pode estar em branco, linha: 2, coluna: " + (i+1) );
                }
                descricoes.add(linha.get(i));
            }
            // SE FOR A TERCEIRA LINHA, CRIAR O ARRAY COM AS SUB DESCRICOES
            linha = tabela.get(2);
            List<String> subDescricoes  = new ArrayList<>();
            for(int i = 1; i < linha.size(); i++){
                subDescricoes.add(linha.get(i));
            }

            for (int i = 4; i < tabela.size(); i++){
                for(int j = 0; j < codigos.size(); j++){
                    if(tabela.get(i).get(0).trim().isEmpty()){
                        return Response.Error("Campo CPF não pode estar em branco, linha: " + (i+1));
                    }else if(tabela.get(i).get(j+1).trim().isEmpty()){
                        return Response.Error("Campo valor não pode estar em branco, linha: " + (i+1) + ", coluna: " + (j+2));
                    }
                    itens.add(createItemImportContracheque(Long.parseLong(tabela.get(i).get(0).replaceAll( "[^\\d]", "")), codigos.get(j), descricoes.get(j), subDescricoes.get(j),
                            Double.parseDouble(tabela.get(i).get(j+1))));
                }
            }
            if (dao.insertOrUpdateItemImportContracheque(itens, ano, mes, codUnidade)) {
                return Response.Ok("Dados inseridos com sucesso");
            }
        }catch(SQLException e){
            e.printStackTrace();
            return Response.Error("Erro relacionado ao banco de dados");
        }catch(IOException e){
            e.printStackTrace();
            return Response.Error("Erro no processamento do arquivo");
        }catch (NumberFormatException e){
            e.printStackTrace();
            return Response.Error("Número mal formatado encontrado no arquivo");
        }
        return Response.Error("Erro ao inserir os dados");
    }

    private ItemImportContracheque createItemImportContracheque(Long cpf , long codigo,
                                                                String descricao, String subDescricao, double valor){
        ItemImportContracheque item = new ItemImportContracheque();
        item.setCpf(cpf);
        item.setCodigo(codigo);
        item.setDescricao(descricao);
        item.setSubDescricao(subDescricao);
        item.setValor(valor);
        return item;
    }

    public List<ItemImportContracheque> getItemImportContracheque(Long codUnidade, int ano, int mes, String cpf) {
        try {
            return dao.getItemImportContracheque(codUnidade, ano, mes, cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) {
        try {
            return dao.updateItemImportContracheque(item, ano, mes, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItemImportContracheque(ItemImportContracheque item, int ano, int mes, Long codUnidade) {
        try {
            return dao.deleteItemImportContracheque(item, ano, mes, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
