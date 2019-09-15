package com.deque.axe.android

import com.deque.axe.android.constants.AxeEventType
import com.deque.axe.android.constants.Constants
import com.deque.axe.android.utils.JsonSerializable

import java.util.HashSet

class AxeEvent private constructor(
    @param:AxeEventType @field:AxeEventType @field:Transient val eventType: Int?, val packageName: String, val  classsName: String, @field:Transient val axeView: AxeView?
) : JsonSerializable {

    val eventTypeName: String

    val id = idCounter++

    val isViewChangeEvent: Boolean
        get() = viewChangeEventTypes.contains(eventType)

    init {
        eventTypeName = Constants.getEventTypeName(eventType)
    }

    /**
     * Construct an AxeEvent using a builder.
     * @param builder An AxeEvent builder.
     */
    constructor(builder: Builder) : this(
        builder.eventType(),
        builder.packageName(),
        builder.className(),
        builder.axeView()
    ) {
    }

    fun isEventType(@AxeEventType eventType: Int): Boolean {
        return eventType == this.eventType
    }

    override fun toString(): String {
        return toJson()
    }

    interface Builder {

        @AxeEventType
        fun eventType(): Int?

        fun packageName(): String

        fun className(): String

        fun axeView(): AxeView?
    }

    companion object {

        private var idCounter = 0

        private val viewChangeEventTypes = HashSet<Int>()

        init {
            viewChangeEventTypes.add(AxeEventType.WINDOW_STATE_CHANGED)
        }
    }
}
