package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.link.Link;
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

		add(new VList("nanopublist", nanopubModel, "Latest Nanopublications"));

		add(new VList("personlist", personModel, "Some Persons"));

		add(new VList("sentencelist", sentenceModel, "Some Sentences"));
		
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
		nanopubModel.setObject(Nanopub.getNonmetaNanopubs(10));
		personModel.setObject(Agent.getAllPersons(10));
		sentenceModel.setObject(Sentence.getAllSentences(10));
	}

}
