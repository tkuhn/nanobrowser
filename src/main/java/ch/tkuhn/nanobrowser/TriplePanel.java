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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class TriplePanel extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;
	
	private int settings;
	
	public static final int SHOW_SUBJECT = 0x01;
	public static final int SHOW_PREDICATE = 0x02;
	public static final int SHOW_OBJECT = 0x04;
	public static final int SHOW_FULLSTOP = 0x08;

	public static final int SUBJECT_PREDICATE = SHOW_SUBJECT | SHOW_PREDICATE | SHOW_FULLSTOP;
	public static final int PREDICATE_OBJECT = SHOW_PREDICATE | SHOW_OBJECT | SHOW_FULLSTOP;
	
	public static final int SHOW_ALL = SHOW_SUBJECT | SHOW_PREDICATE | SHOW_OBJECT | SHOW_FULLSTOP;
	
	public static final int START_WITH_PREDICATE = 0x10;

	public static final int PREDICATE_SUBJECT = SHOW_SUBJECT | SHOW_PREDICATE | START_WITH_PREDICATE;

	public TriplePanel(String id, Triple<?,?> triple, int settings) {
		super(id);
		
		this.settings = settings;
		
		if (isFlagSet(SHOW_SUBJECT)) {
			add(triple.getSubject().createGUIItem("subject"));
		} else {
			hide("subject");
		}

		if (isFlagSet(SHOW_PREDICATE)) {
			if (isFlagSet(START_WITH_PREDICATE)) {
				add(triple.getPredicate().createGUIItem("predicatefirst", ThingElement.PREDICATEFIRST_ITEM));
				hide("predicate");
			} else {
				add(triple.getPredicate().createGUIItem("predicate"));
				hide("predicatefirst");
			}
		} else {
			hide("predicatefirst");
			hide("predicate");
		}

		if (isFlagSet(SHOW_OBJECT)) {
			Object obj = triple.getObject();
			if (obj instanceof ThingElement) {
				add(((ThingElement) obj).createGUIItem("object"));
			} else {
				add(new Label("object", "\"" + obj.toString() + "\""));
			}
		} else {
			hide("object");
		}
		
		if (isFlagSet(SHOW_FULLSTOP)) {
			add(new Label("end", "."));
		} else {
			hide("end");
		}
		
		WebMarkupContainer ref = new WebMarkupContainer("reference");
		if (triple.getNanopub() == null) {
			ref.setVisible(false);
			ref.add(new Label("nanopub"));
		} else {
			ref.add(new NanopubItem("nanopub", triple.getNanopub(), ThingElement.TINY_GUI_ITEM));
		}
		add(ref);
	}

	public TriplePanel(String id, Triple<?,?> triple) {
		this(id, triple, SHOW_ALL);
	}
	
	private void hide(String id) {
		Label l = new Label(id);
		l.setVisible(false);
		add(l);
	}
	
	private boolean isFlagSet(int flag) {
		return (settings & flag) == flag;
	}

}
