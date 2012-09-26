package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;

public class NanopubAccess {
	
	// No instances allowed:
	private NanopubAccess() {}
	
	public static List<String> getAllNanopubs(int limit) {
		String nanopubsQuery = "select distinct ?p where {?p a <http://www.nanopub.org/nschema#Nanopublication>}";
		if (limit >= 0) nanopubsQuery += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(nanopubsQuery);
		List<String> nanopubs = new ArrayList<String>();
		for (BindingSet bs : result) {
			nanopubs.add(bs.getValue("p").stringValue());
		}
		return nanopubs;
	}
	
	public static List<String> getNanopubs(String claimURI) {
		String query = "select distinct ?p where { { " +
			"?p <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> <" + claimURI + "> . " +
			"} union { " +
			"?p <http://www.nanopub.org/nschema#hasAssertion> <" + claimURI + "> . " +
			" } }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> nanopubs = new ArrayList<String>();
		for (BindingSet bs : result) {
			nanopubs.add(bs.getValue("p").stringValue());
		}
		return nanopubs;
	}
	
	public static List<String> getAllClaims(int limit) {
		String claimQuery = "select distinct ?c where {?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c}";
		if (limit >= 0) claimQuery += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(claimQuery);
		List<String> claims = new ArrayList<String>();
		for (BindingSet bs : result) {
			claims.add(bs.getValue("c").stringValue());
		}
		return claims;
	}
	
	public static List<String> getClaims(String pubURI) {
		String query = "select distinct ?c where {" +
			"<" + pubURI + "> <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> claims = new ArrayList<String>();
		for (BindingSet bs : result) {
			claims.add(bs.getValue("c").stringValue());
		}
		return claims;
	}
	
	public static List<String> getAllFormulas(int limit) {
		String formulaQuery = "select distinct ?f where { { " +
			"{?s <http://www.nanopub.org/nschema#hasAssertion> ?f}" +
			" union " +
			"{?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asFormula> ?f}" +
			" } graph ?f {?a ?b ?c} }";
		if (limit >= 0) formulaQuery += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(formulaQuery);
		List<String> formulas = new ArrayList<String>();
		for (BindingSet bs : result) {
			formulas.add(bs.getValue("f").stringValue());
		}
		return formulas;
	}
	
	public static List<String> getFormulas(String pubURI) {
		String formulaQuery = "select distinct ?f where { { " +
			"{<" + pubURI + "> <http://www.nanopub.org/nschema#hasAssertion> ?f}" +
			" union " +
			"{<" + pubURI + "> <http://krauthammerlab.med.yale.edu/nanopub/extensions/asFormula> ?f}" +
			" } graph ?f {?a ?b ?c} }";
		List<BindingSet> result = TripleStoreAccess.getTuples(formulaQuery);
		List<String> formulas = new ArrayList<String>();
		for (BindingSet bs : result) {
			formulas.add(bs.getValue("f").stringValue());
		}
		return formulas;
	}
	
	public static String getCreateDateString(String pubURI) {
		String query = "select ?d where {" +
			"<" + pubURI + "> <http://purl.org/dc/terms/created> ?d . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		if (result.size() == 0) return "(unknown)";
		return result.get(0).getValue("d").stringValue();
	}

}
