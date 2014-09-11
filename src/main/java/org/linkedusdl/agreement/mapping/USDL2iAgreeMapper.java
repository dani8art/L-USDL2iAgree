package org.linkedusdl.agreement.mapping;

import java.util.Collection;

import es.us.isa.ada.wsag10.Agreement;
import es.us.isa.ada.wsag10.Context;
import es.us.isa.ada.wsag10.CreationConstraints;
import es.us.isa.ada.wsag10.GuaranteeTerm;
import es.us.isa.ada.wsag10.ServiceDescriptionTerm;
import es.us.isa.ada.wsag10.ServiceProperties;
import es.us.isa.ada.wsag10.TermCompositor;

import com.viceversatech.rdfbeans.exceptions.RDFBeanException;


public abstract class USDL2iAgreeMapper {
	
	private Agreement document;
	private CreationConstraints cc;
	private USDLModel model;
	
	public USDL2iAgreeMapper (USDLModel model){
		this.model = model;
		this.document = new Agreement();
		this.cc = new CreationConstraints();
	}
	
	public abstract Collection<GuaranteeTerm> getGuaranteeTerms();
	public abstract ServiceProperties getServiceProperties();
	public abstract CreationConstraints getCreationConstraints();
	public abstract ServiceDescriptionTerm getServiceDescriptionTerm();
	public abstract Context getContext();
	public Agreement transform () throws RDFBeanException{
		
		//context
		getDocument().setContext(getContext());
		
		TermCompositor tcmp = new TermCompositor();
		
		//add guaranteeterm
		for(GuaranteeTerm gt : getGuaranteeTerms()){
			tcmp.addComprisedTerm(gt);
		}
		//add SP
		tcmp.addComprisedTerm(getServiceProperties());
		//add SDT
		tcmp.addComprisedTerm(getServiceDescriptionTerm());
		//add Term
		getDocument().setTerms(tcmp);
		//set CC
		setCC(getCreationConstraints());
		
		return getDocument();		
	}

	public Agreement getDocument() {
		return this.document;
	}

	public void setDocument(Agreement document) {
		this.document = document;
	}
	
	public USDLModel getModel(){
		return this.model;
	}
	
	public void setModel(USDLModel model){
		this.model = model;
	}
	
	public CreationConstraints getCC(){
		return this.cc;
	}
	
	public void setCC (CreationConstraints cc){
		this.cc = cc;
	}
}
