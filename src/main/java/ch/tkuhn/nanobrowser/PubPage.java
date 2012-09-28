package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getAuthors;
import static ch.tkuhn.nanobrowser.NanopubAccess.getCreateDateString;
import static ch.tkuhn.nanobrowser.NanopubAccess.getFormulaAssertions;
import static ch.tkuhn.nanobrowser.NanopubAccess.getSentenceAssertions;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PubPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public PubPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", Utils.getLastPartOfURI(uri)));
		
		//add(new Label("uri", uri));

		add(new Label("created", getCreateDateString(uri)));
		
		add(new ListView<String>("sassertions", getSentenceAssertions(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new AssertionItem("sassertion", uri));
			}
			
		});
		
		add(new ListView<String>("fassertions", getFormulaAssertions(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new AssertionItem("fassertion", uri));
			}
			
		});
		
		add(new ListView<String>("authors", getAuthors(uri)) {

			private static final long serialVersionUID = 6872614881667929445L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new PersonItem("author", uri));
			}
			
		});
		
	}

}
