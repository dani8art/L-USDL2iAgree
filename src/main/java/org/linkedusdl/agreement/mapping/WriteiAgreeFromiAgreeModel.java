package org.linkedusdl.agreement.mapping;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import es.us.isa.ada.document.AbstractDocument;
import es.us.isa.ada.wsag10.Agreement;
import es.us.isa.ada.wsag10.BusinessValueList;
import es.us.isa.ada.wsag10.Context;
import es.us.isa.ada.wsag10.CreationConstraints;
import es.us.isa.ada.wsag10.GeneralConstraint;
import es.us.isa.ada.wsag10.GuaranteeTerm;
import es.us.isa.ada.wsag10.OfferItem;
import es.us.isa.ada.wsag10.Penalty;
import es.us.isa.ada.wsag10.ServiceDescriptionTerm;
import es.us.isa.ada.wsag10.ServiceProperties;
import es.us.isa.ada.wsag10.StringSLO;
import es.us.isa.ada.wsag10.StringValueExpr;
import es.us.isa.ada.wsag10.Term;
import es.us.isa.ada.wsag10.Variable;

public class WriteiAgreeFromiAgreeModel {
	

	public WriteiAgreeFromiAgreeModel(){
		
	}
	
	public void writeFile(AbstractDocument doc, CreationConstraints cc, String destination) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(destination, "UTF-8");
		
		Agreement ag = (Agreement)doc;
		
		//context
		//define type of file and name 
		writer.println("Template "+ ag.getName() + " version " + ag.getId() );		
				 
		Context c = ag.getContext();
		writer.println("	Provider as Responder");
		writer.println("");
		//terms
		writer.println("AgreementTerms");
		
		//service description term
		Collection<Term> cterms = ag.getAllTerms();
		for( Term t : cterms){
			String termType = t.getClass().getSimpleName();
			if (termType.equals("ServiceDescriptionTerm")){
				ServiceDescriptionTerm SDT = (ServiceDescriptionTerm) t;
				writer.println("	Service " + SDT.getServiceName() + " avaiableAt. ");
				writer.println("		GlobalDescription");
				for (OfferItem offi : SDT.getOfferItems()){
					writer.println("			"+offi.getName() + ": " + offi.getRestriction().getBaseType()); 
				}
				writer.println("");
			}
		}
		//service properties
		for( Term t : cterms){
			String termType = t.getClass().getSimpleName();
			if (termType.equals("ServiceProperties")){
				ServiceProperties sps = (ServiceProperties) t;
				writer.println("	MonitorableProperties");
				writer.println("		global:");
				for (Variable v : sps.getVariableSet()){
					writer.println("			" + v.getName() +": " + v.getLocation().getContent());
				}
				writer.println("");
			}
		}
		
		//Guarantee Term
		writer.println("	GuaranteeTerms");
		for( Term t : cterms){
			String termType = t.getClass().getSimpleName();
			if (termType.equals("GuaranteeTerm")) {
				GuaranteeTerm gTerm = (GuaranteeTerm) t;
				writer.println("		" + gTerm.getName() + ": " + gTerm.getObligated() +" guarantees "
						+ ((StringSLO)gTerm.getSlo()).getSlo() + " ;");
				
				//add BSL
				BusinessValueList bsl = gTerm.getBvl();
				if(bsl != null){
					//penalty
					writer.println("			with yearly penalty");
					for (Penalty p : bsl.getPenalties()){
						writer.println("				of "+ ((StringValueExpr)p.getVExp()).getValueExpr() + " ;");
					}
					writer.println("			end");
				}				
			}
		}
		writer.println("");
		
		//Creation Constraint
		writer.println("CreationConstraints");
		int i= 1;
		for (GeneralConstraint gc: cc.getConstraints()){
			writer.println("	C" + i + ": " + gc.getConstraint() + " ;");
			i++;
		}		
		writer.println("EndTemplate"); 
		writer.close();
		System.out.println("Document created");
	}

}
