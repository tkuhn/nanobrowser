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

import java.net.URLEncoder;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class NanopubPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;
	
	private NanopubElement pub;

	public NanopubPage(final PageParameters parameters) {
		
		pub = new NanopubElement(parameters.get("uri").toString());
		
		add(new MenuBar("menubar"));

		WebMarkupContainer icon = new WebMarkupContainer("icon");
		if (pub.isValid()) {
			icon.add(new AttributeModifier("src", new Model<String>("icons/nanopubv.svg")));
		}
		add(icon);
		
		add(new Label("title", pub.getShortName()));
		
		add(new Link<Object>("trig") {
			
			private static final long serialVersionUID = 4680516569316406945L;

			@SuppressWarnings("deprecation")
			public void onClick() {
				throw new RedirectToUrlException("./trig?uri=" + URLEncoder.encode(pub.getURI()));
			}
			
		});
		
		add(new Link<Object>("xml") {
			
			private static final long serialVersionUID = 4680516569316406945L;

			@SuppressWarnings("deprecation")
			public void onClick() {
				throw new RedirectToUrlException("./xml?uri=" + URLEncoder.encode(pub.getURI()));
			}
			
		});
		
		add(new Link<Object>("nq") {
			
			private static final long serialVersionUID = 4680516569316406945L;

			@SuppressWarnings("deprecation")
			public void onClick() {
				throw new RedirectToUrlException("./nq?uri=" + URLEncoder.encode(pub.getURI()));
			}
			
		});
		
		add(new ExternalLink("uri", pub.getURI(), pub.getTruncatedURI()));
		
		add(new HList("typelist", pub.getTypes(), "Types"));
		
		String dateString = pub.getCreateDateString();
		if (dateString == null) {
			add(new Label("dateempty", "(unknown)"));
			add(new Label("date", ""));
		} else {
			add(new Label("dateempty", ""));
			add(new Label("date", dateString));
		}

		add(new HList("authorlist", pub.getAuthors(), "Authors"));
		
		add(new HList("creatorlist", pub.getCreators(), "Creator"));

		List<SentenceElement> sentass = pub.getSentenceAssertions();
		if (sentass.size() > 0) {
			add(new VList("sentencelist", sentass, "Assertion as sentence"));
		} else {
			add(new Label("sentencelist", ""));
		}

		List<Triple<?,?>> ass = pub.getAssertionTriples();
		if (ass.size() > 0) {
			String assUri = pub.getNanopub().getAssertionUri().stringValue();
			String note = null;
			if (!pub.hasCompleteFormalAssertionQuery()) note = "(incomplete)";
			add(new VList("asslist", TripleStoreAccess.sortTriples(ass), "Assertion as formula", assUri, note));
		} else {
			add(new Label("asslist", ""));
		}

		List<Triple<?,?>> prov = pub.getProvenanceTriples();
		if (prov.size() > 0) {
			add(new VList("provlist", TripleStoreAccess.sortTriples(prov), "Provenance", pub.getURI()));
		} else {
			add(new Label("provlist", ""));
		}

		List<Opinion> opinions = pub.getOpinions(true);

		add(new Label("emptyopinions", opinions.isEmpty() ? "(nothing)" : ""));
		
		add(new ListView<Opinion>("opinions", opinions) {
			
			private static final long serialVersionUID = 6804591967140101102L;

			protected void populateItem(ListItem<Opinion> item) {
				item.add(new AgentItem("opinionagent", item.getModelObject().getAgent()));
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), false) + "."));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), ThingElement.TINY_GUI_ITEM));
			}
			
		});

		WebMarkupContainer aa = new WebMarkupContainer("adminactions");
		if (NanobrowserApplication.isInDevelopmentMode()) {
			aa.add(new Link<Object>("delete") {
				
				private static final long serialVersionUID = 8608371149183694875L;

				public void onClick() {
					pub.delete();
					MainPage.resetLists();
					setResponsePage(MainPage.class);
				}
				
			});
		} else {
			aa.add(new AttributeModifier("class", new Model<String>("hidden")));
			aa.add(new Label("delete", ""));
		}
		add(aa);
		
	}

}
