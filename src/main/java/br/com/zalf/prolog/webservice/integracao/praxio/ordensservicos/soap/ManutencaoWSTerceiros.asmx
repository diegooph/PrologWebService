<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" targetNamespace="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros">
      <s:element name="GerarOrdemDeServicoCorretiva">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ordemDeServico" type="tns:OrdemDeServicoCorretivaVO" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="OrdemDeServicoCorretivaVO">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CodigoEmpresa" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoFilial" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoGaragem" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="PrefixoVeiculo" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="Usuario" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoOrigemOS" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="ListaGrupoDefeitos" type="tns:ArrayOfGrupoDefeitoVO" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfGrupoDefeitoVO">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="GrupoDefeitoVO" nillable="true" type="tns:GrupoDefeitoVO" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="GrupoDefeitoVO">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CodigoGrupo" nillable="true" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoDefeito" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="Observacao" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="GerarOrdemDeServicoCorretivaResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GerarOrdemDeServicoCorretivaResult" type="tns:RetornoOsCorretivaVO" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="RetornoOsCorretivaVO">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="Sucesso" type="s:boolean" />
          <s:element minOccurs="0" maxOccurs="1" name="MensagemDeRetorno" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="CodigoOS" type="s:int" />
        </s:sequence>
      </s:complexType>
      <s:element name="AutenticacaoWebService" type="tns:AutenticacaoWebService" />
      <s:complexType name="AutenticacaoWebService">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="Token" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="ShortCode" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="NomeMetodo" type="s:string" />
        </s:sequence>
        <s:anyAttribute />
      </s:complexType>
      <s:element name="GerarOrdemDeServicoCorretivaProlog">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="ordemDeServico" type="tns:OrdemDeServicoCorretivaPrologVO" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="OrdemDeServicoCorretivaPrologVO">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CodUnidadeChecklist" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="CodChecklistRealizado" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="CpfColaboradorRealizacao" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PlacaVeiculoChecklist" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="KmColetadoChecklist" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="TipoChecklist" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="dataHoraRealizacaoUtc" type="s:dateTime" />
          <s:element minOccurs="0" maxOccurs="1" name="Usuario" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ListaPerguntasNokVO" type="tns:ArrayOfPerguntasNokVO" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfPerguntasNokVO">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="PerguntasNokVO" nillable="true" type="tns:PerguntasNokVO" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="PerguntasNokVO">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CodPerguntaNok" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="DescricaoPerguntaNok" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="ListaAlternativasNok" type="tns:ArrayOfAlternativasNokVO" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="ArrayOfAlternativasNokVO">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="unbounded" name="AlternativasNokVO" nillable="true" type="tns:AlternativasNokVO" />
        </s:sequence>
      </s:complexType>
      <s:complexType name="AlternativasNokVO">
        <s:sequence>
          <s:element minOccurs="1" maxOccurs="1" name="CodAlternativaNok" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="DescricaoAlternativaNok" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="PrioridadeAlternativaNok" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="GerarOrdemDeServicoCorretivaPrologResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GerarOrdemDeServicoCorretivaPrologResult" type="tns:RetornoOsCorretivaVO" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="EnviaOsAbertaProlog">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="1" maxOccurs="1" name="codigoOS" type="s:int" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="EnviaOsAbertaPrologResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="EnviaOsAbertaPrologResult" type="tns:RetornoEnvioPrologVO" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:complexType name="RetornoEnvioPrologVO">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" name="msgErro" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" name="codigoErroProlog" nillable="true" type="s:int" />
          <s:element minOccurs="1" maxOccurs="1" name="httpCode" nillable="true" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" name="msgDev" type="s:string" />
          <s:element minOccurs="0" maxOccurs="1" name="msgRetornoOk" type="s:string" />
        </s:sequence>
      </s:complexType>
      <s:element name="EnviaServicosExecutadosProlog">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="1" maxOccurs="1" name="codigoOS" type="s:int" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="EnviaServicosExecutadosPrologResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="EnviaServicosExecutadosPrologResult" type="tns:RetornoEnvioPrologVO" />
          </s:sequence>
        </s:complexType>
      </s:element>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="GerarOrdemDeServicoCorretivaSoapIn">
    <wsdl:part name="parameters" element="tns:GerarOrdemDeServicoCorretiva" />
  </wsdl:message>
  <wsdl:message name="GerarOrdemDeServicoCorretivaSoapOut">
    <wsdl:part name="parameters" element="tns:GerarOrdemDeServicoCorretivaResponse" />
  </wsdl:message>
  <wsdl:message name="GerarOrdemDeServicoCorretivaAutenticacaoWebService">
    <wsdl:part name="AutenticacaoWebService" element="tns:AutenticacaoWebService" />
  </wsdl:message>
  <wsdl:message name="GerarOrdemDeServicoCorretivaPrologSoapIn">
    <wsdl:part name="parameters" element="tns:GerarOrdemDeServicoCorretivaProlog" />
  </wsdl:message>
  <wsdl:message name="GerarOrdemDeServicoCorretivaPrologSoapOut">
    <wsdl:part name="parameters" element="tns:GerarOrdemDeServicoCorretivaPrologResponse" />
  </wsdl:message>
  <wsdl:message name="GerarOrdemDeServicoCorretivaPrologAutenticacaoWebService">
    <wsdl:part name="AutenticacaoWebService" element="tns:AutenticacaoWebService" />
  </wsdl:message>
  <wsdl:message name="EnviaOsAbertaPrologSoapIn">
    <wsdl:part name="parameters" element="tns:EnviaOsAbertaProlog" />
  </wsdl:message>
  <wsdl:message name="EnviaOsAbertaPrologSoapOut">
    <wsdl:part name="parameters" element="tns:EnviaOsAbertaPrologResponse" />
  </wsdl:message>
  <wsdl:message name="EnviaOsAbertaPrologAutenticacaoWebService">
    <wsdl:part name="AutenticacaoWebService" element="tns:AutenticacaoWebService" />
  </wsdl:message>
  <wsdl:message name="EnviaServicosExecutadosPrologSoapIn">
    <wsdl:part name="parameters" element="tns:EnviaServicosExecutadosProlog" />
  </wsdl:message>
  <wsdl:message name="EnviaServicosExecutadosPrologSoapOut">
    <wsdl:part name="parameters" element="tns:EnviaServicosExecutadosPrologResponse" />
  </wsdl:message>
  <wsdl:message name="EnviaServicosExecutadosPrologAutenticacaoWebService">
    <wsdl:part name="AutenticacaoWebService" element="tns:AutenticacaoWebService" />
  </wsdl:message>
  <wsdl:portType name="ManutencaoWSTerceirosSoap">
    <wsdl:operation name="GerarOrdemDeServicoCorretiva">
      <wsdl:documentation xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Gerar ordem de serviço corretiva.</wsdl:documentation>
      <wsdl:input message="tns:GerarOrdemDeServicoCorretivaSoapIn" />
      <wsdl:output message="tns:GerarOrdemDeServicoCorretivaSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="GerarOrdemDeServicoCorretivaProlog">
      <wsdl:documentation xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Gerar ordem de serviço corretiva por CheckList Prolog.</wsdl:documentation>
      <wsdl:input message="tns:GerarOrdemDeServicoCorretivaPrologSoapIn" />
      <wsdl:output message="tns:GerarOrdemDeServicoCorretivaPrologSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="EnviaOsAbertaProlog">
      <wsdl:documentation xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Envia O.S. aberta por CheckList Prolog para APi Prolog.</wsdl:documentation>
      <wsdl:input message="tns:EnviaOsAbertaPrologSoapIn" />
      <wsdl:output message="tns:EnviaOsAbertaPrologSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="EnviaServicosExecutadosProlog">
      <wsdl:documentation xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">Envia serviços executados da O.S. aberta por CheckList Prolog para APi Prolog.</wsdl:documentation>
      <wsdl:input message="tns:EnviaServicosExecutadosPrologSoapIn" />
      <wsdl:output message="tns:EnviaServicosExecutadosPrologSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="ManutencaoWSTerceirosSoap" type="tns:ManutencaoWSTerceirosSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="GerarOrdemDeServicoCorretiva">
      <soap:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/GerarOrdemDeServicoCorretiva" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
        <soap:header message="tns:GerarOrdemDeServicoCorretivaAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GerarOrdemDeServicoCorretivaProlog">
      <soap:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/GerarOrdemDeServicoCorretivaProlog" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
        <soap:header message="tns:GerarOrdemDeServicoCorretivaPrologAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="EnviaOsAbertaProlog">
      <soap:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/EnviaOsAbertaProlog" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
        <soap:header message="tns:EnviaOsAbertaPrologAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="EnviaServicosExecutadosProlog">
      <soap:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/EnviaServicosExecutadosProlog" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
        <soap:header message="tns:EnviaServicosExecutadosPrologAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="ManutencaoWSTerceirosSoap12" type="tns:ManutencaoWSTerceirosSoap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="GerarOrdemDeServicoCorretiva">
      <soap12:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/GerarOrdemDeServicoCorretiva" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
        <soap12:header message="tns:GerarOrdemDeServicoCorretivaAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GerarOrdemDeServicoCorretivaProlog">
      <soap12:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/GerarOrdemDeServicoCorretivaProlog" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
        <soap12:header message="tns:GerarOrdemDeServicoCorretivaPrologAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="EnviaOsAbertaProlog">
      <soap12:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/EnviaOsAbertaProlog" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
        <soap12:header message="tns:EnviaOsAbertaPrologAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="EnviaServicosExecutadosProlog">
      <soap12:operation soapAction="http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/EnviaServicosExecutadosProlog" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
        <soap12:header message="tns:EnviaServicosExecutadosPrologAutenticacaoWebService" part="AutenticacaoWebService" use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="ManutencaoWSTerceiros">
    <wsdl:port name="ManutencaoWSTerceirosSoap" binding="tns:ManutencaoWSTerceirosSoap">
      <soap:address location="http://sp.bgmrodotec.com.br:8184/vanderlei.junior/ManutencaoWSTerceiros.asmx" />
    </wsdl:port>
    <wsdl:port name="ManutencaoWSTerceirosSoap12" binding="tns:ManutencaoWSTerceirosSoap12">
      <soap12:address location="http://sp.bgmrodotec.com.br:8184/vanderlei.junior/ManutencaoWSTerceiros.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>