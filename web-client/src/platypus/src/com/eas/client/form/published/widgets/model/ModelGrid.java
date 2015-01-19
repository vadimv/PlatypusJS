package com.eas.client.form.published.widgets.model;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.gwt.ui.widgets.grid.GridSection;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedHeaderOrFooterBuilder;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderAnalyzer;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderSplitter;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider;
import com.bearsoft.rowset.Utils.JsObject;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.RenderedTableCellBuilder;
import com.eas.client.form.grid.CursorPropertySelectionReflector;
import com.eas.client.form.grid.columns.ModelColumn;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasJsName;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SetSelectionModel;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<JavaScriptObject> implements HasJsFacade, HasOnRender, HasComponentPopupMenu, HasEventsExecutor, HasEnabled, HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	//
	public static final int SERVICE_COLUMN_WIDTH = 22;

	protected class ServiceColumnRerenderer {
		/*
		 * @Override public void rowsetScrolled(RowsetScrollEvent event) { if
		 * (getDataColumnCount() > 0 && getDataColumn(0) instanceof
		 * UsualServiceColumn) { if (frozenColumns > 0) {
		 * frozenLeft.redrawAllRowsInColumn(0, ModelGrid.this.dataProvider);
		 * scrollableLeft.redrawAllRowsInColumn(0, ModelGrid.this.dataProvider);
		 * } else { frozenRight.redrawAllRowsInColumn(0,
		 * ModelGrid.this.dataProvider);
		 * scrollableRight.redrawAllRowsInColumn(0,
		 * ModelGrid.this.dataProvider); } } }
		 */
	}

	protected boolean enabled = true;
	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	//
	protected String parentField;
	protected String childrenField;
	//
	protected JavaScriptObject data;
	protected ServiceColumnRerenderer markerRerenderer = new ServiceColumnRerenderer();
	protected JavaScriptObject onRender;
	protected PublishedComponent published;
	protected FindWindow finder;
	protected String groupName = "group-name-" + Document.get().createUniqueId();
	protected List<HeaderNode<JavaScriptObject>> header = new ArrayList<>();
	// runtime
	protected ListHandler<JavaScriptObject> sortHandler;
	protected HandlerRegistration sortHandlerReg;
	protected HandlerRegistration positionSelectionHandler;
	protected boolean editable;
	protected boolean deletable;
	protected boolean insertable;

	public ModelGrid() {
		super(new RowKeyProvider());
		addDomHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_DELETE && deletable) {
					if (getSelectionModel() instanceof SetSelectionModel<?>) {
						SetSelectionModel<JavaScriptObject> rowSelection = (SetSelectionModel<JavaScriptObject>) getSelectionModel();
					}
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_INSERT && insertable) {
					/*
					 * JavaScriptObject iserted = null; if (inserted != null &&
					 * getSelectionModel() instanceof SetSelectionModel<?>) {
					 * SetSelectionModel<JavaScriptObject> rowSelection =
					 * (SetSelectionModel<JavaScriptObject>)
					 * getSelectionModel(); rowSelection.clear();
					 * rowSelection.setSelected(inserted, true); }
					 */
				}
			}

		}, KeyUpEvent.getType());
		addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if ((event.isMetaKeyDown() || event.isControlKeyDown()) && event.getNativeKeyCode() == KeyCodes.KEY_F) {
					event.stopPropagation();
					event.preventDefault();
					ModelGrid.this.find();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_F3) {
					event.stopPropagation();
					event.preventDefault();
					if (finder != null) {
						finder.findNext();
					}
				}
			}
		}, KeyDownEvent.getType());
	}

	protected void installCellBuilders() {
		for (GridSection<?> section : new GridSection<?>[] { frozenLeft, frozenRight, scrollableLeft, scrollableRight }) {
			GridSection<JavaScriptObject> gSection = (GridSection<JavaScriptObject>) section;
			gSection.setTableBuilder(new RenderedTableCellBuilder<>(gSection, dynamicTDClassName, dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName));
		}
	}

	public JavaScriptObject getData() {
		return data;
	}

	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			data = aValue;
		}
	}

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String aValue) {
		if (parentField == null ? aValue != null : !parentField.equals(aValue)) {
			boolean wasTree = isTreeConfigured();
			parentField = aValue;
			boolean isTree = isTreeConfigured();
			if (wasTree != isTree) {
				// TODO: apply rows configuration
			}
		}
	}

	public String getChildrenField() {
		return childrenField;
	}

	public void setChildrenField(String aValue) {
		if (childrenField == null ? aValue != null : !childrenField.equals(aValue)) {
			boolean wasTree = isTreeConfigured();
			childrenField = aValue;
			boolean isTree = isTreeConfigured();
			if (wasTree != isTree) {
				// TODO: apply rows configuration
			}
		}
	}

	public final boolean isTreeConfigured() {
		return parentField != null && !parentField.isEmpty() && childrenField != null && !childrenField.isEmpty();
	}

	public ListHandler<JavaScriptObject> getSortHandler() {
		return sortHandler;
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		boolean oldValue = enabled;
		enabled = aValue;
		if (!oldValue && enabled) {
			getElement().<XElement> cast().unmask();
		} else if (oldValue && !enabled) {
			getElement().<XElement> cast().disabledMask();
		}
	}

	@Override
	public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu;
	}

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {

					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public List<HeaderNode<JavaScriptObject>> getHeader() {
		return header;
	}

	public void setHeader(List<HeaderNode<JavaScriptObject>> aHeader) {
		if (header != aHeader) {
			header = aHeader;
			if (autoRefreshHeader) {
				applyColumns();
			}
		}
	}

	/*
	 * Indicates that subsequent changes will take no effect in general columns
	 * collection and header. They will affect only underlying grid sections
	 */
	protected boolean columnsAjusting;

	public boolean isColumnsAjusting() {
		return columnsAjusting;
	}

	public void setColumnsAjusting(boolean aValue) {
		columnsAjusting = aValue;
	}

	@Override
	protected void refreshColumns() {
		// no op since hierarchical header processing
	}

	protected ModelColumn treeIndicatorColumn;

	@Override
	public void addColumn(int aIndex, Column<JavaScriptObject, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		super.addColumn(aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
		if (treeIndicatorColumn == null) {
			int treeIndicatorIndex = 0;
			if (treeIndicatorIndex < getDataColumnCount()) {
				Column<JavaScriptObject, ?> indicatorColumn = getDataColumn(treeIndicatorIndex);
				if (indicatorColumn instanceof ModelColumn) {
					treeIndicatorColumn = (ModelColumn) indicatorColumn;
					if (dataProvider instanceof TreeDataProvider<?> && treeIndicatorColumn.getCell() instanceof TreeExpandableCell<?, ?>) {
						TreeExpandableCell<JavaScriptObject, ?> treeCell = (TreeExpandableCell<JavaScriptObject, ?>) treeIndicatorColumn.getCell();
						treeCell.setDataProvider((TreeDataProvider<JavaScriptObject>) dataProvider);
					}
				}
			}
		}
	}

	@Override
	public void removeColumn(int aIndex) {
		Column<JavaScriptObject, ?> toDel = getDataColumn(aIndex);
		ModelColumn mCol = (ModelColumn) toDel;
		if (mCol == treeIndicatorColumn) {
			TreeExpandableCell<JavaScriptObject, ?> treeCell = (TreeExpandableCell<JavaScriptObject, ?>) mCol.getCell();
			if (treeCell.getDataProvider() != null) {
				treeCell.setDataProvider(null);
			}
			treeIndicatorColumn = null;
		}
		super.removeColumn(aIndex);
	}

	@Override
	public void setColumnWidthFromHeaderDrag(Column<JavaScriptObject, ?> aColumn, double aWidth, Unit aUnit) {
		ModelColumn modelCol = (ModelColumn) aColumn;
		if (aWidth >= modelCol.getMinWidth() && aWidth <= modelCol.getMaxWidth()) {
			super.setColumnWidth(aColumn, aWidth, aUnit);
			modelCol.setWidth(aWidth);
		}
	}

	@Override
	public void setColumnWidth(Column<JavaScriptObject, ?> aColumn, double aWidth, Unit aUnit) {
		super.setColumnWidth(aColumn, aWidth, aUnit);
		ModelColumn colFacade = (ModelColumn) aColumn;
		colFacade.updateWidth(aWidth);
	}

	@Override
	public void showColumn(Column<JavaScriptObject, ?> aColumn) {
		super.showColumn(aColumn);
		ModelColumn colFacade = (ModelColumn) aColumn;
		colFacade.updateVisible(true);
	}

	public void hideColumn(Column<JavaScriptObject, ?> aColumn) {
		super.hideColumn(aColumn);
		ModelColumn colFacade = (ModelColumn) aColumn;
		colFacade.updateVisible(false);
	}

	@Override
	public void setFrozenColumns(int aValue) {
		if (aValue >= 0 && frozenColumns != aValue) {
			if (aValue >= 0) {
				frozenColumns = aValue;
				if (autoRefreshHeader && getDataColumnCount() > 0 && aValue <= getDataColumnCount()) {
					applyColumns();
				}
			}
		}
	}

	protected boolean autoRefreshHeader = true;

	public boolean isAutoRefreshHeader() {
		return autoRefreshHeader;
	}

	public void setAutoRefreshHeader(boolean aValue) {
		autoRefreshHeader = aValue;
	}

	public void applyColumns() {
		List<HeaderNode<JavaScriptObject>> leaves = new ArrayList<>();
		HeaderAnalyzer.achieveLeaves(header, leaves);
		for (HeaderNode<JavaScriptObject> leaf : leaves) {
			Header<String> header = leaf.getHeader();
			ModelColumn column = (ModelColumn) leaf.getColumn();
			addColumn(column, column.getWidth() + "px", header, null, !column.isVisible());
		}
		ThemedHeaderOrFooterBuilder<JavaScriptObject> leftBuilder = (ThemedHeaderOrFooterBuilder<JavaScriptObject>) headerLeft.getHeaderBuilder();
		ThemedHeaderOrFooterBuilder<JavaScriptObject> rightBuilder = (ThemedHeaderOrFooterBuilder<JavaScriptObject>) headerRight.getHeaderBuilder();
		List<HeaderNode<JavaScriptObject>> leftHeader = HeaderSplitter.split(header, 0, frozenColumns);
		leftBuilder.setHeaderNodes(leftHeader);
		List<HeaderNode<JavaScriptObject>> rightHeader = HeaderSplitter.split(header, frozenColumns, getDataColumnCount());
		rightBuilder.setHeaderNodes(rightHeader);
		redrawHeaders();
	}

	@Override
	public void setSelectionModel(SelectionModel<JavaScriptObject> aValue) {
		assert aValue != null : "Selection model can't be null.";
		SelectionModel<? super JavaScriptObject> oldValue = getSelectionModel();
		if (aValue != oldValue) {
			if (positionSelectionHandler != null)
				positionSelectionHandler.removeHandler();
			super.setSelectionModel(aValue);
			positionSelectionHandler = aValue.addSelectionChangeHandler(new CursorPropertySelectionReflector(data, aValue));
		}
	}

	protected void applyColorsFontCursor() {
		if (published.isBackgroundSet() && published.isOpaque())
			ControlsUtils.applyBackground(this, published.getBackground());
		if (published.isForegroundSet())
			ControlsUtils.applyForeground(this, published.getForeground());
		if (published.isFontSet())
			ControlsUtils.applyFont(this, published.getFont());
		if (published.isCursorSet())
			ControlsUtils.applyCursor(this, published.getCursor());
	}

	/**
	 * Sets entity instance, that have to be used as rows source. Configures
	 * tree if needed.
	 * 
	 * @param aValue
	 * @throws Exception
	 *             public void setRowsSource(Entity aValue) throws Exception {
	 *             if (rowsSource != aValue) { if (sortHandlerReg != null)
	 *             sortHandlerReg.removeHandler(); if (rowsSource != null &&
	 *             rowsSource.getRowset() != null)
	 *             rowsSource.getRowset().removeRowsetListener
	 *             (markerRerenderer); rowsSource = aValue; if (rowsSource !=
	 *             null) { Runnable onResize = new Runnable() {
	 * @Override public void run() { ModelGrid.this.getElement().<XElement>
	 *           cast().unmask(); setupVisibleRanges(); }
	 * 
	 *           }; Runnable onSort = new Runnable() {
	 * @Override public void run() { if (dataProvider instanceof
	 *           IndexOfProvider<?>) ((IndexOfProvider<?>)
	 *           dataProvider).rescan(); }
	 * 
	 *           }; Runnable onLoadingStart = new Runnable() {
	 * @Override public void run() { ModelGrid.this.getElement().<XElement>
	 *           cast().unmask(); ModelGrid.this.getElement().<XElement>
	 *           cast().loadMask(); } }; Callback<Void, String> onError = new
	 *           Callback<Void, String>() {
	 * @Override public void onSuccess(Void result) { }
	 * @Override public void onFailure(String reason) {
	 *           ModelGrid.this.getElement().<XElement> cast().unmask();
	 *           ModelGrid.this.getElement().<XElement>
	 *           cast().errorMask(reason); } }; if (isTreeConfigured()) {
	 *           RowsetTree tree = new RowsetTree(rowsSource.getRowset(),
	 *           unaryLinkField.field, onLoadingStart, onError);
	 *           TreeDataProvider<Row> treeDataProvider = new
	 *           TreeDataProvider<>(tree, onResize);
	 *           setDataProvider(treeDataProvider); sortHandler = new
	 *           TreeMultiSortHandler<>(treeDataProvider, onSort);
	 *           treeDataProvider.addExpandedCollapsedHandler(new
	 *           ExpandedCollapsedHandler<Row>() {
	 * @Override public void expanded(Row anElement) {
	 *           ColumnSortEvent.fire(ModelGrid.this, sortList); }
	 * @Override public void collapsed(Row anElement) {
	 *           ColumnSortEvent.fire(ModelGrid.this, sortList); }
	 * 
	 *           }); } else { setDataProvider(new
	 *           RowsetDataProvider(rowsSource.getRowset(), onResize,
	 *           onLoadingStart, onError)); sortHandler = new
	 *           ListMultiSortHandler<>(dataProvider.getList(), onSort); }
	 *           sortHandlerReg = addColumnSortHandler(sortHandler); if
	 *           (rowsSource.getRowset() != null)
	 *           rowsSource.getRowset().addRowsetListener(markerRerenderer); } }
	 *           }
	 */

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue != null ? aValue.<PublishedComponent> cast() : null;
		if (published != null) {
			publish(this, published);
			publishColumnNodes(header);
		}
	}

	protected void publishColumnNodes(List<HeaderNode<JavaScriptObject>> aNodes) {
		for (HeaderNode<JavaScriptObject> node : aNodes) {
			String jsName = ((HasJsName) node).getJsName();
			if (jsName != null && !jsName.isEmpty()) {
				HasPublished pCol = (HasPublished) node;
				published.<JsObject> cast().inject(jsName, pCol.getPublished());
			}
			publishColumnNodes(node.getChildren());
		}
	}

	private native static void publish(ModelGrid aWidget, JavaScriptObject aPublished)/*-{
		aPublished.select = function(aRow) {
			if (aRow != null && aRow != undefined)
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::selectRow(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
		};
		aPublished.unselect = function(aRow) {
			if (aRow != null && aRow != undefined)
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::unselectRow(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
		};
		aPublished.clearSelection = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::clearSelection()();
		};
		aPublished.find = function() {
			aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::find()();
		};
		aPublished.findSomething = function() {
			aPublished.find();
		};
		aPublished.makeVisible = function(aRow, needToSelect) {
			var need2Select = arguments.length > 1 ? !!needToSelect : false;
			if (aRow != null)
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::makeVisible(Lcom/google/gwt/core/client/JavaScriptObject;Z)(aRow, need2Select);
			else
				return false;
		};

		Object.defineProperty(aPublished, "rowsHeight", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getRowsHeight()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setRowsHeight(I)(aValue * 1);
			}
		});
		Object.defineProperty(aPublished, "showHorizontalLines", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isShowHorizontalLines()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setShowHorizontalLines(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "showVerticalLines", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isShowVerticalLines()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setShowVerticalLines(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "showOddRowsInOtherColor", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isShowOddRowsInOtherColor()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setShowOddRowsInOtherColor(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "gridColor", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getGridColor()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setGridColor(Lcom/eas/client/form/published/PublishedColor;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "oddRowsColor", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getOddRowsColor()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setOddRowsColor(Lcom/eas/client/form/published/PublishedColor;)(aValue);
			}
		});

		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getOnRender()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "selected", {
			get : function() {
				var selectionList = aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::getJsSelected()();
				var selectionArray = [];
				for ( var i = 0; i < selectionList.@java.util.List::size()(); i++) {
					selectionArray[selectionArray.length] = selectionList.@java.util.List::get(I)(i);
				}
				return selectionArray;
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isEditable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setEditable(Z)(aValue);
			}
		});
		Object.defineProperty(aPublished, "deletable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isDeletable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setDeletable(Z)(aValue);
			}
		});
		Object.defineProperty(aPublished, "insertable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::isInsertable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelGrid::setInsertable(Z)(aValue);
			}
		});
	}-*/;

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public void selectRow(JavaScriptObject aRow) {
		getSelectionModel().setSelected(aRow, true);
	}

	public void unselectRow(JavaScriptObject aRow) {
		getSelectionModel().setSelected(aRow, false);
	}

	public List<JavaScriptObject> getJsSelected() throws Exception {
		List<JavaScriptObject> result = new ArrayList<>();
		for (JavaScriptObject row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row))
				result.add(row);
		}
		return result;
	}

	public void clearSelection() {
		SelectionModel<? super JavaScriptObject> sm = getSelectionModel();
		for (JavaScriptObject row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row)) {
				sm.setSelected(row, false);
			}
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean aValue) {
		editable = aValue;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean aValue) {
		deletable = aValue;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean aValue) {
		insertable = aValue;
	}

	public boolean makeVisible(JavaScriptObject aRow, boolean needToSelect) {
		return false;
	}

	public void find() {
		if (finder == null) {
			finder = new FindWindow(ModelGrid.this);
		}
		finder.show();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (finder != null) {
			finder.close();
		}
	}

	@Override
	public void onResize() {
		super.onResize();
		if (isAttached()) {
			double commonWidth = 0;
			double weightedWidth = 0;
			for (int i = 0; i < getDataColumnCount(); i++) {
				Column<JavaScriptObject, ?> column = getDataColumn(i);
				String factWidth = i < frozenColumns ? scrollableLeft.getColumnWidth(column, false) : scrollableRight.getColumnWidth(column, false);
				ModelColumn mCol = (ModelColumn) column;
				if (mCol.isVisible()) {
					double colWidth = mCol.getDesignedWidth();
					commonWidth += colWidth;
					weightedWidth += colWidth;
				}
			}
			double delta = (scrollableLeftContainer.getElement().getClientWidth() + scrollableRightContainer.getElement().getClientWidth()) - commonWidth;
			if (delta < 0)
				delta = 0;
			for (int i = 0; i < getDataColumnCount(); i++) {
				Column<JavaScriptObject, ?> column = getDataColumn(i);
				ModelColumn mCol = (ModelColumn) column;
				if (mCol.isVisible()) {
					double colWidth = mCol.getDesignedWidth();
					double newFloatWidth = colWidth + colWidth / weightedWidth * delta;
					setColumnWidth(mCol, newFloatWidth, Style.Unit.PX);
				}
			}
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	public PublishedStyle complementPublishedStyle(PublishedStyle aStyle) {
		PublishedStyle complemented = aStyle;
		if (published.isBackgroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setBackground(published.getBackground());
		}
		if (published.isForegroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setForeground(published.getForeground());
		}
		if (published.isFontSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setFont(published.getFont());
		}
		return complemented;
	}
}
