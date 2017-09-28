/**
 * Web Service para integracao do Avacorp com o ProLog
 * 
 */
@javax.xml.bind.annotation.XmlSchema(
        namespace = AvaCorpAvilanConstants.NAMESPACE,
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix="ns2", namespaceURI = AvaCorpAvilanConstants.NAMESPACE)
        })
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConstants;

import javax.xml.bind.annotation.XmlNs;