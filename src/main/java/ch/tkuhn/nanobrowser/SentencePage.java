package ch.tkuhn.nanobrowser;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SentencePage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	private Sentence sentence;
	private ListModel<Opinion> opinionModel = new ListModel<Opinion>();
	private ListModel<Pair<Sentence,Nanopub>> sameMeaningsModel = new ListModel<Pair<Sentence,Nanopub>>();
	private TextField<String> newSameMeaningField;

	public SentencePage(final PageParameters parameters) {
		
		sentence = new Sentence(parameters.get("uri").toString());
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", sentence.getSentenceText()));
		
		add(new ExternalLink("uri", sentence.getURI(), sentence.getTruncatedURI()));
		
		add(new ListView<Nanopub>("nanopubs", sentence.getNanopubs()) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Nanopub> item) {
				item.add(new NanopubItem("nanopub", item.getModelObject()));
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
				item.add(new PersonItem("person", item.getModelObject().getPerson()));
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), false) + "."));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), false));
			}
			
		});
		
		add(new ListView<Pair<Sentence,Nanopub>>("samemeanings", sameMeaningsModel) {
			
			private static final long serialVersionUID = 1L;

			protected void populateItem(ListItem<Pair<Sentence,Nanopub>> item) {
				item.add(new SentenceItem("samemeaning", item.getModelObject().getLeft()));
				item.add(new NanopubItem("samemeaningpub", item.getModelObject().getRight(), false));
			}
			
		});
		
		newSameMeaningField = new TextField<String>("newsamemeaning", Model.of(""));
		
		Form<?> newSameMeaningForm = new Form<Void>("newsamemeaningform") {
			
			private static final long serialVersionUID = -6636881419461562970L;

			protected void onSubmit() {
				String s = newSameMeaningField.getModelObject();
				Sentence other = null;
				if (s != null) {
					if (Sentence.isSentenceURI(s)) {
						other = new Sentence(s);
					} else if (Sentence.isSentenceText(s)) {
						other = Sentence.withText(s);
					}
				}
				if (other != null) {
					sentence.publishSameMeaning(other, getUser());
				} else {
					// TODO
				}
				setResponsePage(SentencePage.class, getPageParameters());
			}
			
		};
		
		add(newSameMeaningForm);
		newSameMeaningForm.add(newSameMeaningField);
		
	}
	
	private void update() {
		opinionModel.setObject(sentence.getOpinions(true));
		sameMeaningsModel.setObject(sentence.getSameMeaningSentences());
	}

}
