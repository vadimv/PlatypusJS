package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.SpinnerBox;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ValueBox;

public class ConstraintedSpinnerBox extends SpinnerBox<Double> implements HasShowHandlers, HasHideHandlers, HasResizeHandlers {
	
	protected double step = 1.0;
	protected Double min;
	protected Double max;

	public ConstraintedSpinnerBox(ValueBox<Double> aDecorated) {
		super(aDecorated);
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		super.onResize();
		if(isAttached()){
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
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
	protected void increment() {
		Double oldValue = getValue();
		Double newValue = (oldValue != null ? oldValue : 0) + step;
		if (max == null || newValue <= max)
			setValue(newValue, true);
	}

	@Override
	protected void decrement() {
		Double oldValue = getValue();
		Double newValue = (oldValue != null ? oldValue : 0) - step;
		if (min == null || newValue >= min)
			setValue(newValue, true);
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double aValue) {
		min = aValue;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double aValue) {
		max = aValue;
	}

	public Double getStep() {
		return step;
	}

	public void setStep(Double aValue) {
		step = aValue;
	}
}
