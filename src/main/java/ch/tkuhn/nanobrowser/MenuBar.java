package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MenuBar extends ThingItem {
	
	private static final long serialVersionUID = -3135904174404048609L;

	private TextField<String> searchTextField;

	public MenuBar(String id) {
		super(id);
		
		add(new AgentItem("user", getNanobrowserApp().getUser()));
		
		searchTextField = new TextField<String>("searchtextfield", Model.of(""));
		
		Form<?> searchForm = new Form<Void>("searchform") {

			private static final long serialVersionUID = 4204397843169014882L;

			protected void onSubmit() {
				String s = searchTextField.getModelObject();
				if (s == null) s = "";
				PageParameters params = new PageParameters();
				params.add("q", s);
				setResponsePage(SearchPage.class, params);
			}
			
		};
		
		add(searchForm);
		searchForm.add(searchTextField);
	}

}
