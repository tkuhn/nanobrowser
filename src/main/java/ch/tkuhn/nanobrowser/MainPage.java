package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MainPage extends NanobrowserWebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	private ListModel<Nanopub> nanopubModel = new ListModel<Nanopub>();
	private ListModel<Agent> personModel = new ListModel<Agent>();
	private ListModel<Sentence> sentenceModel = new ListModel<Sentence>();

	public MainPage(final PageParameters parameters) {
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new ListView<Nanopub>("nanopubs", nanopubModel) {
			
			private static final long serialVersionUID = 1587686459411075758L;
			
			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject()));
			}
			
		});
		
		add(new ListView<Agent>("persons", personModel) {
			
			private static final long serialVersionUID = 3911519757128281636L;
			
			protected void populateItem(ListItem<Agent> item) {
				item.add(new AgentItem("person", item.getModelObject()));
			}
			
		});
		
		add(new ListView<Sentence>("sentences", sentenceModel) {
			
			private static final long serialVersionUID = 3911519757128281636L;
			
			protected void populateItem(ListItem<Sentence> item) {
				item.add(new SentenceItem("sentence", item.getModelObject()));
			}
			
		});
		
		add(new Link<Object>("deletemetapubs") {
			
			private static final long serialVersionUID = -3387170765807183435L;

			public void onClick() {
				Nanopub.deleteAllNanopubsWithProperty("npx:opinionOn");
				Nanopub.deleteAllNanopubsWithProperty("npx:hasSameMeaning");
				update();
			}
			
		});
		
	}
	
	private void update() {
		nanopubModel.setObject(Nanopub.getNonmetaNanopubs(20));
		personModel.setObject(Agent.getAllPersons(20));
		sentenceModel.setObject(Sentence.getAllSentences(20));
	}

}
