package org.linkedusdl.agreement.model;

import java.net.URI;

import com.viceversatech.rdfbeans.annotations.RDF;
import com.viceversatech.rdfbeans.annotations.RDFBean;
import com.viceversatech.rdfbeans.annotations.RDFNamespaces;
import com.viceversatech.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
	"ag = http://purl.org/agreements#",
	"cloud = http://purl.org/cloudServices#",
	"gr = http://purl.org/goodrelations/v1#",
	"metrics = http://purl.org/metrics#",
	"owl = http://www.w3.org/2002/07/owl#",
	"qudt = http://qudt.org/schema/qudt#",
	"skos = http://www.w3.org/2004/02/skos/core#",
	"usdl = http://www.linked-usdl.org/ns/usdl-core#",
	"rdf = http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	"rdfs =  http://www.w3.org/2000/01/rdf-schema#"
})

@RDFBean("metrics:Metric")
public interface Metric {

	@RDFSubject
	public String getId();
	public void setId(String id);
	
	@RDF("rdf:type")
	public URI getType();
	public void setType(URI type);	
	
	@RDF("metrics:hasExpression")
	public String getHasExpression();
	public void setHasExpression(String val);
	
	@RDF("qudt:unit")
	public URI getUnit();
	public void setUnit(URI unit);
}
