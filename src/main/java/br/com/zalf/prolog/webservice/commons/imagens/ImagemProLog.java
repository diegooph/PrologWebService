package br.com.zalf.prolog.webservice.commons.imagens;

import java.time.LocalDateTime;

/**
 * Created on 21/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ImagemProLog {

    public Long codImagem;
    public String urlImagem;
    public LocalDateTime dataHoraCadastro;
    public Boolean statusImagem;

    public ImagemProLog() {
    }

    public ImagemProLog(final Long codImagem,
                        final String urlImagem,
                        final LocalDateTime dataHoraCadastro,
                        final Boolean statusImagem) {
        this.codImagem = codImagem;
        this.urlImagem = urlImagem;
        this.dataHoraCadastro = dataHoraCadastro;
        this.statusImagem = statusImagem;
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

    @Override
    public String toString() {
        return "ImagemProLog{" +
                "codImagem=" + codImagem +
                ", urlImagem='" + urlImagem + '\'' +
                ", dataHoraCadastro=" + dataHoraCadastro +
                ", statusImagem=" + statusImagem +
                '}';
    }
}
