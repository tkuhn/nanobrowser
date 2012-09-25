package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openrdf.query.BindingSet;

public class ClaimPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public ClaimPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", Utils.getSentenceFromURI(uri)));
		
		add(new Label("uri", uri));
		
		String claimQuery = "select distinct ?p where {?p <http://www.nanopub.org/nschema#hasAssertion> ?a . ?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> <" + uri + ">}";
		List<BindingSet> claims = TripleStoreAccess.getTuples(claimQuery);
		
		ListView<BindingSet> claimList = new ListView<BindingSet>("nanopubs", claims) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<BindingSet> item) {
				BindingSet b = item.getModelObject();
				String uri = b.getValue("p").stringValue();
				String s = Utils.getLastPartOfURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", PubPage.class, params);
				item.add(link);
				link.add(new Label("nanopub", s));
			}
			
		};
		add(claimList);
		
	}

}
