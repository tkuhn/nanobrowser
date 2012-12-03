package ch.tkuhn.nanobrowser;

public enum SentenceRelation {
	
	HAS_RELATED_MEANING("hasRelatedMeaning", "related"),
	HAS_UNRELATED_MEANING("hasUnelatedMeaning", "unrelated"),
	HAS_SAME_MEANING("hasSameMeaning", "same"),
	HAS_DIFFERENT_MEANING("hasDifferentMeaning", "different"),
	HAS_OPPOSITE_MEANING("hasOppositeMeaning", "opposite"),
	HAS_CONSISTENT_MEANING("hasConsistentMeaning", "consistent"),
	HAS_CONFLICTING_MEANING("hasConflictingMeaning", "conflicting"),
	HAS_MORE_GENERAL_MEANING_THAN("hasMoreGeneralMeaningThan", "more general"),
	HAS_MORE_SPECIFIC_MEANING_THAN("hasMoreSpecificMeaningThan", "more specific");

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
