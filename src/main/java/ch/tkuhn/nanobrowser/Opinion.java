package ch.tkuhn.nanobrowser;

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

}
