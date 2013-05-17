// This file is part of Nanobrowser.
// Copyright 2012, Tobias Kuhn, http://www.tkuhn.ch
//
// Nanobrowser is free software: you can redistribute it and/or modify it under the terms of the
// GNU Lesser General Public License as published by the Free Software Foundation, either version
// 3 of the License, or (at your option) any later version.
//
// Nanobrowser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License along with Nanobrowser.
// If not, see http://www.gnu.org/licenses/.

package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MenuBar extends ThingItem {
	
	private static final long serialVersionUID = -3135904174404048609L;

	private TextField<String> searchTextField;

	public MenuBar(String id) {
		super(id);

		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("publish", PublishPage.class);
		add(link);
		
		PageParameters params = new PageParameters();
		params.add("uri", getNanobrowserApp().getUser().getURI());
		BookmarkablePageLink<WebPage> userLink = new BookmarkablePageLink<WebPage>("userlink", AgentPage.class, params);
		add(userLink);
		
		userLink.add(new Label("user", getNanobrowserApp().getUser().getName()));
		
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
