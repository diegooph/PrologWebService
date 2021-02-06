package br.com.zalf.prolog.webservice.commons.imagens;

import java.time.LocalDateTime;

/**
 * Created on 21/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ImagemProlog {

    private Long codImagem;
    private String urlImagem;
    private LocalDateTime dataHoraCadastro;
    private Boolean statusImagem;

    public ImagemProlog() {

    }

    @Override
    public String toString() {
        return "ImagemProLog{" +
                "codImagem=" + codImagem +
                ", urlImagem='" + urlImagem + '\'' +
                ", dataHoraCadastro=" + dataHoraCadastro +
                ", statusImagem=" + statusImagem +
                '}';
    }

    public Long getCodImagem() {
        return codImagem;
    }

    public void setCodImagem(final Long codImagem) {
        this.codImagem = codImagem;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(final String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public LocalDateTime getDataHoraCadastro() {
        return dataHoraCadastro;
    }

    public void setDataHoraCadastro(final LocalDateTime dataHoraCadastro) {
        this.dataHoraCadastro = dataHoraCadastro;
    }

    public Boolean getStatusImagem() {
        return statusImagem;
    }

    public void setStatusImagem(final Boolean statusImagem) {
        this.statusImagem = statusImagem;
    }
}