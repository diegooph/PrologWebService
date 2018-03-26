package br.com.zalf.prolog.webservice.commons.imagens;

import java.util.List;

/**
 * Created on 21/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class Galeria {
    private List<ImagemProLog> galeria;

    public Galeria() {

    }

    public Galeria(final List<ImagemProLog> galeria) {
        this.galeria = galeria;
    }

    public List<ImagemProLog> getGaleria() {
        return galeria;
    }

    public void setGaleria(final List<ImagemProLog> galeria) {
        this.galeria = galeria;
    }

    @Override
    public String toString() {
        return "Galeria{" +
                "galeria=" + galeria +
                '}';
    }
}