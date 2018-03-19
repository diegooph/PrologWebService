package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

/**
 * Created on 04/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class Localizacao {
    private String latitude;
    private String longitude;

    public Localizacao() {
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof Localizacao))
            return false;

        if (obj == this)
            return true;

        final Localizacao localizacao = (Localizacao) obj;
        return localizacao.latitude.equals(latitude) && localizacao.longitude.equals(longitude);
    }
}
