package br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama;

/**
 * Created by luiz on 22/05/17.
 */
public enum TipoEixoVeiculo {
    DIANTEIRO("D"),
    TRASEIRO("T");

    private final String tipoEixo;

    TipoEixoVeiculo(final String tipoEixo) {
        this.tipoEixo = tipoEixo;
    }

    public static TipoEixoVeiculo fromString(final String s) throws IllegalArgumentException{
        if (s != null) {
            for (TipoEixoVeiculo tipoEixo : TipoEixoVeiculo.values()) {
                if (s.equalsIgnoreCase(tipoEixo.tipoEixo)) {
                    return tipoEixo;
                }
            }
        }

        throw new IllegalArgumentException(String.format("Nenhum enum com valor %s encontrado", s));
    }
}