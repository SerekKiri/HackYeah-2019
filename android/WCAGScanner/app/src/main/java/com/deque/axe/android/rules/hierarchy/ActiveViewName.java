package com.deque.axe.android.rules.hierarchy;

import com.deque.axe.android.AxeView;
import com.deque.axe.android.constants.AxeImpact;
import com.deque.axe.android.constants.AxeStandard;
import com.deque.axe.android.constants.AxeStatus;
import com.deque.axe.android.rules.hierarchy.base.ActiveView;
import com.deque.axe.android.utils.AxeTextUtils;
import com.deque.axe.android.wrappers.AxeProps;
import com.deque.axe.android.wrappers.AxeProps.Name;

@SuppressWarnings("unused")
public class ActiveViewName extends ActiveView {

  public ActiveViewName() {
    super(AxeStandard.WCAG_21, AxeImpact.CRITICAL,
        "1.1.1 Views should have a speakable text");
  }

  @Override
  public void collectProps(AxeView axeView, AxeProps axeProps) {

    super.collectProps(axeView, axeProps);

    axeProps.put(Name.SPEAKABLE_TEXT, axeView.speakableTextRecursive());
  }

  @Override
  public String runRule(AxeProps axeProps) {

    final String speakableText = axeProps.get(Name.SPEAKABLE_TEXT, String.class);
    final boolean isActive = axeProps.get(Name.IS_CLICKABLE, Boolean.class);

    if (!isActive) {
      return AxeStatus.INAPPLICABLE;
    }

    if (AxeTextUtils.isNullOrEmpty(speakableText)) {
      return AxeStatus.FAIL;
    } else {
      return AxeStatus.PASS;
    }
  }
}
