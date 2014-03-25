package org.linkedusdl.agreement.mapping;

import java.net.URI;

import org.linkedusdl.agreement.model.AgreementCondition;
import org.linkedusdl.agreement.model.ServiceOffering;

import es.us.isa.ada.wsag10.Agreement;
import es.us.isa.ada.wsag10.StringSLO;

import org.linkedusdl.agreement.model.GuaranteeTerm;



public class USDL2iAgreeClassMapping {
	
	protected Agreement document;
	
	public USDL2iAgreeClassMapping (){
		this.document = new Agreement();				
	}
	
	public Agreement transform (ServiceOffering in){
		// Name = URI
		try{
			getDocument().setName(in.getId());		
			
			int n = 1; //var auxiliar para nombrar terms
			for( GuaranteeTerm gt: in.getComplyWith() ){
				
				es.us.isa.ada.wsag10.GuaranteeTerm gtwsag = new es.us.isa.ada.wsag10.GuaranteeTerm();
				gtwsag.setName("G"+n); // establece el nombre del GuaranteeTerm.
				
				// service level objective
				AgreementCondition agC = gt.getGuarantees();
				StringSLO slo = new StringSLO();
				slo.setSlo(getStringSLOFromAgC(agC));
				gtwsag.setSlo(slo);
				
				//add GuaranteeTerm to agreement
				getDocument().getAllTerms().add(gtwsag);
				
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return document;		
	}

	public Agreement getDocument() {
		return document;
	}

	public void setDocument(Agreement document) {
		this.document = document;
	}
	
	private String getStringSLOFromAgC(AgreementCondition agC){
		String ret = "";
		String type = getShortURI(agC.getType());
		
		if (type.equals("MinGuaranteedValue")){
			ret += getShortURI(agC.getRefersTo().getId()) + " > " + getAgCValue(agC).toString();
		}else if (type.equals("MaxGuaranteedValue")){
			ret += getShortURI(agC.getRefersTo().getId()) + " < " + getAgCValue(agC).toString();
		}else if (type.equals("GuaranteedValue")){
			ret += getShortURI(agC.getRefersTo().getId()) + " = " + getAgCValue(agC).toString();
		}else{
			
		}
		return ret;
	}
	
	private static Object getAgCValue(AgreementCondition agc){
		Object ret = null;
		
		if ( getShortURI(agc.getHasValue().getType()).equals("QuantitativeValueFloat")){
			ret = agc.getHasValue().getHasValueFloat();
		}else if ( getShortURI(agc.getHasValue().getType()).equals("QuantitativeValueInteger")){
			ret = agc.getHasValue().getHasValueInteger();
		}else{
			
		}
		
		return ret;
	}
	
	private static String getShortURI (URI uri){
		return uri.toString().substring(uri.toString().indexOf("#")+1);
	}
	private static String getShortURI (String uri){
		return uri.substring(uri.indexOf("#")+1);
	}
}
