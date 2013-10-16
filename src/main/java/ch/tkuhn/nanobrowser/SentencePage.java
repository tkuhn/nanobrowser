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

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SentencePage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	private static List<SentenceRelation> sentenceRelOptions = Arrays.asList(new SentenceRelation[] {
			SentenceRelation.IS_IMPROVED_VERSION_OF,
			SentenceRelation.HAS_RELATED_MEANING,
			SentenceRelation.HAS_SAME_MEANING,
			SentenceRelation.HAS_OPPOSITE_MEANING,
			SentenceRelation.HAS_CONFLICTING_MEANING,
			SentenceRelation.HAS_MORE_GENERAL_MEANING_THAN,
			SentenceRelation.HAS_MORE_SPECIFIC_MEANING_THAN
	});

	private SentenceElement sentence;
	private ListModel<Opinion> opinionModel = new ListModel<Opinion>();
	private Model<String> opinionsEmptyModel = new Model<String>();
	private ListModel<Triple<SentenceElement,SentenceElement>> relationModel = new ListModel<Triple<SentenceElement,SentenceElement>>();
	private Model<String> relationsEmptyModel = new Model<String>();
	private TextField<String> otherSentenceField;
	private Model<String> sentenceError;
	private DropDownChoice<SentenceRelation> sentenceRelChoice;
	private SentenceRelation selectedRelType = SentenceRelation.IS_IMPROVED_VERSION_OF;

	public SentencePage(final PageParameters parameters) {
		
		sentence = new SentenceElement(parameters.get("uri").toString());
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", sentence.getSentenceText()));
		
		add(new ExternalLink("uri", sentence.getURI(), sentence.getTruncatedURI()));

		add(new VList("nanopublist", sentence.getNanopubs(), "Nanopublications"));
		
		add(new Link<Object>("agree") {
			
			private static final long serialVersionUID = 8608371149183694875L;

			public void onClick() {
				(new Opinion(getUser(), Opinion.AGREEMENT_TYPE, sentence)).publish();
				update();
			}
			
		});
		
		add(new Link<Object>("disagree") {
			
			private static final long serialVersionUID = 6155018832205809659L;

			public void onClick() {
				(new Opinion(getUser(), Opinion.DISAGREEMENT_TYPE, sentence)).publish();
				update();
			}
			
		});
		
		add(new Link<Object>("noopinion") {

			private static final long serialVersionUID = -731806526201590205L;

			public void onClick() {
				(new Opinion(getUser(), Opinion.NULL_TYPE, sentence)).publish();
				update();
			}
			
		});

		add(new Label("emptyopinions", opinionsEmptyModel));
		
		add(new ListView<Opinion>("opinions", opinionModel) {
			
			private static final long serialVersionUID = 5235305068010085751L;
			
			protected void populateItem(ListItem<Opinion> item) {
				item.add(new AgentItem("agent", item.getModelObject().getAgent()));
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), false) + "."));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), ThingElement.TINY_GUI_ITEM));
			}
			
		});

		add(new Label("emptyrelations", relationsEmptyModel));
		
		add(new ListView<Triple<SentenceElement,SentenceElement>>("relations", relationModel) {
			
			private static final long serialVersionUID = -3149020273243388808L;

			protected void populateItem(ListItem<Triple<SentenceElement,SentenceElement>> item) {
				item.add(new TriplePanel("relation", item.getModelObject(), TriplePanel.PREDICATE_SUBJECT));
			}
			
		});
		
		otherSentenceField = new TextField<String>("othersentence", Model.of(""));
		sentenceRelChoice = new DropDownChoice<SentenceRelation>("reltype",
				new PropertyModel<SentenceRelation>(this, "selectedRelType"), sentenceRelOptions);
		
		Form<?> newSentRelForm = new Form<Void>("newrelform") {
			
			private static final long serialVersionUID = -6636881419461562970L;

			protected void onSubmit() {
				String s = otherSentenceField.getModelObject();
				SentenceElement other = null;
				if (s != null && SentenceElement.isSentenceURI(s)) {
					other = new SentenceElement(s);
				} else {
					try {
						other = SentenceElement.withText(s);
					} catch (AidaException ex) {
						sentenceError.setObject("ERROR: " + ex.getMessage());
						return;
					}
				}
				sentence.publishSentenceRelation(selectedRelType, other, getUser());
				setResponsePage(SentencePage.class, getPageParameters());
			}
			
		};
		
		add(newSentRelForm);
		newSentRelForm.add(otherSentenceField);
		newSentRelForm.add(sentenceRelChoice);
		sentenceError = Model.of("");
		newSentRelForm.add(new Label("sentenceerror", sentenceError));
		
	}
	
	private void update() {
		opinionModel.setObject(sentence.getOpinions(true));
		opinionsEmptyModel.setObject(opinionModel.getObject().isEmpty() ? "(nothing)" : "");
		relationModel.setObject(sentence.getRelatedSentences());
		relationsEmptyModel.setObject(relationModel.getObject().isEmpty() ? "(nothing)" : "");
	}

}
