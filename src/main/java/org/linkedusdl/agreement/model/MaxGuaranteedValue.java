package org.linkedusdl.agreement.model;

import com.viceversatech.rdfbeans.annotations.RDFBean;
import com.viceversatech.rdfbeans.annotations.RDFNamespaces;

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

@RDFBean("ag:MaxGuaranteedValue")
public interface MaxGuaranteedValue extends AgreementCondition {

}
