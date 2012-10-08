package ch.tkuhn.nanobrowser;

import java.util.Random;

import org.openrdf.query.BindingSet;

public class Opinion {
	
	public static final String AGREEMENT_TYPE = "http://krauthammerlab.med.yale.edu/nanopub/extensions/Agreement";
	public static final String DISAGREEMENT_TYPE = "http://krauthammerlab.med.yale.edu/nanopub/extensions/Disagreement";
	public static final String NULL_TYPE = "http://krauthammerlab.med.yale.edu/nanopub/extensions/NullOpinion";
	
	private final Person person;
	private final String opinionType;
	private final Sentence sentence;
	
	public Opinion(Person person, String opinionType, Sentence sentence) {
		this.person = person;
		this.opinionType = opinionType;
		this.sentence = sentence;
	}
	
	public Person getPerson() {
		return person;
	}
	
	public String getOpinionType() {
		return opinionType;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public String getVerbPhrase() {
		return getVerbPhrase(opinionType);
	}
	
	public static String getVerbPhrase(String opinionType) {
		if (opinionType.equals(AGREEMENT_TYPE)) {
			return "agrees";
		} else if (opinionType.equals(DISAGREEMENT_TYPE)) {
			return "disagrees";
		} else if (opinionType.equals(NULL_TYPE)) {
			return "has no opinion";
		}
		return null;
	}
	
	private static final String publishOpinionQuery =
		"prefix : <@I> insert data into graph : { :Pub a np:Nanopublication . :Pub np:hasAssertion :Ass . " +
		":Pub np:hasProvenance :Prov . :Prov np:hasAttribution :Att . :Prov np:hasSupporting :Supp } \n\n" +
		"prefix : <@I> insert data into graph :Ass { <@P> ex:hasOpinion :Op . :Op ex:opinionType <@T> . :Op ex:opinionOn <@S> } \n\n" +
		"prefix : <@I> insert data into graph :Att { :Pub pav:authoredBy <@P> . :Pub dc:created \"@D\"^^xsd:dateTime }";
	
	public void publish() {
		String query = publishOpinionQuery
				.replaceAll("@I", "http://foo.org/" + (new Random()).nextInt(1000000000))
				.replaceAll("@P", person.getURI())
				.replaceAll("@S", sentence.getURI())
				.replaceAll("@T", opinionType)
				.replaceAll("@D", NanobrowserApplication.getTimestamp());
		TripleStoreAccess.runUpdateQuery(query);
	}
	
	private static final String getAllOpinionGraphsQuery =
		"select ?g ?ass ?att where { graph ?ass { ?a ex:opinionOn ?c } . " +
		"graph ?g { ?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att } }";
	private static final String deleteGraphQuery =
		"delete from graph identified by <@> { ?a ?b ?c } where  { ?a ?b ?c }";
	
	public static void deleteAllOpinionNanopubs() {
		for (BindingSet bs : TripleStoreAccess.getTuples(getAllOpinionGraphsQuery)) {
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("g").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("ass").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("att").stringValue()));
		}
	}

}
