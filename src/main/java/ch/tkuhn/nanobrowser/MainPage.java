package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MainPage extends WebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	public MainPage(final PageParameters parameters) {
		
		add(new ListView<Nanopub>("nanopubs", Nanopub.getAllNanopubs(20)) {
			
			private static final long serialVersionUID = 1587686459411075758L;
			
			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject()));
			}
			
		});
		
		add(new ListView<Person>("persons", Person.getAllPersons(20)) {
			
			private static final long serialVersionUID = 3911519757128281636L;
			
			protected void populateItem(ListItem<Person> item) {
				item.add(new PersonItem("person", item.getModelObject()));
			}
			
		});
		
		add(new ListView<Sentence>("sentences", Sentence.getAllSentences(20)) {
			
			private static final long serialVersionUID = 3911519757128281636L;
			
			protected void populateItem(ListItem<Sentence> item) {
				item.add(new SentenceItem("sentence", item.getModelObject()));
			}
			
		});
		
	}

}
