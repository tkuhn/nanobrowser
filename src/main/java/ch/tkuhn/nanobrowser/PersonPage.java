package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PersonPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	private Person person;
	private ListModel<Nanopub> nanopubModel = new ListModel<Nanopub>();
	
	public PersonPage(final PageParameters parameters) {
		
		person = new Person(parameters.get("uri").toString());
		
		update();
		
		add(new Label("title", person.getName()));

		add(new ExternalLink("uri", person.getURI(), person.getURI()));
		
		add(new Link<Object>("thatsme") {
			
			private static final long serialVersionUID = 8608371149183694875L;

			public void onClick() {
				PersonPage.this.getNanobrowserApp().setUser(person);
				update();
			}
			
		});
		
		add(new ListView<Nanopub>("nanopubs", nanopubModel) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject()));
			}
			
		});
		
	}
	
	private void update() {
		nanopubModel.setObject(person.getAuthoredNanopubs());
	}

}
