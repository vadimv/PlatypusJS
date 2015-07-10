package com.eas.client.form.grid.rows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ListDataProvider;

public class JsArrayListDataProvider extends ListDataProvider<JavaScriptObject> implements IndexOfProvider<JavaScriptObject>, JsDataContainer {

	protected JsObject data;
	protected HandlerRegistration boundToData;
	protected HandlerRegistration boundToDataElements;
	protected Map<JavaScriptObject, Integer> indicies;
	protected Runnable onResize;
	protected Runnable onChange;
	protected Callback<Void, String> onError;

	public JsArrayListDataProvider(Runnable aOnResize, Runnable aOnChange, Callback<Void, String> aOnError) {
		super();
		onResize = aOnResize;
		onChange = aOnChange;
		onError = aOnError;
	}

	@Override
	public JavaScriptObject getData() {
		return data;
	}

	protected boolean changesQueued;

	protected void enqueueChanges() {
		changesQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (changesQueued) {
					changesQueued = false;
					if (onChange != null)
						onChange.run();
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
					getList().clear();
					if (data != null) {
						getList().addAll(new JsArrayList(data));
						boundToDataElements = Utils.listenElements(data, new Utils.OnChangeHandler() {
							
							@Override
							public void onChange(JavaScriptObject anEvent) {
								enqueueChanges();
							}
						});
					}
					if (onResize != null)
						onResize.run();
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
			enqueueReadd();
		}
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			unbind();
			data = aValue != null ? aValue.<JsObject> cast() : null;
			bind();
		}
	}

	protected void invalidate() {
		indicies = null;
	}

	protected void validate() {
		if (indicies == null) {
			indicies = new HashMap<>();
			List<JavaScriptObject> targetList = getList();
			if (targetList != null) {
				for (int i = 0; i < targetList.size(); i++) {
					indicies.put(targetList.get(i), i);
				}
			}
		}
	}

	@Override
	public int indexOf(JavaScriptObject aItem) {
		validate();
		Integer idx = indicies.get(aItem);
		return idx != null ? idx.intValue() : -1;
	}

	@Override
	public void rescan() {
		invalidate();
		validate();
	}
}
