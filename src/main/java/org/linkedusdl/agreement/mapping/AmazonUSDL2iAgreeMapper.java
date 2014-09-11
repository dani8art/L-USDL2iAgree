package org.linkedusdl.agreement.mapping;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.linkedusdl.agreement.model.AgreementCondition;
import org.linkedusdl.agreement.model.Compensation;
import org.linkedusdl.agreement.model.ComputationService;
import org.linkedusdl.agreement.model.EntityInvolvement;
import org.linkedusdl.agreement.model.Guarantee;
import org.linkedusdl.agreement.model.Metric;
import org.linkedusdl.agreement.model.ServiceOffering;
import org.linkedusdl.agreement.model.ServiceProperty;

import es.us.isa.ada.wsag10.BusinessValueList;
import es.us.isa.ada.wsag10.Context;
import es.us.isa.ada.wsag10.CreationConstraints;
import es.us.isa.ada.wsag10.GeneralConstraint;
import es.us.isa.ada.wsag10.GuaranteeTerm;
import es.us.isa.ada.wsag10.Location;
import es.us.isa.ada.wsag10.OfferItem;
import es.us.isa.ada.wsag10.Penalty;
import es.us.isa.ada.wsag10.Restriction;
import es.us.isa.ada.wsag10.ServiceDescriptionTerm;
import es.us.isa.ada.wsag10.ServiceProperties;
import es.us.isa.ada.wsag10.ServiceScope;
import es.us.isa.ada.wsag10.StringSLO;
import es.us.isa.ada.wsag10.StringValueExpr;
import es.us.isa.ada.wsag10.Variable;

import org.linkedusdl.agreement.model.AgreementTerm;

import com.viceversatech.rdfbeans.exceptions.RDFBeanException;


public class AmazonUSDL2iAgreeMapper extends USDL2iAgreeMapper{
	
	private Collection<ServiceOffering> svoff;
	private Collection<ComputationService> cp;
	
	public AmazonUSDL2iAgreeMapper (USDLModel model) throws RDFBeanException{
		super(model);
		getDocument().setName("AmazonEC2");		
		this.svoff = getServiceOfferings();
		this.cp = getComputationServices();
	}
	
	@Override
	public Context getContext() {
		
		return new Context();
	}
	
	public Collection<GuaranteeTerm> getGuaranteeTerms(){
		Collection<GuaranteeTerm> ret = new LinkedList<GuaranteeTerm>();
		try{
		
			for (ServiceOffering in : this.svoff){					
				int n = 1; //var auxiliar para nombrar terms
				for( Guarantee gt : in.getCompliesWith() ){
					GuaranteeTerm gtwsag = new GuaranteeTerm();
					
					gtwsag.setName("G"+n); // establece el nombre del GuaranteeTerm.
					//obligated
					EntityInvolvement entity = gt.getHasObligedEntityInvolvement();
					String role = getShortURI(entity.getWithBusinessRole());
					if(role.equals("provider")){
						gtwsag.setObligated("Provider");
					}else{
						gtwsag.setObligated("Consumer");
					}
						
					//serviceScope
					ServiceScope sp = new ServiceScope();
					sp.setServiceName(getShortURI(gt.getGuaranteedOver()));
					// service level objective
					AgreementCondition agC = gt.getGuarantees();
					StringSLO slo = new StringSLO();
					slo.setSlo(getConditionExpFromAgC(agC));
					gtwsag.setSlo(slo);
						
					//BusinessValueList
					// Penalty
					Collection<Compensation> penalties = gt.getHasCompensation();
					BusinessValueList bsl = new BusinessValueList();
					for(AgreementTerm penalty : penalties){					
						Penalty pl = new Penalty();
						//TimeInterval tinter = new TimeInterval();
						//tinter.setDuration("");
						pl.setValueUnit(getShortURI(penalty.getGuarantees().getRefersTo().getId()));
						StringValueExpr vl = new StringValueExpr();
						vl.setValueExpr(getConditionExpFromAgC(penalty.getGuarantees())+ " if "+
								getConditionExpFromAgC(penalty.getHasPrecondition()));
						pl.setVExp(vl);
						//add Penalty and add bsl to GuaranteeTerm 
						bsl.addPenalty(pl); gtwsag.setBvl(bsl);
					}
					
					//add GuaranteeTerm to agreement
					ret.add(gtwsag);							
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;		
	}
	
	public ServiceProperties getServiceProperties(){
		//sp que seran obtenidas de los refersTo de cada GuarateTerm
		ServiceProperties sps = new ServiceProperties();
		try{
		
			for (ServiceOffering in : this.svoff){
				
				for( Guarantee gt: in.getCompliesWith() ){
					
					AgreementCondition agC = gt.getGuarantees();
					ServiceProperty sp = agC.getRefersTo();
					Variable v = new Variable();
					Metric m = sp.getHasMetric();
					v.setName(getShortURI(sp.getId()));
					v.setLocation(new Location(getShortURI(m.getId())));
					sps.getVariableSet().add(v);
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sps;	
	}
	
	public ServiceDescriptionTerm getServiceDescriptionTerm(){
		//add service description term
		ServiceDescriptionTerm SDT = new ServiceDescriptionTerm();
		try{
			
			SDT.setName("AmazonEC2");
			SDT.setServiceName("AmazonEC2");
			//InstanceType
			OfferItem offi = new OfferItem(SDT, "InstanceType", new Restriction());
			List<OfferItem> offeritems = new LinkedList<OfferItem>();
			offeritems.add(offi);
			for (ComputationService cp : this.cp){
				if(cp.getHasComputingPerformance() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasComputingPerformance().getUnit().toString()));
					OfferItem offi1 = new OfferItem(SDT, "ComputingPerformance", r);
					if(!offeritems.contains(offi1)){
						offeritems.add(offi1);
					}
				}
				if(cp.getHasIOPerformance() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasIOPerformance()));
					OfferItem offi2 = new OfferItem(SDT, "IOPerformance", r);
					if(!offeritems.contains(offi2)){
						offeritems.add(offi2);
					}
				}
				if(cp.getHasInternalStorage() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasInternalStorage().getUnit().toString()));
					OfferItem offi3 = new OfferItem(SDT, "InternalStorage", r);
					if(!offeritems.contains(offi3)){
						offeritems.add(offi3);
					}
				}
				if(cp.getHasMemory() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasMemory().getUnit().toString()));
					OfferItem offi4 = new OfferItem(SDT, "Memory", r);
					if(!offeritems.contains(offi4)){
						offeritems.add(offi4);
					}
				}
				if(cp.getHasVirtualCores() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasVirtualCores().getUnit().toString()));
					OfferItem offi5 = new OfferItem(SDT, "VirtualCores", r);
					if(!offeritems.contains(offi5)){
						offeritems.add(offi5);
					}
				}
		}

			for (OfferItem offia: offeritems){
				SDT.getOfferItems().add(offia);
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return SDT;
	}
	
	public CreationConstraints getCreationConstraints(){
		CreationConstraints cc = new CreationConstraints();
		try{
			
			//creationconstraint ---> Type Agreement no tiene ninguna propiedad, o creation constrain hereda de algo equivocado
			//CreationConstraints cc = new CreationConstraints();
			//solucion alternativa mantener las constrains en el transformador y devolverlas.
			
			for (ComputationService cp : this.cp){
				
					String aux = "InstanceType=" + getShortURI(cp.getId()) + " IMPLIES ";
					if(cp.getHasComputingPerformance() != null){
						try{
							aux += "ComputingPerformance="+cp.getHasComputingPerformance().getHasValueFloat();
						}catch(Exception e){
							aux += "ComputingPerformance < "+cp.getHasComputingPerformance().getHasMaxValueFloat();
						}
					}
					if(cp.getHasIOPerformance() != null){
						aux += " AND IOPerformance="+getShortURI(cp.getHasIOPerformance());
					}
					if(cp.getHasInternalStorage() != null){
						aux += " AND InternalStorage="+cp.getHasInternalStorage().getHasValueFloat();
					}
					if(cp.getHasMemory() != null){
						aux += " AND Memory="+cp.getHasMemory().getHasValueFloat();
					}
					if(cp.getHasVirtualCores() != null){
						aux += " AND VirtualCores="+cp.getHasVirtualCores().getHasValueInteger();
					}
					GeneralConstraint gc = new GeneralConstraint();
					gc.setConstraint(aux);
					cc.addConstraint(gc);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return cc;		
	}
	
	public Collection<ServiceOffering> getServiceOfferings() throws RDFBeanException{
		Collection<ServiceOffering> ret;;
		ret = getModel().getManager().createAll(ServiceOffering.class);
		return ret;
	}
	
	public Collection<ComputationService> getComputationServices() throws RDFBeanException{
		Collection<ComputationService> ret;
		ret = getModel().getManager().createAll(ComputationService.class);
		return ret;
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
