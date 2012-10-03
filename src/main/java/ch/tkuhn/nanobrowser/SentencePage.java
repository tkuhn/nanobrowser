package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SentencePage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	private Sentence sentence;
	private ListModel<Person> agreerModel = new ListModel<Person>();

	public SentencePage(final PageParameters parameters) {
		
		sentence = new Sentence(parameters.get("uri").toString());
		
		update();
		
		add(new Label("title", sentence.getSentenceText()));
		
		add(new ExternalLink("uri", sentence.getURI(), sentence.getURI()));
		
		add(new ListView<Nanopub>("nanopubs", sentence.getNanopubs()) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject()));
			}
			
		});
		
		add(new Link<Object>("agree") {
			
			private static final long serialVersionUID = 8608371149183694875L;

			public void onClick() {
				(new Person("http://www.example.org/somebody")).publishAgreement(sentence);
				update();
			}
			
		});
		
		add(new ListView<Person>("persons", agreerModel) {
			
			private static final long serialVersionUID = 5235305068010085751L;
			
			protected void populateItem(ListItem<Person> item) {
				item.add(new PersonItem("person", item.getModelObject()));
			}
			
		});
		
	}
	
	private void update() {
		agreerModel.setObject(sentence.getAgreers());
	}

}
