package org.linkedusdl.agreement.mapping;

import java.net.URI;

import org.linkedusdl.agreement.model.AgreementCondition;
import org.linkedusdl.agreement.model.Metric;
import org.linkedusdl.agreement.model.ServiceOffering;
import org.linkedusdl.agreement.model.ServiceProperty;

import es.us.isa.ada.wsag10.Agreement;
import es.us.isa.ada.wsag10.AssesmentInterval;
import es.us.isa.ada.wsag10.BusinessValueList;
import es.us.isa.ada.wsag10.Location;
import es.us.isa.ada.wsag10.Penalty;
import es.us.isa.ada.wsag10.ServiceProperties;
import es.us.isa.ada.wsag10.StringSLO;
import es.us.isa.ada.wsag10.StringValueExpr;
import es.us.isa.ada.wsag10.TermCompositor;
import es.us.isa.ada.wsag10.TimeInterval;
import es.us.isa.ada.wsag10.Variable;

import org.linkedusdl.agreement.model.GuaranteeTerm;



public class USDL2iAgreeClassMapping {
	
	protected Agreement document;
	
	public USDL2iAgreeClassMapping (){
		this.document = new Agreement();				
	}
	
	public Agreement transform (ServiceOffering in){
		// Name = URI
		try{
			getDocument().setName(getShortURI(in.getId()));		
			
			TermCompositor tcmp = new TermCompositor();
			//sp que seran obtenidas de los refersTo de cada GuarateTerm
			ServiceProperties sps = new ServiceProperties();
			
			int n = 1; //var auxiliar para nombrar terms
			for( GuaranteeTerm gt: in.getComplyWith() ){
				
				es.us.isa.ada.wsag10.GuaranteeTerm gtwsag = new es.us.isa.ada.wsag10.GuaranteeTerm();
				gtwsag.setName("G"+n); // establece el nombre del GuaranteeTerm.
				
				// service level objective
				AgreementCondition agC = gt.getGuarantees();
				StringSLO slo = new StringSLO();
				slo.setSlo(getConditionExpFromAgC(agC));
				gtwsag.setSlo(slo);
				
				//BusinessValueList
				// Penalty
				AgreementCondition penalty = gt.getHasCompensation();
				if(penalty != null){
					BusinessValueList bsl = new BusinessValueList();
					Penalty pl = new Penalty();
					//TimeInterval tinter = new TimeInterval();
					//tinter.setDuration("");
					pl.setValueUnit(getShortURI(penalty.getId()));
					StringValueExpr vl = new StringValueExpr();
					vl.setValueExpr("of "+getConditionExpFromAgC(penalty)+ " if "+
							getPenaltyExpFromAgC(agC));
					pl.setVExp(vl);
					//add Penalty and add bsl to GuaranteeTerm 
					bsl.addPenalty(pl); gtwsag.setBvl(bsl);
				}
				
				//refersTo a SP
				ServiceProperty sp = agC.getRefersTo();
				Variable v = new Variable();
				Metric m = sp.getHasMetric();
				v.setName(getShortURI(sp.getId()));
				v.setLocation(new Location(m.getId()));
				sps.getVariableSet().add(v);
						
				//add GuaranteeTerm to agreement
				tcmp.addComprisedTerm(gtwsag);
				
				
			}
			//add SP
			tcmp.addComprisedTerm(sps);
			
			//add Terms
			getDocument().setTerms(tcmp);
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		return document;		
	}

	public Agreement getDocument() {
		return document;
	}

	public void setDocument(Agreement document) {
		this.document = document;
	}
	
	private String getConditionExpFromAgC(AgreementCondition agC){
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
	
	private String getPenaltyExpFromAgC(AgreementCondition agC){
		String ret = "";
		String type = getShortURI(agC.getType());
		
		if (type.equals("MinGuaranteedValue")){
			ret += getShortURI(agC.getRefersTo().getId()) + " < " + getAgCValue(agC).toString();
		}else if (type.equals("MaxGuaranteedValue")){
			ret += getShortURI(agC.getRefersTo().getId()) + " > " + getAgCValue(agC).toString();
		}else if (type.equals("GuaranteedValue")){
			ret += getShortURI(agC.getRefersTo().getId()) + " != " + getAgCValue(agC).toString();
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
