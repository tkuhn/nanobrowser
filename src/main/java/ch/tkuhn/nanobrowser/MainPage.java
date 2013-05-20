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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MainPage extends NanobrowserWebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	private ListModel<NanopubElement> nanopubModel = new ListModel<NanopubElement>();
	private ListModel<AgentElement> personModel = new ListModel<AgentElement>();
	private ListModel<SentenceElement> sentenceModel = new ListModel<SentenceElement>();

	public MainPage(final PageParameters parameters) {
		
		update();
		
		add(new MenuBar("menubar"));

		add(new VList("nanopublist", nanopubModel, "Latest Nanopublications"));

		add(new VList("personlist", personModel, "Some Persons"));

		add(new VList("sentencelist", sentenceModel, "Some Sentences"));

		WebMarkupContainer aa = new WebMarkupContainer("adminactions");
		if (NanobrowserApplication.isInDevelopmentMode()) {
			aa.add(new Link<Object>("deletemetapubs") {
				
				private static final long serialVersionUID = -3387170765807183435L;

				public void onClick() {
					NanopubElement.deleteAllNanopubsWithProperty("npx:opinionOn");
					NanopubElement.deleteAllNanopubsWithProperty("npx:hasSameMeaning");
					update();
				}
				
			});
		} else {
			aa.add(new AttributeModifier("class", new Model<String>("hidden")));
			aa.add(new Label("deletemetapubs", ""));
		}
		add(aa);
		
	}
	
	private void update() {
		nanopubModel.setObject(NanopubElement.getNonmetaNanopubs(10));
		personModel.setObject(AgentElement.getAllPersons(10));
		sentenceModel.setObject(SentenceElement.getAllSentences(10));
	}

}
