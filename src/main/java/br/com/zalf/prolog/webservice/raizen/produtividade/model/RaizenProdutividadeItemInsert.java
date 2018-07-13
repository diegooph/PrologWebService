package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeItem;

import java.time.LocalDate;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItemInsert extends RaizenProdutividadeItem {

    private Long cpfMotorista;
    private LocalDate dataViagem;

    public Long getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(Long cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    @Override
    public LocalDate getDataViagem() {
        return dataViagem;
    }

    @Override
    public void setDataViagem(LocalDate dataViagem) {
        this.dataViagem = dataViagem;
    }
}
