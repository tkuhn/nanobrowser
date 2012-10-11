package ch.tkuhn.nanobrowser;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Sentence extends Thing {
	
	private static final long serialVersionUID = -7967327315454171639L;
	
	public static final String TYPE_URI = "http://krauthammerlab.med.yale.edu/nanopub/claims/claim";
	
	public Sentence(String uri) {
		super(uri);
	}
	
	private static final String allSentencesQuery =
		"select distinct ?s where {?a ex:asSentence ?s}";
	
	public static List<Sentence> getAllSentences(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allSentencesQuery + lm);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	public static boolean isSentence(String uri) {
		return uri.startsWith("http://krauthammerlab.med.yale.edu/nanopub/claims/");
	}
	
	private static final String nanopubsQuery =
		"select distinct ?p where { ?p np:hasAssertion ?a . ?a ex:asSentence <@> . ?p np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?x dc:created ?d } }";
	
	public List<Nanopub> getNanopubs() {
		String query = nanopubsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}

	private static final String opinionsQuery =
		"select ?p ?t ?pub where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?x dc:created ?d } . " +
		"graph ?ass { ?p ex:hasOpinion ?o . ?o ex:opinionType ?t . ?o ex:opinionOn ?s } ." +
		"?ass2 ex:asSentence <@> . ?ass2 ex:asSentence ?s } order by asc(?d)";
	
	public List<Opinion> getOpinions(boolean excludeNullOpinions) {
		String query = opinionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Opinion> opinionMap = new HashMap<String, Opinion>();
		for (BindingSet bs : result) {
			Value p = bs.getValue("p");
			Value t = bs.getValue("t");
			Value pub = bs.getValue("pub");
			if (p instanceof BNode || t instanceof BNode || pub instanceof BNode) continue;
			if (excludeNullOpinions && t.stringValue().equals(Opinion.NULL_TYPE)) {
				opinionMap.remove(p.stringValue());
			} else {
				Person person = new Person(p.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				Opinion opinion = new Opinion(person, t.stringValue(), this, nanopub);
				opinionMap.put(p.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}
	
	private static final String sameMeaningSentencesQuery =
		"select ?s ?pub where { ?pub np:hasAssertion ?a . ?a ex:asSentence <@> . ?a ex:asSentence ?s }";
	
	public List<Pair<Sentence,Nanopub>> getSameMeaningSentences() {
		String query = sameMeaningSentencesQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Pair<Sentence,Nanopub>> sentences = new ArrayList<Pair<Sentence,Nanopub>>();
		for (BindingSet bs : result) {
			Value s = bs.getValue("s");
			Value pub = bs.getValue("pub");
			if (s instanceof BNode || pub instanceof BNode) continue;
			if (!s.stringValue().equals(getURI())) {
				Sentence sentence = new Sentence(s.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				sentences.add(new ImmutablePair<Sentence,Nanopub>(sentence, nanopub));
			}
		}
		return sentences;
	}
	
	public String getSentenceText() {
		try {
			return URLDecoder.decode(getLastPartOfURI(), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public SentenceItem createGUIItem(String id) {
		return new SentenceItem(id, this);
	}

}
