package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class HList extends Panel {

	private static final long serialVersionUID = -8506288478189670570L;

	public HList(String id, List<? extends Object> items, String title) {
		super(id);

		add(new Label("title", title));
		add(new Label("empty", items.size() == 0 ? "(unknown)" : ""));
		add(new ListView<Object>("items", items) {

			private static final long serialVersionUID = -6222434246491371652L;

			protected void populateItem(ListItem<Object> item) {
				Object obj = item.getModelObject();
				if (obj instanceof Thing) {
					item.add(((Thing) obj).createGUIItem("item"));
				} else {
					item.add(Thing.getThing(obj.toString()).createGUIItem("item"));
				}
			}
			
		});
	}

}
