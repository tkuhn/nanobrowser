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

import java.util.List;

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
	private static List<NanopubElement> nanopubList = null;
	private ListModel<AgentElement> personModel = new ListModel<AgentElement>();
	private static List<AgentElement> personList = null;
	private ListModel<SentenceElement> sentenceModel = new ListModel<SentenceElement>();
	private static List<SentenceElement> sentenceList = null;

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
		if (nanopubList == null) {
			nanopubList = NanopubElement.getNonmetaNanopubs(10);
		}
		nanopubModel.setObject(nanopubList);
		if (personList == null) {
			personList = AgentElement.getAllPersons(10);
		}
		personModel.setObject(personList);
		if (sentenceList == null) {
			sentenceList = SentenceElement.getAllSentences(10);
		}
		sentenceModel.setObject(sentenceList);
	}

	public static void addToList(ThingElement el) {
		if (el instanceof NanopubElement) {
			nanopubList.add(0, (NanopubElement) el);
			while (nanopubList.size() > 10) {
				nanopubList.remove(10);
			}
		} else if (el instanceof AgentElement) {
			AgentElement agent = (AgentElement) el;
			if (personList.contains(agent)) return;
			if (agent.isBot()) return;
			personList.add(0, agent);
			while (personList.size() > 10) {
				personList.remove(10);
			}
		} else if (el instanceof SentenceElement) {
			SentenceElement sentence = (SentenceElement) el;
			if (sentenceList.contains(sentence)) return;
			sentenceList.add(0, sentence);
			while (sentenceList.size() > 10) {
				sentenceList.remove(10);
			}
		}
	}

	public static void resetLists() {
		nanopubList = null;
		personList = null;
		sentenceList = null;
	}

}
