package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getAllSentenceAssertions;
import static ch.tkuhn.nanobrowser.NanopubAccess.getAllFormulaAssertions;
import static ch.tkuhn.nanobrowser.NanopubAccess.getAllNanopubs;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MainPage extends WebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	public MainPage(final PageParameters parameters) {
		
		add(new ListView<String>("nanopubs", getAllNanopubs(20)) {
			
			private static final long serialVersionUID = 1587686459411075758L;
			
			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getLastPartOfURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", PubPage.class, params);
				item.add(link);
				link.add(new Label("nanopubname", s));
			}
			
		});
		
		add(new ListView<String>("sassertions", getAllSentenceAssertions(20)) {
			
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
		
		add(new ListView<String>("fassertions", getAllFormulaAssertions(20)) {
			
			private static final long serialVersionUID = 1323106346907312283L;
			
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
		
	}

}
