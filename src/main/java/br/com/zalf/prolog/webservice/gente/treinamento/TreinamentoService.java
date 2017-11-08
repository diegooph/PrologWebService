package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.commons.util.S3FileSender;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Classe TreinamentoService responsavel por comunicar-se com a interface DAO
 */
public class TreinamentoService {

    private final TreinamentoDao dao = new TreinamentoDaoImpl();

    public List<Treinamento> getVistosByColaborador(Long cpf) {
        try {
            return dao.getVistosColaborador(cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar treinamentos vistos pelo colaborador " + cpf);
        }
    }

    public List<Treinamento> getAll(Long dataInicial, Long dataFinal, String codFuncao,
                                    Long codUnidade, Boolean comCargosLiberados,
                                    boolean apenasLiberados, long limit, long offset) {
        try {
            return dao.getAll(dataInicial, dataFinal, codFuncao, codUnidade, comCargosLiberados,
                    apenasLiberados, limit, offset);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar treinamentos");
        }
    }

    public Treinamento getByCod(Long codUnidade, Long codTreinamento) {
        try {
            return dao.getByCod(codUnidade, codTreinamento);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar treinamento com código: " + codTreinamento);
        }
    }

    public List<Treinamento> getNaoVistosByColaborador(Long cpf) {
        try {
            return dao.getNaoVistosColaborador(cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar treinamentos não vistos pelo colaborador " + cpf);
        }
    }

    public boolean marcarTreinamentoComoVisto(Long codTreinamento, Long cpf) {
        try {
            return dao.marcarTreinamentoComoVisto(codTreinamento, cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long insert(InputStream file, Treinamento treinamento) {
        try {
            PDFTransformer transformer = new PDFTransformer();
            UploadTreinamentoHelper helper = new UploadTreinamentoHelper(transformer);
            return dao.insert(helper.upload(treinamento, file));
        } catch (SQLException | IOException | S3FileSender.S3FileSenderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) {
        try {
            return dao.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateTreinamento(Treinamento treinamento) {
        try {
            return dao.updateTreinamento(treinamento);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) {
        try {
            return dao.updateUrlImagensTreinamento(urls, codTreinamento);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTreinamento(Long codTreinamento) {
        try {
            return dao.deleteTreinamento(codTreinamento);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
