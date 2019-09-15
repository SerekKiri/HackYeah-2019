package com.example.wcagscanner

import android.util.Log
import com.deque.axe.android.AxeContext
import com.deque.axe.android.AxeRule
import com.deque.axe.android.AxeRuleViewHierarchy
import com.deque.axe.android.AxeView
import com.deque.axe.android.constants.AxeImpact
import com.deque.axe.android.constants.AxeStandard
import com.deque.axe.android.constants.AxeStatus
import com.deque.axe.android.wrappers.AxeProps
import com.deque.axe.android.wrappers.AxeRect

class ScrollableRule : ScrollableRulee(
    AxeStandard.WCAG_21, AxeImpact.MODERATE,
    "Scrollable should be scrollablew"
) {

    override fun isApplicable(axeProps: AxeProps): Boolean {
        //TODO: Respect isEnabled.
        return true
    }

}