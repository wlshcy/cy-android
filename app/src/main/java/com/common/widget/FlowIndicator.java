package com.common.widget;


public interface FlowIndicator extends ViewFlow.ViewSwitchListener {

    /**
     * Set the current ViewFlow. This method is called by the ViewFlow when the
     * FlowIndicator is attached to it.
     *
     * @param view
     */
    void setViewFlow(ViewFlow view);

    /**
     * The scroll position has been changed. A FlowIndicator may implement this
     * method to reflect the current position
     *
     * @param oldh
     * @param oldv
     * @param h
     */
    void onScrolled(int h);
}