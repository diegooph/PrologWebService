package br.com.zalf.prolog.webservice.commons.imagens;

import java.util.List;

/**
 * Created on 21/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class Galeria {
    private List<ImagemProlog> imagens;

    public Galeria(final List<ImagemProlog> imagens) {
        this.imagens = imagens;
    }

    @Override
    public String toString() {
        return "Galeria{" +
                "imagens=" + imagens +
                '}';
    }

    public List<ImagemProlog> getImagens() {
        return imagens;
    }

    public void setImagens(final List<ImagemProlog> imagens) {
        this.imagens = imagens;
    }
}