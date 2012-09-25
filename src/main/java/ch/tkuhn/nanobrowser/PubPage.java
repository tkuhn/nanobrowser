package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
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

		add(new Label("created", getCreateDateString(uri)));
		
		add(new ListView<String>("claims", getClaims(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getSentenceFromURI(uri);
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("claimlink", ClaimPage.class, params);
				item.add(link);
				link.add(new Label("claim", s));
			}
			
		});
		
	}
	
	public static List<String> getClaims(String pubURI) {
		String query = "select distinct ?c where {" +
			"<" + pubURI + "> <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> claims = new ArrayList<String>();
		for (BindingSet bs : result) {
			claims.add(bs.getValue("c").stringValue());
		}
		return claims;
	}
	
	public static String getCreateDateString(String pubURI) {
		String query = "select ?d where {" +
			"<" + pubURI + "> <http://purl.org/dc/terms/created> ?d . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		if (result.size() == 0) return "(unknown)";
		return result.get(0).getValue("d").stringValue();
	}

}
