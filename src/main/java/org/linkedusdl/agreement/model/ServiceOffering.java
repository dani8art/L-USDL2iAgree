package org.linkedusdl.agreement.model;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

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
@RDFBean("usdl:ServiceOffering")
public interface ServiceOffering {
	
	@RDFSubject
	public String getId();
	public void setId(String id);

	@RDF("ag:complyWith")
	public Collection<GuaranteeTerm> getComplyWith();
	public void setComplyWith(Collection<GuaranteeTerm> complyWith);
	
	@RDF("usdl:includes")
	public Set<URI> getIncludes();
	public void setIncludes(Set<URI> includes);	
	
}
