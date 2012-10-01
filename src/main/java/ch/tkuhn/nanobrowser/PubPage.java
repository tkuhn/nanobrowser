package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getAuthors;
import static ch.tkuhn.nanobrowser.NanopubAccess.getCreateDateString;
import static ch.tkuhn.nanobrowser.NanopubAccess.getSentenceAssertions;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openrdf.model.Statement;

public class PubPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public PubPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", Utils.getLastPartOfURI(uri)));
		
		add(new ExternalLink("uri", uri, uri));

		String dateString = getCreateDateString(uri);
		if (dateString == null) {
			add(new Label("dateempty", "(unknown)"));
			add(new Label("date", ""));
		} else {
			add(new Label("dateempty", ""));
			add(new Label("date", dateString));
		}
		
		List<String> authors = getAuthors(uri);
		add(new Label("authorsempty", authors.size() == 0 ? "(unknown)" : ""));
		add(new ListView<String>("authors", authors) {

			private static final long serialVersionUID = 6872614881667929445L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new PersonItem("author", uri));
			}
			
		});
		
		add(new ListView<String>("sassertions", getSentenceAssertions(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new AssertionItem("sassertion", uri));
			}
			
		});
		
		List<Statement> assTriples;
		List<String> a = NanopubAccess.getFormulaAssertions(uri);
		if (a.size() > 0) {
			assTriples = TripleStoreAccess.getGraph(a.get(0));
		} else {
			assTriples = new ArrayList<Statement>();
		}
		
		add(new ListView<Statement>("fassertions", assTriples) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Statement> item) {
				Statement st = item.getModelObject();
				item.add(new TriplePanel("fassertion", st));
			}
			
		});
		
	}

}
