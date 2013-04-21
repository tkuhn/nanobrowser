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
		if (obj instanceof ThingElement) {
			item.add(((ThingElement) obj).createGUIItem("item", ThingElement.LONG_GUI_ITEM));
		} else if (obj instanceof Triple<?,?>) {
			item.add(new TriplePanel("item", (Triple<?,?>) obj));
		} else {
			item.add(new Label("item", obj.toString()));
		}
	}

}
