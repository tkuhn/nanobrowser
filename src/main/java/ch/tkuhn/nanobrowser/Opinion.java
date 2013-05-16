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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import org.openrdf.model.URI;

import ch.tkuhn.hashuri.rdf.TransformNanopub;

public class Opinion implements Serializable {

	private static final long serialVersionUID = 6930959498954451133L;

	public static final String AGREEMENT_TYPE = "http://purl.org/nanopub/x/Agreement";
	public static final String DISAGREEMENT_TYPE = "http://purl.org/nanopub/x/Disagreement";
	public static final String NULL_TYPE = "http://purl.org/nanopub/x/NullOpinion";
	
	private final AgentElement agent;
	private final String opinionType;
	private final SentenceElement sentence;
	private NanopubElement nanopub;
	
	public Opinion(AgentElement agent, String opinionType, SentenceElement sentence, NanopubElement nanopub) {
		this.agent = agent;
		this.opinionType = opinionType;
		this.sentence = sentence;
		this.nanopub = nanopub;
	}

	public Opinion(AgentElement agent, String opinionType, SentenceElement sentence) {
		this(agent, opinionType, sentence, null);
	}
	
	public AgentElement getAgent() {
		return agent;
	}
	
	public String getOpinionType() {
		return opinionType;
	}
	
	public SentenceElement getSentence() {
		return sentence;
	}
	
	public NanopubElement getNanopub() {
		return nanopub;
	}
	
	private void setNanopub(NanopubElement nanopub) {
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
		try {
			String pubURI = NanobrowserApplication.getProperty("nanopub-server-baseuri") + "meta/";
			String nanopubString = NanopubElement.getTemplate("opinion")
					.replaceAll("@ROOT@", pubURI)
					.replaceAll("@AGENT@", agent.getURI())
					.replaceAll("@OBJECT@", sentence.getURI())
					.replaceAll("@TYPE@", opinionType)
					.replaceAll("@DATETIME@", NanobrowserApplication.getTimestamp());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			URI hashURI = TransformNanopub.transform(new ByteArrayInputStream(nanopubString.getBytes()), out, pubURI);
			String query = TripleStoreAccess.getNanopublishQuery(new ByteArrayInputStream(out.toByteArray()));
			TripleStoreAccess.runUpdateQuery(query);
			setNanopub(new NanopubElement(hashURI.toString()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
