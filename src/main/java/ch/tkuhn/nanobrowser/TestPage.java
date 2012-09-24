package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openrdf.query.BindingSet;

public class TestPage extends WebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	public TestPage(final PageParameters parameters) {
		
		String query = "select distinct ?p where {?p a <http://www.nanopub.org/nschema#Nanopublication>}";
		List<BindingSet> nanopubs = TripleStoreAccess.getTuples(query);
		
		ListView<BindingSet> listview = new ListView<BindingSet>("nanopubs", nanopubs) {
			
			private static final long serialVersionUID = 1587686459411075758L;
			
			protected void populateItem(ListItem<BindingSet> item) {
				BindingSet b = item.getModelObject();
				item.add(new Label("nanopub", b.getValue("p").stringValue()));
			}
			
		};
		add(listview);
		
	}

}
