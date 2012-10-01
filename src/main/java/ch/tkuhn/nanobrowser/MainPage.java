package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getAllNanopubs;
import static ch.tkuhn.nanobrowser.NanopubAccess.getAllPersons;
import static ch.tkuhn.nanobrowser.NanopubAccess.getAllSentenceAssertions;

import org.apache.wicket.markup.html.WebPage;
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
				item.add(new PubItem("nanopub", uri));
			}
			
		});
		
		add(new ListView<String>("persons", getAllPersons(20)) {
			
			private static final long serialVersionUID = 3911519757128281636L;
			
			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new PersonItem("person", uri));
			}
			
		});
		
		add(new ListView<String>("sassertions", getAllSentenceAssertions(20)) {
			
			private static final long serialVersionUID = 3911519757128281636L;
			
			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new AssertionItem("sassertion", uri));
			}
			
		});
		
	}

}
