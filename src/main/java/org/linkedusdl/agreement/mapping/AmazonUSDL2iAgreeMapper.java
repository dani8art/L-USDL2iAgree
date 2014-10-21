package org.linkedusdl.agreement.mapping;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.linkedusdl.agreement.model.AgreementCondition;
import org.linkedusdl.agreement.model.Compensation;
import org.linkedusdl.agreement.model.ComputationService;
import org.linkedusdl.agreement.model.EntityInvolvement;
import org.linkedusdl.agreement.model.Guarantee;
import org.linkedusdl.agreement.model.Metric;
import org.linkedusdl.agreement.model.ServiceOffering;
import org.linkedusdl.agreement.model.ServiceProperty;

import es.us.isa.ada.salmon.WebServiceInformation;
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
import es.us.isa.ada.wsag10.ServiceReference;
import es.us.isa.ada.wsag10.ServiceScope;
import es.us.isa.ada.wsag10.ServiceTerm;
import es.us.isa.ada.wsag10.StringSLO;
import es.us.isa.ada.wsag10.StringValueExpr;
import es.us.isa.ada.wsag10.Variable;
import es.us.isa.ada.wsag10.domain.IntegerDomain;
import es.us.isa.ada.wsag10.domain.IntegerRange;
import es.us.isa.ada.wsag10.domain.RealDomain;
import es.us.isa.ada.wsag10.domain.RealRange;

import org.linkedusdl.agreement.model.AgreementTerm;

import com.viceversatech.rdfbeans.exceptions.RDFBeanException;


public class AmazonUSDL2iAgreeMapper extends USDL2iAgreeMapper{
	
	private Collection<ServiceOffering> svoff;
	private Collection<ComputationService> cp;
	private Collection<Guarantee> gt;
	private ServiceDescriptionTerm sdt;
	private Set<Variable> metrics;
	
	public AmazonUSDL2iAgreeMapper (USDLModel model) throws RDFBeanException{
		super(model);
		getDocument().setName("AmazonEC2");	
		getDocument().setId("1.0");
		this.svoff = getServiceOfferings();
		this.gt = getGuarantee();
		this.cp = getComputationServices();
		this.sdt = new ServiceDescriptionTerm();
		this.sdt.setName("AmazonEC2");
		this.sdt.setServiceName("AmazonEC2");
		this.metrics = new HashSet<Variable>();
	}
	
	@Override
	public Context getContext() {
		Context cnt = new Context();
		return cnt;
	}
	
	public Collection<GuaranteeTerm> getGuaranteeTerms(){
		Collection<GuaranteeTerm> ret = new LinkedList<GuaranteeTerm>();
		try{					
				int n = 1; //var auxiliar para nombrar terms
				for( Guarantee gt : this.gt ){
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
					List<OfferItem> aux = new LinkedList<OfferItem>();
					for(AgreementTerm penalty : penalties){					
						Penalty pl = new Penalty();
						//TimeInterval tinter = new TimeInterval();
						//tinter.setDuration("");
						
						// add referto to sdt
						Restriction r = new Restriction();
						r.setBaseType("Percent");
						OfferItem offi5 = new OfferItem(this.sdt,getShortURI(penalty.getGuarantees().getRefersTo().getId()) , r);
						
						if(!aux.contains(offi5)){
							this.sdt.getOfferItems().add(offi5);
							aux.add(offi5);
						}
						//fin
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
					
					if(sp.getHasMetric() != null){
						Metric ma = sp.getHasMetric();
						Variable va = new Variable();
						va.setName(getShortURI(ma.getId()));
						va.setLocation(new Location(getShortURI(ma.getUnit().toString())));
						IntegerDomain d = new IntegerDomain();
						d.addRange(new IntegerRange(1, 100));
						va.setDomain(d);
						this.metrics.add(va);
					}
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sps;	
	}
	
	public ServiceDescriptionTerm getServiceDescriptionTerm(){
		//add service description term
		try{
			WebServiceInformation wsi = new WebServiceInformation("ServiceReference", "", "", "aws.amazon.com/ec2/", "",new HashMap<String, String>());
			this.sdt.setWebServiceInformation(wsi);
			
			List<OfferItem> offeritems = new LinkedList<OfferItem>();
			List<String> setOfvalues = new LinkedList<String>();
				
			for (ComputationService cp : this.cp){
				setOfvalues.add(getShortURI(cp.getId()));
				if(cp.getHasComputingPerformance() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasComputingPerformance().getUnit().toString()));
					OfferItem offi1 = new OfferItem(this.sdt, "ComputingPerformance", r);
					if(!offeritems.contains(offi1)){
						offeritems.add(offi1);
					}
				}
				if(cp.getHasIOPerformance() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasIOPerformance()));
					OfferItem offi2 = new OfferItem(this.sdt, "IOPerformance", r);
					if(!offeritems.contains(offi2)){
						offeritems.add(offi2);
					}
				}
				if(cp.getHasInternalStorage() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasInternalStorage().getUnit().toString()));
					OfferItem offi3 = new OfferItem(this.sdt, "InternalStorage", r);
					if(!offeritems.contains(offi3)){
						offeritems.add(offi3);
					}
				}
				if(cp.getHasMemory() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasMemory().getUnit().toString()));
					OfferItem offi4 = new OfferItem(this.sdt, "Memory", r);
					if(!offeritems.contains(offi4)){
						offeritems.add(offi4);
					}
				}
				if(cp.getHasVirtualCores() != null){
					Restriction r = new Restriction();
					r.setBaseType(getShortURI(cp.getHasVirtualCores().getUnit().toString()));
					OfferItem offi5 = new OfferItem(this.sdt, "VirtualCores", r);
					if(!offeritems.contains(offi5)){
						offeritems.add(offi5);
					}
				}
		}

			//InstanceType
			String restricionVal = "set {";
			for( String val: setOfvalues){
				if(setOfvalues.indexOf(val) != setOfvalues.size()-1){
					restricionVal += val + ", ";
				}else{
					restricionVal += val + "}";
				}
			}
			Restriction rtype = new Restriction();
			rtype.setBaseType(restricionVal);
			OfferItem offi = new OfferItem(this.sdt, "InstanceType", rtype);
			
			offeritems.add(offi);
			for (OfferItem offia: offeritems){
				this.sdt.getOfferItems().add(offia);
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return this.sdt;
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
	
	public Collection<Guarantee> getGuarantee() throws RDFBeanException{
		Collection<Guarantee> ret;
		ret = getModel().getManager().createAll(Guarantee.class);
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
	
	public Set<Variable> getMetrics(){
		return this.metrics;
	}

	
}
