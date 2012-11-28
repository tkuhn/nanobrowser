package ch.tkuhn.nanobrowser;

import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SearchPage extends NanobrowserWebPage {

	private static final long serialVersionUID = 6936478522592088856L;
	
	private String searchText;
	
	private ListModel<Sentence> sentenceModel = new ListModel<Sentence>();

	public SearchPage(final PageParameters parameters) {
		
		searchText = parameters.get("q").toString();
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new VList("sentencelist", sentenceModel, "Sentences"));
		
	}
	
	private void update() {
		sentenceModel.setObject(Sentence.search(searchText, 100));
	}

}
