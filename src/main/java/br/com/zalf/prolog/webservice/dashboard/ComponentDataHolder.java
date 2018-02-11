package br.com.zalf.prolog.webservice.dashboard;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ComponentDataHolder {
    // Informações do tipo do componente:
    public Integer codigoTipoComponente;
    public String nomeTipoComponente;
    public String descricaoTipoComponente;
    public int maximoBlocosHorizontais;
    public int maximoBlocosVerticais;
    public int minimoBlocosHorizontais;
    public int minimoBlocosVerticais;

    // Informações do componente:
    public Integer codigoComponente;
    public Integer codigoPilarProLogComponente;
    public String tituloComponente;
    public String subtituloComponente;
    public String descricaoComponente;
    public int qtdBlocosHorizontais;
    public int qtdBlocosVerticais;
    public int ordemExibicao;
    public String urlEndpointDados;
    public String corBackgroundHex;
    public String urlIcone;
    public String labelEixoX;
    public String labelEixoY;

    public ComponentDataHolder() {

    }
}