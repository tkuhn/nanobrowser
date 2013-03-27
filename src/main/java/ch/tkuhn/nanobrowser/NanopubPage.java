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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class NanopubPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;
	
	private Nanopub pub;

	public NanopubPage(final PageParameters parameters) {
		
		pub = new Nanopub(parameters.get("uri").toString());
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", pub.getLastPartOfURI()));
		
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

		add(new VList("sentencelist", pub.getSentenceAssertions(), "Assertion as sentence"));
		
		add(new ListView<Triple<?,?>>("assertion", TripleStoreAccess.sortTriples(pub.getAssertionTriples())) {
			
			private static final long serialVersionUID = 4266539302092878158L;

			protected void populateItem(ListItem<Triple<?,?>> item) {
				Triple<?,?> t = item.getModelObject();
				item.add(new TriplePanel("assertiontriple", t));
			}
			
		});
		
		add(new ListView<Triple<?,?>>("supporting", TripleStoreAccess.sortTriples(pub.getProvenanceTriples())) {
			
			private static final long serialVersionUID = -809372636947729189L;

			protected void populateItem(ListItem<Triple<?,?>> item) {
				Triple<?,?> t = item.getModelObject();
				item.add(new TriplePanel("supportingtriple", t));
			}
			
		});
		
		add(new ListView<Opinion>("opinions", pub.getOpinions(true)) {
			
			private static final long serialVersionUID = 6804591967140101102L;

			protected void populateItem(ListItem<Opinion> item) {
				item.add(new AgentItem("opinionagent", item.getModelObject().getAgent()));
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), false) + "."));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), Thing.TINY_GUI_ITEM));
			}
			
		});
		
		add(new Link<Object>("delete") {
			
			private static final long serialVersionUID = 8608371149183694875L;

			public void onClick() {
				pub.delete();
				setResponsePage(MainPage.class);
			}
			
		});
		
	}

}
