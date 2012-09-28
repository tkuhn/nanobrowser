package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getSentenceAssertions;
import static ch.tkuhn.nanobrowser.NanopubAccess.getCreateDateString;
import static ch.tkuhn.nanobrowser.NanopubAccess.getFormulaAssertions;
import static ch.tkuhn.nanobrowser.NanopubAccess.getAuthors;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PubPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public PubPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", Utils.getLastPartOfURI(uri)));
		
		//add(new Label("uri", uri));

		add(new Label("created", getCreateDateString(uri)));
		
		add(new ListView<String>("sassertions", getSentenceAssertions(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getSentenceFromURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("sassertionlink", AssertionPage.class, params);
				item.add(link);
				link.add(new Label("sassertion", s));
			}
			
		});
		
		add(new ListView<String>("fassertions", getFormulaAssertions(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getGraphSummary(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("fassertionlink", AssertionPage.class, params);
				item.add(link);
				link.add(new Label("fassertion", s));
			}
			
		});
		
		add(new ListView<String>("authors", getAuthors(uri)) {

			private static final long serialVersionUID = 6872614881667929445L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getLastPartOfURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("authorlink", PersonPage.class, params);
				item.add(link);
				link.add(new Label("author", s));
			}
			
		});
		
	}

}
