package ch.tkuhn.nanobrowser;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AgentPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	private Agent agent;
	private ListModel<Nanopub> nanopubModel = new ListModel<Nanopub>();
	private ListModel<Opinion> opinionModel = new ListModel<Opinion>();
	
	public AgentPage(final PageParameters parameters) {
		
		agent = new Agent(parameters.get("uri").toString());
		boolean isBot = agent.isBot();
		
		update();
		
		add(new MenuBar("menubar"));
		
		WebMarkupContainer icon = new WebMarkupContainer("icon");
		if (isBot) {
			icon.add(new AttributeModifier("src", new Model<String>("icons/bot.svg")));
		}
		add(icon);
		
		add(new Label("title", agent.getName()));

		add(new ExternalLink("uri", agent.getURI(), agent.getTruncatedURI()));

		add(new HList("typelist", agent.getTypes(), "Types"));
		
		if (isBot) {
			add(new HList("commanderlist", agent.getCommanders(), "Commanders"));
		} else {
			add(new WebMarkupContainer("commanderlist"));
		}

		add(new VList("nanopublist", nanopubModel, "Nanopublications"));
		
		add(new ListView<Opinion>("opinions", opinionModel) {
			
			private static final long serialVersionUID = -4257147575068849793L;

			protected void populateItem(ListItem<Opinion> item) {
				item.add(new Label("opinion", Opinion.getVerbPhrase(item.getModelObject().getOpinionType(), true)));
				item.add(new SentenceItem("opinionsentence", item.getModelObject().getSentence()));
				item.add(new NanopubItem("opinionpub", item.getModelObject().getNanopub(), Thing.TINY_GUI_ITEM));
			}
			
		});
		
		Link<Object> thatsmeButton;
		add(thatsmeButton = new Link<Object>("thatsme") {
			
			private static final long serialVersionUID = 8608371149183694875L;

			public void onClick() {
				AgentPage.this.getNanobrowserApp().setUser(agent);
				update();
				setResponsePage(AgentPage.class, getPageParameters());
			}
			
		});
		thatsmeButton.setVisible(!isBot);
		
	}
	
	private void update() {
		nanopubModel.setObject(agent.getAuthoredNanopubs());
		opinionModel.setObject(agent.getOpinions(true));
	}

}
