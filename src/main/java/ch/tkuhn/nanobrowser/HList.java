package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class HList extends Panel {

	private static final long serialVersionUID = -8506288478189670570L;

	public HList(String id, List<? extends Object> entities, String title) {
		super(id);

		add(new Label("title", title));
		add(new Label("empty", entities.size() == 0 ? "(unknown)" : ""));
		add(new ListView<Object>("entities", entities) {

			private static final long serialVersionUID = -6222434246491371652L;

			protected void populateItem(ListItem<Object> item) {
				Object obj = item.getModelObject();
				if (obj instanceof Thing) {
					item.add(((Thing) obj).createGUIItem("entity"));
				} else {
					item.add(Thing.getThing(obj.toString()).createGUIItem("entity"));
				}
			}
			
		});
	}

}
