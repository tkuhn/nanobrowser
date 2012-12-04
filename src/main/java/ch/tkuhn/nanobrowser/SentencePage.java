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
			SentenceRelation.HAS_CONFLICTING_MEANING
	});

	private Sentence sentence;
	private ListModel<Opinion> opinionModel = new ListModel<Opinion>();
	private ListModel<Triple<Sentence,Sentence>> relationModel = new ListModel<Triple<Sentence,Sentence>>();
	private TextField<String> otherSentenceField;
	private DropDownChoice<SentenceRelation> sentenceRelChoice;
	private SentenceRelation selectedRelType = SentenceRelation.IS_IMPROVED_VERSION_OF;

	public SentencePage(final PageParameters parameters) {
		
		sentence = new Sentence(parameters.get("uri").toString());
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", sentence.getSentenceText()));
		
		add(new ExternalLink("uri", sentence.getURI(), sentence.getTruncatedURI()));
		
		add(new ListView<Nanopub>("nanopubs", sentence.getNanopubs()) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject(), Thing.LONG_GUI_ITEM));
			}
			
		});
		
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
		
		add(new ListView<Opinion>("opinions", opinionModel) {
			
			private static final long serialVersionUID = 5235305068010085751L;
			
			protected void populateItem(ListItem<Opinion> item) {
				item.add(new AgentItem("agent", item.getModelObject().getAgent()));
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), false) + "."));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), Thing.TINY_GUI_ITEM));
			}
			
		});
		
		add(new ListView<Triple<Sentence,Sentence>>("relations", relationModel) {
			
			private static final long serialVersionUID = -3149020273243388808L;

			protected void populateItem(ListItem<Triple<Sentence,Sentence>> item) {
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
				Sentence other = null;
				if (s != null) {
					if (Sentence.isSentenceURI(s)) {
						other = new Sentence(s);
					} else if (Sentence.isSentenceText(s)) {
						other = Sentence.withText(s);
					}
				}
				if (other != null) {
					sentence.publishSentenceRelation(selectedRelType, other, getUser());
				} else {
					// TODO
				}
				setResponsePage(SentencePage.class, getPageParameters());
			}
			
		};
		
		add(newSentRelForm);
		newSentRelForm.add(otherSentenceField);
		newSentRelForm.add(sentenceRelChoice);
		
	}
	
	private void update() {
		opinionModel.setObject(sentence.getOpinions(true));
		relationModel.setObject(sentence.getRelatedSentences());
	}

}
