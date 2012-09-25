package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openrdf.query.BindingSet;

public class PubPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public PubPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", Utils.getLastPartOfURI(uri)));
		
		add(new Label("uri", uri));
		
		String claimQuery = "select distinct ?c where {<" + uri + "> <http://www.nanopub.org/nschema#hasAssertion> ?a . ?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c}";
		List<BindingSet> claims = TripleStoreAccess.getTuples(claimQuery);
		
		ListView<BindingSet> claimList = new ListView<BindingSet>("claims", claims) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<BindingSet> item) {
				BindingSet b = item.getModelObject();
				String uri = b.getValue("c").stringValue();
				String s = Utils.getSentenceFromURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("claimlink", ClaimPage.class, params);
				item.add(link);
				link.add(new Label("claim", s));
			}
			
		};
		add(claimList);
		
	}

}
