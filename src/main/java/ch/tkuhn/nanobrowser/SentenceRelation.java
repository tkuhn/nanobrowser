// This file is part of Nanobrowser.
// Copyright 2012, Tobias Kuhn, http://www.tkuhn.ch
//
// Nanobrowser is free software: you can redistribute it and/or modify it under the terms of the
// GNU Lesser General Public License as published by the Free Software Foundation, either version
// 3 of the License, or (at your option) any later version.
//
// Nanobrowser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License along with Nanobrowser.
// If not, see http://www.gnu.org/licenses/.

package ch.tkuhn.nanobrowser;

import java.util.HashMap;
import java.util.Map;

public enum SentenceRelation {
	
	HAS_RELATED_MEANING("hasRelatedMeaning", "related meaning"),
	HAS_UNRELATED_MEANING("hasUnrelatedMeaning", "unrelated meaning"),
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
