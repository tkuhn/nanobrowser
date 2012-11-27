package ch.tkuhn.nanobrowser;

import java.util.Random;

public class Opinion {
	
	public static final String AGREEMENT_TYPE = "http://krauthammerlab.med.yale.edu/nanopub/Agreement";
	public static final String DISAGREEMENT_TYPE = "http://krauthammerlab.med.yale.edu/nanopub/Disagreement";
	public static final String NULL_TYPE = "http://krauthammerlab.med.yale.edu/nanopub/NullOpinion";
	
	private final Agent agent;
	private final String opinionType;
	private final Sentence sentence;
	private Nanopub nanopub;
	
	public Opinion(Agent agent, String opinionType, Sentence sentence, Nanopub nanopub) {
		this.agent = agent;
		this.opinionType = opinionType;
		this.sentence = sentence;
		this.nanopub = nanopub;
	}

	public Opinion(Agent agent, String opinionType, Sentence sentence) {
		this(agent, opinionType, sentence, null);
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public String getOpinionType() {
		return opinionType;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public Nanopub getNanopub() {
		return nanopub;
	}
	
	private void setNanopub(Nanopub nanopub) {
		this.nanopub = nanopub;
	}
	
	public String getVerbPhrase(boolean withObject) {
		return getVerbPhrase(opinionType, withObject);
	}
	
	public static String getVerbPhrase(String opinionType, boolean withObject) {
		if (opinionType.equals(AGREEMENT_TYPE)) {
			return "agrees" + (withObject ? " with" : "");
		} else if (opinionType.equals(DISAGREEMENT_TYPE)) {
			return "disagrees" + (withObject ? " with" : "");
		} else if (opinionType.equals(NULL_TYPE)) {
			return "has no opinion" + (withObject ? " on" : "");
		}
		return null;
	}
	
	public void publish() {
		String pubURI = "http://www.tkuhn.ch/nanobrowser/meta/" +
				(new Random()).nextInt(1000000000);
		String query = TripleStoreAccess.getNanopublishQueryTemplate("opinion")
				.replaceAll("@ROOT@", pubURI)
				.replaceAll("@AGENT@", agent.getURI())
				.replaceAll("@OBJECT@", sentence.getURI())
				.replaceAll("@TYPE@", opinionType)
				.replaceAll("@DATETIME@", NanobrowserApplication.getTimestamp());
		TripleStoreAccess.runUpdateQuery(query);
		setNanopub(new Nanopub(pubURI));
	}

}
