package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openrdf.model.Statement;

public class NanopubPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;
	
	Nanopub pub;

	public NanopubPage(final PageParameters parameters) {
		
		pub = new Nanopub(parameters.get("uri").toString());
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", pub.getLastPartOfURI()));
		
		add(new ExternalLink("uri", pub.getURI(), pub.getTruncatedURI()));
		
		List<String> types = pub.getTypes();
		add(new Label("typesempty", types.size() == 0 ? "(unknown)" : ""));
		add(new ListView<String>("types", types) {
			
			private static final long serialVersionUID = 8592870259564915600L;

			protected void populateItem(ListItem<String> item) {
				item.add(Thing.getThing(item.getModelObject()).createGUIItem("type"));
			}
			
		});

		String dateString = pub.getCreateDateString();
		if (dateString == null) {
			add(new Label("dateempty", "(unknown)"));
			add(new Label("date", ""));
		} else {
			add(new Label("dateempty", ""));
			add(new Label("date", dateString));
		}
		
		List<Agent> authors = pub.getAuthors();
		add(new Label("authorsempty", authors.size() == 0 ? "(unknown)" : ""));
		add(new ListView<Agent>("authors", authors) {

			private static final long serialVersionUID = 6872614881667929445L;

			protected void populateItem(ListItem<Agent> item) {
				item.add(new AgentItem("author", item.getModelObject()));
			}
			
		});
		
		List<Agent> creators = pub.getCreators();
		add(new Label("creatorsempty", creators.size() == 0 ? "(unknown)" : ""));
		add(new ListView<Agent>("creators", creators) {
			
			private static final long serialVersionUID = 4928584915881911596L;

			protected void populateItem(ListItem<Agent> item) {
				item.add(new AgentItem("creator", item.getModelObject()));
			}
			
		});
		
		add(new ListView<Sentence>("sentences", pub.getSentenceAssertions()) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<Sentence> item) {
				item.add(new SentenceItem("sentence", item.getModelObject()));
			}
			
		});
		
		add(new ListView<Statement>("assertion", TripleStoreAccess.sortTriples(pub.getAssertionTriples())) {
			
			private static final long serialVersionUID = 4266539302092878158L;

			protected void populateItem(ListItem<Statement> item) {
				Statement st = item.getModelObject();
				item.add(new TriplePanel("assertiontriple", st));
			}
			
		});
		
		add(new ListView<Statement>("supporting", TripleStoreAccess.sortTriples(pub.getSupportingTriples())) {
			
			private static final long serialVersionUID = -809372636947729189L;

			protected void populateItem(ListItem<Statement> item) {
				Statement st = item.getModelObject();
				item.add(new TriplePanel("supportingtriple", st));
			}
			
		});
		
		add(new ListView<Opinion>("opinions", pub.getOpinions(true)) {
			
			private static final long serialVersionUID = 6804591967140101102L;

			protected void populateItem(ListItem<Opinion> item) {
				item.add(new AgentItem("opinionagent", item.getModelObject().getAgent()));
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), false) + "."));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), false));
			}
			
		});
		
		add(new Link<Object>("delete") {
			
			private static final long serialVersionUID = 8608371149183694875L;

			public void onClick() {
				pub.delete();
				setResponsePage(MainPage.class);
			}
			
		});
		
	}

}
