package com.eas.client.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.rowset.Callback;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Utils;
import com.eas.client.form.MarginConstraints.Margin;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedColor;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedFont;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.MarginsPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ControlsUtils {

	public static final String DD = "DD";
	public static final String DD_MM = "DD_MM";
	public static final String DD_MM_YYYY = "DD_MM_YYYY";
	public static final String MM = "MM";
	public static final String MM_YYYY = "MM_YYYY";
	public static final String YYYY = "YYYY";
	public static final String MM_SS = "MM_SS";
	public static final String HH_MM = "HH_MM";
	public static final String HH_MM_SS = "HH_MM_SS";
	public static final String DD_MM_YYYY_HH_MM_SS = "DD_MM_YYYY_HH_MM_SS";
	public static final String CONTEXT_MENU = "contextMenu";

	public static String convertDateFormatString(String dateFormat) {
		if (DD.equals(dateFormat))
			return "dd";
		else if (MM.equals(dateFormat))
			return "MM";
		else if (MM_YYYY.equals(dateFormat))
			return "MM.yyyy";
		else if (YYYY.equals(dateFormat))
			return "yyyy";
		else if (DD_MM.equals(dateFormat))
			return "dd.MM";
		else if (DD_MM_YYYY.equals(dateFormat))
			return "dd.MM.yyyy";
		else if (MM_SS.equals(dateFormat))
			return "mm:ss";
		else if (HH_MM.equals(dateFormat))
			return "HH:mm";
		else if (HH_MM_SS.equals(dateFormat))
			return "HH:mm:ss";
		else if (DD_MM_YYYY_HH_MM_SS.equals(dateFormat))
			return "dd.MM.yyyy HH:mm:ss";
		else
			return dateFormat;
	}

	protected static class XFileUploadField extends FileUpload {

		public XFileUploadField() {
			super();
		}

		public void scheduleSelect() {
			getElement().<XElement> cast().click();
		}

		public native JsArray<JavaScriptObject> getFiles()/*-{
			var element = this.@com.google.gwt.user.client.ui.Widget::getElement()();
			return element.files;
		}-*/;
	}

	public static void jsSelectFile(final JavaScriptObject aCallback) {
		if (aCallback != null) {
			selectFile(new Callback<JavaScriptObject>() {
				@Override
				public void run(JavaScriptObject aResult) throws Exception {
					Utils.executeScriptEventVoid(aCallback, aCallback, aResult);
				}

				@Override
				public void cancel() {
				}
			});
		}
	}

	public static void selectFile(final Callback<JavaScriptObject> aCallback) {
		final XFileUploadField fu = new XFileUploadField();
		fu.getElement().getStyle().setDisplay(Style.Display.NONE);
		RootPanel.get().add(fu);
		fu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (fu.getFiles() != null) {
					for (int i = 0; i < fu.getFiles().length(); i++) {
						try {
							aCallback.run(fu.getFiles().get(i));
						} catch (Exception ex) {
							Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
				fu.removeFromParent();
			}
		});
		fu.scheduleSelect();
		/*
		 * Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		 * 
		 * @Override public void execute() { fu.removeFromParent(); } });
		 */
	}

	/*
	 * public static String getEmptyText(ValueBaseField<?> aField) { XElement
	 * iel = XElement.as(aField.getCell().getInputElement(aField.getElement()));
	 * return iel.getAttribute("placeholder"); }
	 * 
	 * public static void setEmptyText(ValueBaseField<?> aField, String aValue)
	 * { XElement iel =
	 * XElement.as(aField.getCell().getInputElement(aField.getElement()));
	 * setEmptyText(iel, aValue); }
	 */

	public static void setEmptyText(Element aElement, String aValue) {
		aElement.setAttribute("placeholder", aValue);
	}

	protected static RegExp rgbPattern = RegExp.compile("rgb *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *\\)");
	protected static RegExp rgbaPattern = RegExp.compile("rgba *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *, *([0-9]*\\.?[0-9]+) *\\)");

	public static PublishedColor parseColor(String aInput) {
		if (aInput != null && !aInput.isEmpty()) {
			if (aInput.startsWith("#")) {
				Integer intval = Integer.decode(aInput);
				int i = intval.intValue();
				return PublishedColor.create((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, 0xFF);
			} else {
				MatchResult m = rgbPattern.exec(aInput);
				if (m != null) {
					return PublishedColor.create(Integer.valueOf(m.getGroup(1)), // r
					        Integer.valueOf(m.getGroup(2)), // g
					        Integer.valueOf(m.getGroup(3)), // b
					        0xFF); // a
				} else {
					MatchResult m1 = rgbaPattern.exec(aInput);
					if (m1 != null) {
						return PublishedColor.create(Integer.valueOf(m1.getGroup(1)), // r
						        Integer.valueOf(m1.getGroup(2)), // g
						        Integer.valueOf(m1.getGroup(3)), // b
						        Math.round(Float.valueOf(m1.getGroup(3)) * 255)); // a
					}
				}
			}
		}
		return null;
	}

	public static PublishedColor getStyledForeground(XElement aElement) {
		String c = aElement.getStyle().getColor();
		return c != null ? parseColor(c) : null;
	}

	public static PublishedColor getStyledBackground(XElement aElement) {
		String c = aElement.getStyle().getBackgroundColor();
		return c != null ? parseColor(c) : null;
	}

	public static void renderDecorated(SafeHtmlBuilder rendered, PublishedStyle aStyle, SafeHtmlBuilder sb) {
		if (aStyle != null) {
			StyleIconDecorator.decorate(rendered.toSafeHtml(), aStyle, HasVerticalAlignment.ALIGN_MIDDLE, sb);
		} else
			sb.append(rendered.toSafeHtml());
	}

	public static Runnable createScriptSelector(final JavaScriptObject aThis, final JavaScriptObject selectFunction, final JavaScriptObject aPublishedField) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					Utils.executeScriptEventVoid(aThis, selectFunction, aPublishedField);
				} catch (Exception ex) {
					Logger.getLogger(ControlsUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		};
	}

	/**
	 * Calculates a published cell for standalone model-aware controls against
	 * row aRow.
	 * 
	 * @param aEventThis
	 * @param cellFunction
	 * @param aRow
	 * @param aModelElement
	 * @return
	 * @throws Exception
	 */
	public static PublishedCell calcStandalonePublishedCell(JavaScriptObject aEventThis, JavaScriptObject cellFunction, Row aRow, String aDisplay, ModelElementRef aModelElement,
	        PublishedCell aAlreadyCell) throws Exception {
		if (aEventThis != null && aModelElement != null && cellFunction != null) {
			if (aRow != null) {
				PublishedCell cell = aAlreadyCell != null ? aAlreadyCell : Publisher.publishCell(Utils.toJs(aRow.getColumnObject(aModelElement.getColIndex())), aDisplay);
				Object[] rowIds = aRow.getPKValues();
				if (rowIds != null) {
					for (int i = 0; i < rowIds.length; i++)
						rowIds[i] = Utils.toJs(rowIds[i]);
				}
				Boolean res = Utils.executeScriptEventBoolean(
				        aEventThis,
				        cellFunction,
				        JsEvents.publishOnRenderEvent(aEventThis, rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? Utils.toJsArray(rowIds) : rowIds[0]) : null, null,
				                Entity.publishRowFacade(aRow, aModelElement.entity), cell));
				if (res != null && res) {
					return cell;
				}
			}
		}
		return null;
	}

	protected interface ElementCallback {
		public void run(Element aElement);
	}

	protected static void walkChildren(XElement aRoot, ElementCallback aCallback) {
		NodeList<Element> children = aRoot.select("*");
		for (int i = 0; i < children.getLength(); i++) {
			Element el = children.getItem(i);
			aCallback.run(el);
		}
	}

	protected static void walkChildren(Element aRoot, Element aStop, ElementCallback aCallback) {
		NodeList<Node> children = aRoot.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node el = children.getItem(i);
			if (el instanceof Element) {
				aCallback.run((Element) el);
				if (el != aStop)
					walkChildren((Element) el, aStop, aCallback);
			}
		}
	}

	public static void applyBackground(Widget aWidget, final PublishedColor aColor) {
		applyBackground(aWidget, aColor != null ? aColor.toStyled() : null);
	}

	public static void applyBackground(Widget aWidget, final String aColorString) {
		Element aElement = aWidget.getElement();
		if (aElement != null && aElement.getStyle() != null) {
			if (aColorString != null && !aColorString.isEmpty()) {
				aElement.getStyle().setBackgroundColor(aColorString);
				aElement.getStyle().setBackgroundImage("none");
			} else {
				aElement.getStyle().clearBackgroundColor();
				aElement.getStyle().clearBackgroundImage();
			}
		}
	}

	public static void applyForeground(Widget aWidget, final PublishedColor aColor) {
		Element aElement = aWidget.getElement();
		if (aColor != null)
			aElement.getStyle().setColor(aColor.toStyled());
		else
			aElement.getStyle().clearColor();
	}

	public static void applyFont(Widget aWidget, final PublishedFont aFont) {
		Element aElement = aWidget.getElement();
		aElement.getStyle().setProperty("fontFamily", aFont != null ? aFont.getFamily() : "");
		if (aFont != null) {
			aElement.getStyle().setFontSize(aFont.getSize(), Style.Unit.PT);
			if (aFont.isBold())
				aElement.getStyle().setFontWeight(Style.FontWeight.BOLD);
			// aElement.getStyle().setFontWeight(aFont.isBold() ?
			// FontWeight.BOLD : FontWeight.NORMAL);
			if (aFont.isItalic())
				aElement.getStyle().setFontStyle(Style.FontStyle.ITALIC);
			// aElement.getStyle().setFontStyle(aFont.isItalic() ?
			// FontStyle.ITALIC : FontStyle.NORMAL);
		} else {
			aElement.getStyle().clearFontSize();
			aElement.getStyle().clearFontWeight();
			aElement.getStyle().clearFontStyle();
		}
	}

	public static void applyCursor(Widget aWidget, final String aCursor) {
		aWidget.getElement().getStyle().setProperty("cursor", aCursor != null ? aCursor : "");
	}

	public static void reapplyStyle(HasPublished aComponent) {
		if (aComponent instanceof Widget && aComponent.getPublished() != null) {
			PublishedComponent published = aComponent.getPublished().cast();
			if (published.isBackgroundSet())
				ControlsUtils.applyBackground((Widget) aComponent, published.getBackground());
			if (published.isForegroundSet())
				ControlsUtils.applyForeground((Widget) aComponent, published.getForeground());
			if (published.isFontSet())
				ControlsUtils.applyFont((Widget) aComponent, published.getFont());
			if (published.isCursorSet())
				ControlsUtils.applyCursor((Widget) aComponent, published.getCursor());
		}
	}
	
	public static JavaScriptObject lookupPublishedParent(Widget aWidget) {
		assert aWidget != null;
		Widget parent = aWidget;
		while (parent != null && !(parent instanceof HasPublished)) {
			parent = parent.getParent();
		}
		return parent != null ? ((HasPublished)parent).getPublished() : null;
	}

	public static void addWidgetTo(Widget aWidet, String aElementId) {
		addWidgetTo(aWidet, RootPanel.get(aElementId));
	}

	public static void addWidgetTo(Widget aWidet, Element aElement) {
		addWidgetTo(aWidet, new WrappingPanel(aElement));
	}

	public static void addWidgetTo(Widget aWidet, HasWidgets aContainer) {
		aWidet.setVisible(true);
		aContainer.clear();
		if (aContainer instanceof BorderPane) {
			((BorderPane) aContainer).add(aWidet);
		} else if (aContainer instanceof MarginsPane) {
			MarginConstraints mc = new MarginConstraints();
			mc.setTop(new Margin(0, Style.Unit.PX));
			mc.setBottom(new Margin(0, Style.Unit.PX));
			mc.setLeft(new Margin(0, Style.Unit.PX));
			mc.setRight(new Margin(0, Style.Unit.PX));
			((MarginsPane) aContainer).add(aWidet, mc);
		} else if (aContainer instanceof SplitPane) {
			((SplitPane) aContainer).setFirstWidget(aWidet);
		} else if (aContainer instanceof RootPanel) {
			aContainer.add(aWidet);
		} else {
			aContainer.add(aWidet);
		}
	}

}
