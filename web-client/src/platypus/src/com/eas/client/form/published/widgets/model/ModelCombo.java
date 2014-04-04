package com.eas.client.form.published.widgets.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.converters.RowRowValueConverter;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.combo.ValueLookup;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class ModelCombo extends PublishedDecoratorBox<Row> implements HasEmptyText {

	protected CrossUpdater updater = new CrossUpdater(new Runnable() {

		@Override
		public void run() {
			redraw();
		}

	});

	protected RowKeyProvider rowKeyProvider = new RowKeyProvider();
	protected String emptyText = "< >";
	protected ModelElementRef valueElement;
	protected ModelElementRef displayElement;
	protected StringRowValueConverter converter = new StringRowValueConverter();
	protected ValueLookup lookup;
	protected Map<Row, Integer> rowsLocator = new HashMap<>();
	protected String emptyValueKey = String.valueOf(IDGenerator.genId());

	protected boolean list = true;

	public ModelCombo() {
		super(new StyledListBox<Row>());
	}

	public void setValue(Row value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}

	protected void redraw() {
		try {
			if (decorated instanceof StyledListBox<?> && ((Widget) decorated).isAttached()) {
				Row oldValue = getValue();
				StyledListBox<Row> box = (StyledListBox<Row>) decorated;
				box.clear();
				box.setSelectedIndex(-1);
				rowsLocator.clear();
				if (valueElement != null && valueElement.entity != null && valueElement.entity.getRowset() != null && displayElement != null && displayElement.entity != null
				        && displayElement.entity.getRowset() != null) {
					Rowset valuesRowset = valueElement.entity.getRowset();
					Rowset displaysRowset = displayElement.entity.getRowset();
					Rowset bindedRowset = modelElement.entity.getRowset();
					if (bindedRowset != null) {
						Row bindedRow = bindedRowset.getCurrentRow();
						if (bindedRow != null) {
							Object bindedValue = bindedRow.getColumnObject(modelElement.getColIndex());
							box.addItem(emptyText, emptyValueKey, null, null);
							if (ModelCombo.this.list) {
								for (Row row : valuesRowset.getCurrent()) {
									Row displayRow = row;
									if (valuesRowset != displaysRowset) {
										valueElement.entity.scrollTo(row);
										displayRow = displaysRowset.getCurrentRow();
									}
									String label = displayRow != null ? converter.convert(displayRow.getColumnObject(displayElement.getColIndex())) : "";
									box.addItem(label, String.valueOf(row.getColumnObject(valueElement.getColIndex())), row, "");
									if(oldValue == row){
										box.setSelectedIndex(box.getItemCount() - 1);
									}
								}
							} else {
								Row row = lookup.lookupRow(bindedValue);
								Row displayRow = row;
								if (valuesRowset != displaysRowset) {
									valueElement.entity.scrollTo(row);
									displayRow = displaysRowset.getCurrentRow();
								}
								String label = displayRow != null ? converter.convert(displayRow.getColumnObject(displayElement.getColIndex())) : "";
								box.addItem(label, String.valueOf(row.getColumnObject(valueElement.getColIndex())), row, "");
								if(oldValue == row)
									box.setSelectedIndex(0);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ModelCombo.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}

	protected HasValue<Row> getDecorated() {
		return decorated;
	}

	@Override
	public String getEmptyText() {
		return emptyText;
	}

	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		ControlsUtils.applyEmptyText(getElement(), emptyText);
	}

	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelCombo aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "emptyText", {
		     get : function() {
		         return aWidget.@com.eas.client.form.published.HasEmptyText::getEmptyText()();
		     },
		     set : function(aValue) {
		         aWidget.@com.eas.client.form.published.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
		     }
		});
		Object.defineProperty(aPublished, "value", {
		     get : function() {
		         return $wnd.boxAsJs(aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getJsValue()());
		     },
		     set : function(aValue) {
		         if (aValue != null) {
		             aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
		         } else {
		             aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setJsValue(Ljava/lang/Object;)(null);
		         }
		     }
		});
		Object.defineProperty(aPublished, "valueField", {
		    get : function() {
		        return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getValueField()());
		    },
		    set : function(aValue) {
		        if (aValue != null)
		            aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
		        else
		            aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(null);
		    }
		});
		Object.defineProperty(aPublished, "displayField", {
		    get : function() {
		        return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getDisplayField()());
		    },
		    set : function(aValue) {
		        if (aValue != null)
		            aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
		        else
		            aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(null);
		    }
		});
		Object.defineProperty(aPublished, "list", {
		    get : function() {
		        return aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::isList()();
		    },
		    set : function(aValue) {
		        aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setList(Z)(false != aValue);
		    }
		});
    }-*/;

	public ModelElementRef getValueElement() {
		return valueElement;
	}

	public void setValueElement(ModelElementRef aValue) {
		if (valueElement != aValue) {
			if (lookup != null)
				lookup.die();
			if (valueElement != null)
				updater.remove(valueElement.entity);
			valueElement = aValue;
			if (valueElement != null)
				updater.add(valueElement.entity);
			lookup = new ValueLookup(valueElement);
		}
	}

	public ModelElementRef getDisplayElement() {
		return displayElement;
	}

	public void setDisplayElement(ModelElementRef aValue) {
		if (displayElement != aValue) {
			if (displayElement != null)
				updater.remove(displayElement.entity);
			displayElement = aValue;
			if (displayElement != null)
				updater.add(displayElement.entity);
		}
	}

	@Override
	public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new RowRowValueConverter());
	}

	public com.bearsoft.rowset.metadata.Field getValueField() throws Exception {
		ModelElementRef el = getValueElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setValueField(Field aField) throws Exception {
		setValueElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setValueElement(new ModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter)));
		}
		redraw();
	}

	public Field getDisplayField() throws Exception {
		ModelElementRef el = getDisplayElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setDisplayField(Field aField) throws Exception {
		setDisplayElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setDisplayElement(new ModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter)));
		}
		redraw();
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean aValue) {
		if (list != aValue) {
			list = aValue;
			// target.setReadOnly(!editable || selectOnly || !list);
			redraw();
		}
	}

	public Object lookupRowValue(Row aRow) throws Exception {
		return aRow.getColumnObject(valueElement.getColIndex());
	}

	public Object getJsValue() throws Exception {
		Row row = getValue();
		Object key = lookupRowValue(row);
		return Utils.toJs(key);
	}

	public void setJsValue(Object aValue) throws Exception {
		setJsValue(aValue, true);
	}

	public void setJsValue(Object aValue, boolean fireEvents) throws Exception {
		Object key = Utils.toJava(aValue);
		if (lookup != null) {
			setValue(lookup.lookupRow(key), fireEvents);
		}
	}
}
