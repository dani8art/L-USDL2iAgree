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
	"usdl = http://www.linked-usdl.org/ns/usdl-core#"
})

@RDFBean("ag:GuaranteeTerm")
public interface GuaranteeTerm {
	
	@RDFSubject
	public String getId();
	public void setId(String id);

	@RDF("ag:hasCompensation")
	public AgreementCondition getHasCompensation();
	public void setHasCompensation(AgreementCondition hasCompensation);
	
	@RDF("ag:guarantees")
	public AgreementCondition getGuarantees();
	public void setGuarantees(AgreementCondition agc);
	
	@RDF("ag:guaranteedOver") // no tendria por que modelar podria devolver URI no?
	public URI getGuaranteedOver();
	public void setGuaranteedOver(URI s);
	
	@RDF("ag:hasObligedEntityInvolvement")
	public URI getHasObligedEntityInvolvement();
	public void setHasObligedEntityInvolvement(URI uri);
	
	@RDF("ag:hasPrecondition")
	public AgreementCondition getHasPrecondition();
	public void setHasPrecondition(AgreementCondition agc);
	
	
	
}
