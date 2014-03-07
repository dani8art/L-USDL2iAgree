package org.linkedusdl.agreement.model;

import java.net.URI;

import com.viceversatech.rdfbeans.annotations.RDF;
import com.viceversatech.rdfbeans.annotations.RDFBean;
import com.viceversatech.rdfbeans.annotations.RDFNamespaces;
import com.viceversatech.rdfbeans.annotations.RDFSubject;
import com.viceversatech.rdfbeans.reflect.RDFProperty;

@RDFNamespaces({
	"ag = http://purl.org/agreements#",
	"cloud = http://purl.org/cloudServices#",
	"gr = http://purl.org/goodrelations/v1#",
	"metrics = http://purl.org/metrics#",
	"owl = http://www.w3.org/2002/07/owl#",
	"qudt = http://qudt.org/schema/qudt#",
	"skos = http://www.w3.org/2004/02/skos/core#",
	"usdl = http://www.linked-usdl.org/ns/usdl-core#"
})

@RDFBean("ag:AgreementCondition")
public interface AgreementCondition {
	
	@RDFSubject
	public String getId();
	public void setId(String id);
	
	@RDF("ag:hasValue")
	public URI getHasValue();
	public void setHasValue(URI uri);
	
	@RDF("ag:refersTo")
	public URI getRefersTo();
	public void setRefersTo(RDFProperty p);
	
}
