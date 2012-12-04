package ch.tkuhn.nanobrowser;

import java.util.HashMap;
import java.util.Map;

public enum SentenceRelation {
	
	HAS_RELATED_MEANING("hasRelatedMeaning", "related meaning"),
	HAS_UNRELATED_MEANING("hasUnelatedMeaning", "unrelated meaning"),
	HAS_SAME_MEANING("hasSameMeaning", "same meaning"),
	HAS_DIFFERENT_MEANING("hasDifferentMeaning", "different meaning"),
	HAS_OPPOSITE_MEANING("hasOppositeMeaning", "opposite meaning"),
	HAS_NONOPPOSITE_MEANING("hasNonoppositeMeaning", "opposite meaning"),
	HAS_CONFLICTING_MEANING("hasConflictingMeaning", "conflicting meaning"),
	HAS_CONSISTENT_MEANING("hasConsistentMeaning", "consistent meaning"),
	HAS_MORE_GENERAL_MEANING_THAN("hasMoreGeneralMeaningThan", "more general meaning"),
	HAS_MORE_SPECIFIC_MEANING_THAN("hasMoreSpecificMeaningThan", "more specific meaning"),
	IS_IMPROVED_VERSION_OF("isImprovedVersionOf", "improved version");

	private static final Map<String, SentenceRelation> map = new HashMap<String, SentenceRelation>();
	
	static {
        for(SentenceRelation sr : SentenceRelation.values()) {
        	map.put(sr.getURI(), sr);
        }
   }
	
	public static SentenceRelation get(String uri) {
		return map.get(uri);
	}
	
	private static final String prefix = "http://krauthammerlab.med.yale.edu/nanopub/";
	
	private String symbol, text;
	
	private SentenceRelation(String symbol, String text) {
		this.symbol = symbol;
		this.text = text;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getText() {
		return text;
	}
	
	public String getURI() {
		return prefix + symbol;
	}
	
	public String toString() {
		return text;
	}

}
