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

@RDFBean("ag:AgreementCondition")
public interface AgreementCondition {
	
	@RDFSubject
	public String getId();
	public void setId(String id);
	
	@RDF("rdf:type")
	public URI getType();
	public void setType( URI uri);
	
	@RDF("ag:hasValue")
	public Value getHasValue();
	public void setHasValue(Value uri);
	
	@RDF("ag:refersTo")
	public ServiceProperty getRefersTo();
	public void setRefersTo(ServiceProperty uri);
	
	@RDF("ag:hasConditionEvaluationEntityInvolvement")
	public EntityInvolvement getHasConditionEvaluationEntityInvolvement();
	public void setHasConditionEvaluationEntityInvolvement(EntityInvolvement uri);
	
	@RDF("ag:hasEvaluationInterval")
	public URI getHasEvaluationInterval();
	public void setHasEvaluationInterval(URI uri);
}
