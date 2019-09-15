package com.example.wcagscanner;


import com.deque.axe.android.AxeContext;
import com.deque.axe.android.AxeDevice;
import com.deque.axe.android.AxeRuleViewHierarchy;
import com.deque.axe.android.AxeView;
import com.deque.axe.android.constants.AxeStatus;
import com.deque.axe.android.rules.hierarchy.base.ActiveView;
import com.deque.axe.android.wrappers.AxeProps;
import com.deque.axe.android.wrappers.AxeRect;

public abstract class ScrollableRulee extends AxeRuleViewHierarchy {


    public ScrollableRulee(String standard, int impact, String summary) {
        super(standard, impact, summary);
    }

    @Override
    public void collectProps(AxeView axeView, AxeProps axeProps) {

        axeProps.put(AxeProps.Name.IS_SCROLLABLE, axeView.isScrollable);
    }


    @Override
    public String runRule(AxeProps axeProps) {

        final float dpi = axeProps.get(AxeProps.Name.DPI, Float.class);
        final AxeRect frame = axeProps.get(AxeProps.Name.FRAME, AxeRect.class);

        final long height = frame.height();
        final long width = frame.width();

        final int screenHeight = axeProps.get(AxeProps.Name.SCREEN_HEIGHT, Integer.class);
        final int screenWidth = axeProps.get(AxeProps.Name.SCREEN_WIDTH, Integer.class);
        final boolean isScrollable = axeProps.get(AxeProps.Name.IS_SCROLLABLE, Boolean.class);

        if (isRendered(dpi, height, width) || isOffScreen(frame, screenHeight, screenWidth)) {
            return AxeStatus.INCOMPLETE;
        }

        final long adjustedHeight = Math.round(height / dpi);
        final long adjustedWidth = Math.round(width / dpi);


        if (isScrollable && adjustedHeight < 320) {
            return AxeStatus.FAIL;
        } else {
            return AxeStatus.PASS;
        }
    }

    private boolean isRendered(float dpi, long height, long width) {
        return dpi <= 0 || height < 0 || width < 0;
    }

    private boolean isOffScreen(AxeRect frame, int screenHeight, int screenWidth) {
        if (screenHeight > 0 && screenWidth > 0) {
            return frame.top < 0
                    || frame.left < 0
                    || frame.bottom > screenHeight
                    || frame.right > screenWidth;
        }
        return false;
    }
}
