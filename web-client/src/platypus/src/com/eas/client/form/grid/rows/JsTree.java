package com.eas.client.form.grid.rows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.processing.TreeAdapter;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;

public class JsTree extends TreeAdapter<JavaScriptObject> implements JsDataContainer {

	protected JavaScriptObject data;
	protected HandlerRegistration boundToData;
	protected HandlerRegistration boundToDataElements;
	protected String parentField;
	protected String childrenField;

	public JsTree(String aParentField, String aChildrenField) {
		super();
		parentField = aParentField;
		childrenField = aChildrenField;
	}

	protected boolean changesQueued;

	protected void enqueueChanges() {
		changesQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (changesQueued) {
					changesQueued = false;
					if (data != null) {
						List<JavaScriptObject> items = new JsArrayList(data);
						for (int i = 0; i < items.size(); i++) {
							JavaScriptObject item = items.get(i);
							changed(item);
						}
					}
				}
			}
		});
	}

	protected boolean readdQueued;

	private void enqueueReadd() {
		readdQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (readdQueued) {
					readdQueued = false;
					if (boundToDataElements != null) {
						boundToDataElements.removeHandler();
						boundToDataElements = null;
					}
					if (data != null) {
						boundToDataElements = Utils.listenElements(data, new Utils.OnChangeHandler() {
							
							@Override
							public void onChange(JavaScriptObject anEvent) {
								enqueueChanges();
							}
						});
					}
					everythingChanged();
				}
			}
		});
	}
	
	protected void bind() {
		if (data != null) {
			boundToData = Utils.listenPath(data, "length", new Utils.OnChangeHandler() {
				
				@Override
				public void onChange(JavaScriptObject anEvent) {
					enqueueReadd();
				}
			});
			enqueueReadd();
		}
	}

	protected void unbind() {
		if (boundToData != null) {
			boundToData.removeHandler();
			boundToData = null;
		}
		enqueueReadd();
	}

	@Override
	public JavaScriptObject getData() {
		return data;
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			unbind();
			data = aValue;
			bind();
		}
	}

	protected boolean hasRowChildren(JavaScriptObject parent) {
		List<JavaScriptObject> children = findChildren(parent);
		return children != null && !children.isEmpty();
	}

	protected List<JavaScriptObject> findChildren(JavaScriptObject aParent) {
		if (aParent == null) {
			if (data != null) {
				List<JavaScriptObject> items = new JsArrayList(data);
				List<JavaScriptObject> roots = new ArrayList<>();
				for (int i = 0; i < items.size(); i++) {
					JavaScriptObject item = items.get(i);
					if (item != null && item.<JsObject> cast().getJs(parentField) == null) {
						roots.add(item);
					}
				}
				return roots;
			} else {
				return null;
			}
		} else {
			JavaScriptObject children = aParent.<JsObject> cast().getJs(childrenField);
			return children != null ? new JsArrayList(children) : null;
		}
	}

	// Tree structure of a rowset
	@Override
	public JavaScriptObject getParentOf(JavaScriptObject anElement) {
		return anElement.<JsObject> cast().getJs(parentField);
	}

	@Override
	public List<JavaScriptObject> getChildrenOf(JavaScriptObject anElement) {
		List<JavaScriptObject> found = findChildren(anElement);
		return found != null ? found : Collections.<JavaScriptObject> emptyList();
	}

	@Override
	public boolean isLeaf(JavaScriptObject anElement) {
		return !hasRowChildren(anElement);
	}

	// Tree mutation methods
	@Override
	public void add(JavaScriptObject aParent, JavaScriptObject anElement) {
	}

	@Override
	public void add(int aIndex, JavaScriptObject aParent, JavaScriptObject anElement) {
	}

	@Override
	public void addAfter(JavaScriptObject afterElement, JavaScriptObject anElement) {
	}

	@Override
	public void remove(JavaScriptObject anElement) {
	}
}
