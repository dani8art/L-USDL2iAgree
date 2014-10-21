package org.linkedusdl.agreement.model;


import java.net.URI;
import java.util.Collection;

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

@RDFBean("ag:AgreementTerm")
public interface AgreementTerm {
	
	@RDFSubject
	public String getId();
	public void setId(String id);
	
	@RDF("ag:guaranteedOver")
	public URI getGuaranteedOver();
	public void setGuaranteedOver(URI s);
	
	@RDF("ag:guarantees")
	public AgreementCondition getGuarantees();
	public void setGuarantees(AgreementCondition agc);
	
	@RDF("ag:hasPrecondition")
	public AgreementCondition getHasPrecondition();
	public void setHasPrecondition(AgreementCondition agc);
	
	@RDF("ag:hasObligedEntityInvolvement")
	public EntityInvolvement getHasObligedEntityInvolvement();
	public void setHasObligedEntityInvolvement(EntityInvolvement uri);
	
	@RDF("ag:hasValidityInterval")
	public URI getHasValidityInterval();
	public void setHasValidityInterval(URI uri);
	
	
}
