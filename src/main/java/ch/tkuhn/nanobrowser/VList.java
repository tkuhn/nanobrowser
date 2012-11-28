package ch.tkuhn.nanobrowser;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class VList extends Panel {
	
	private static final long serialVersionUID = -5284668164342258059L;

	public VList(String id, List<? extends Object> items, String title) {
		super(id);

		add(new Label("title", title));

		add(new ListView<Object>("items", items) {

			private static final long serialVersionUID = 41253459622515487L;

			protected void populateItem(ListItem<Object> item) {
				VList.this.populateItem(item);
			}
			
		});
	}

	public VList(String id, IModel<? extends List<? extends Object>> items, String title) {
		super(id);

		add(new Label("title", title));

		add(new ListView<Object>("items", items) {

			private static final long serialVersionUID = 41253459622515487L;

			protected void populateItem(ListItem<Object> item) {
				VList.this.populateItem(item);
			}
			
		});
	}
	
	private void populateItem(ListItem<Object> item) {
		Object obj = item.getModelObject();
		if (obj instanceof Thing) {
			item.add(((Thing) obj).createGUIItem("item", Thing.LONG_GUI_ITEM));
		} else {
			item.add(new Label("item", obj.toString()));
		}
	}

}
