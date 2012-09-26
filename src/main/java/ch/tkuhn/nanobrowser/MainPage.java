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

public class MainPage extends WebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	public MainPage(final PageParameters parameters) {
		
		add(new ListView<String>("nanopubs", getAllNanopubs()) {
			
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
		
		add(new ListView<String>("claims", getAllClaims()) {
			
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
		
		add(new ListView<String>("formulas", getAllFormulas()) {
			
			private static final long serialVersionUID = 1323106346907312283L;
			
			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				String s = Utils.getGraphSummary(TripleStoreAccess.getGraph(uri));
				PageParameters params = new PageParameters();
				params.add("uri", uri);
				BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("formulalink", ClaimPage.class, params);
				item.add(link);
				link.add(new Label("formula", s));
			}
			
		});
		
	}
	
	public static List<String> getAllNanopubs() {
		String nanopubsQuery = "select distinct ?p where {?p a <http://www.nanopub.org/nschema#Nanopublication>}";
		List<BindingSet> result = TripleStoreAccess.getTuples(nanopubsQuery);
		List<String> nanopubs = new ArrayList<String>();
		for (BindingSet bs : result) {
			nanopubs.add(bs.getValue("p").stringValue());
		}
		return nanopubs;
	}
	
	public static List<String> getAllClaims() {
		String claimQuery = "select distinct ?c where {?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c}";
		List<BindingSet> result = TripleStoreAccess.getTuples(claimQuery);
		List<String> claims = new ArrayList<String>();
		for (BindingSet bs : result) {
			claims.add(bs.getValue("c").stringValue());
		}
		return claims;
	}
	
	public static List<String> getAllFormulas() {
		String formulaQuery = "select distinct ?f where { { " +
			"{?s <http://www.nanopub.org/nschema#hasAssertion> ?f}" +
			" union " +
			"{?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asFormula> ?f}" +
			" } graph ?f {?a ?b ?c} }";
		List<BindingSet> result = TripleStoreAccess.getTuples(formulaQuery);
		List<String> formulas = new ArrayList<String>();
		for (BindingSet bs : result) {
			formulas.add(bs.getValue("f").stringValue());
		}
		return formulas;
	}

}
