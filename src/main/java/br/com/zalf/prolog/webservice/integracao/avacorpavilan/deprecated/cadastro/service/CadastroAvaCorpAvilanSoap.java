
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.service;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanConstants;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "CadastroSoap", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CadastroAvaCorpAvilanSoap {


    /**
     * Busca Veiculos ativos
     * 
     * @param cpf
     * @return
     *     returns br.com.avacorp.integracaoprolog.VeiculosAtivos
     */
    @WebMethod(action = AvaCorpAvilanConstants.NAMESPACE + "/buscarVeiculosAtivos")
    @WebResult(name = "buscarVeiculosAtivosResult", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
    @RequestWrapper(localName = "buscarVeiculosAtivos", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarVeiculosAtivos")
    @ResponseWrapper(localName = "buscarVeiculosAtivosResponse", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarVeiculosAtivosResponse")
    public VeiculosAtivos buscarVeiculosAtivos(
            @WebParam(name = "cpf", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String cpf);

    /**
     * Busca Veiculo ativo
     * 
     * @param cpf
     * @param placa
     * @return
     *     returns br.com.avacorp.integracaoprolog.VeiculosAtivos
     */
    @WebMethod(action = AvaCorpAvilanConstants.NAMESPACE + "/buscarVeiculoAtivo")
    @WebResult(name = "buscarVeiculoAtivoResult", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
    @RequestWrapper(localName = "buscarVeiculoAtivo", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarVeiculoAtivo")
    @ResponseWrapper(localName = "buscarVeiculoAtivoResponse", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarVeiculoAtivoResponse")
    public VeiculosAtivos buscarVeiculoAtivo(
            @WebParam(name = "cpf", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String cpf,
            @WebParam(name = "placa", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String placa);

    /**
     * Busca Pneus de um  veiculo
     * 
     * @param veiculo
     * @return
     *     returns br.com.avacorp.integracaoprolog.PneusVeiculo
     */
    @WebMethod(action = AvaCorpAvilanConstants.NAMESPACE + "/buscarPneusVeiculo")
    @WebResult(name = "buscarPneusVeiculoResult", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
    @RequestWrapper(localName = "buscarPneusVeiculo", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarPneusVeiculo")
    @ResponseWrapper(localName = "buscarPneusVeiculoResponse", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarPneusVeiculoResponse")
    public PneusVeiculo buscarPneusVeiculo(
            @WebParam(name = "veiculo", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String veiculo);

    /**
     * Busca usuário para integração
     * 
     * @param cpf
     * @param dataNascimento
     * @return
     *     returns br.com.avacorp.integracaoprolog.UsuarioIntegracao
     */
    @WebMethod(action = AvaCorpAvilanConstants.NAMESPACE + "/buscarUsuarioIntegracao")
    @WebResult(name = "buscarUsuarioIntegracaoResult", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
    @RequestWrapper(localName = "buscarUsuarioIntegracao", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarUsuarioIntegracao")
    @ResponseWrapper(localName = "buscarUsuarioIntegracaoResponse", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprolog.BuscarUsuarioIntegracaoResponse")
    public UsuarioIntegracao buscarUsuarioIntegracao(
            @WebParam(name = "cpf", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String cpf,
            @WebParam(name = "dataNascimento", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String dataNascimento);

    /**
     * Busca tipos de veículo
     *
     * @return
     *     returns br.com.avacorp.integracaoprologtestes.TiposVeiculo
     */
    @WebMethod(action = AvaCorpAvilanConstants.NAMESPACE + "/buscarTiposVeiculo")
    @WebResult(name = "buscarTiposVeiculoResult", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
    @RequestWrapper(localName = "buscarTiposVeiculo", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprologtestes.BuscarTiposVeiculo")
    @ResponseWrapper(localName = "buscarTiposVeiculoResponse", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprologtestes.BuscarTiposVeiculoResponse")
    public TiposVeiculo buscarTiposVeiculo();

    /**
     * Busca veiculos do mesmo tipo
     *
     * @param tipoVeiculo
     * @return
     *     returns br.com.avacorp.integracaoprologtestes.VeiculoTipo
     */
    @WebMethod(action = AvaCorpAvilanConstants.NAMESPACE + "/buscarVeiculosTipo")
    @WebResult(name = "buscarVeiculosTipoResult", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
    @RequestWrapper(localName = "buscarVeiculosTipo", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprologtestes.BuscarVeiculosTipo")
    @ResponseWrapper(localName = "buscarVeiculosTipoResponse", targetNamespace = AvaCorpAvilanConstants.NAMESPACE, className = "br.com.avacorp.integracaoprologtestes.BuscarVeiculosTipoResponse")
    public VeiculoTipo buscarVeiculosTipo(
            @WebParam(name = "tipoVeiculo", targetNamespace = AvaCorpAvilanConstants.NAMESPACE)
                    String tipoVeiculo);

}