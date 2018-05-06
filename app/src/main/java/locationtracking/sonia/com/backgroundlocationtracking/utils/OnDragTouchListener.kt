package locationtracking.sonia.com.backgroundlocationtracking.utils

import android.view.MotionEvent
import android.support.v4.view.ViewCompat.animate
import android.R.attr.x
import android.R.attr.y
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.View
import android.view.View.OnTouchListener



/**
 * Created by soniawadji on 06/05/18.
 */
class OnDragTouchListener @JvmOverloads constructor(view: View, parent: View = view.getParent() as View, onDragActionListener: OnDragActionListener? = null) : View.OnTouchListener {

    private var mView: View? = null
    private var mParent: View? = null
    private var isDragging: Boolean = false
    private var isInitialized = false

    private var width: Int = 0
    private var xWhenAttached: Float = 0.toFloat()
    private var maxLeft: Float = 0.toFloat()
    private var maxRight: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()

    private var height: Int = 0
    private var yWhenAttached: Float = 0.toFloat()
    private var maxTop: Float = 0.toFloat()
    private var maxBottom: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()

    private var mOnDragActionListener: OnDragActionListener? = null

    /**
     * Callback used to indicate when the drag is finished
     */
    interface OnDragActionListener {
        /**
         * Called when drag event is started
         *
         * @param view The view dragged
         */
        fun onDragStart(view: View?)

        /**
         * Called when drag event is completed
         *
         * @param view The view dragged
         */
        fun onDragEnd(view: View?)
    }

    constructor(view: View, onDragActionListener: OnDragActionListener) : this(view, view.getParent() as View, onDragActionListener) {}

    init {
        initListener(view, parent)
        setOnDragActionListener(onDragActionListener)
    }

    fun setOnDragActionListener(onDragActionListener: OnDragActionListener?) {
        mOnDragActionListener = onDragActionListener
    }

    fun initListener(view: View, parent: View) {
        mView = view
        mParent = parent
        isDragging = false
        isInitialized = false
    }

    fun updateBounds() {
        updateViewBounds()
        updateParentBounds()
        isInitialized = true
    }

    fun updateViewBounds() {
        width = mView!!.getWidth()
        xWhenAttached = mView!!.getX()
        dX = 0f

        height = mView!!.getHeight()
        yWhenAttached = mView!!.getY()
        dY = 0f
    }

    fun updateParentBounds() {
        maxLeft = 0f
        maxRight = maxLeft + mParent!!.getWidth()

        maxTop = 0f
        maxBottom = maxTop + mParent!!.getHeight()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (isDragging) {
            val bounds = FloatArray(4)
            // LEFT
            bounds[0] = event.rawX + dX
            if (bounds[0] < maxLeft) {
                bounds[0] = maxLeft
            }
            // RIGHT
            bounds[2] = bounds[0] + width
            if (bounds[2] > maxRight) {
                bounds[2] = maxRight
                bounds[0] = bounds[2] - width
            }
            // TOP
            bounds[1] = event.rawY + dY
            if (bounds[1] < maxTop) {
                bounds[1] = maxTop
            }
            // BOTTOM
            bounds[3] = bounds[1] + height
            if (bounds[3] > maxBottom) {
                bounds[3] = maxBottom
                bounds[1] = bounds[3] - height
            }

            when (event.action) {
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> onDragFinish()
                MotionEvent.ACTION_MOVE -> mView!!.animate().x(bounds[0]).y(bounds[1]).setDuration(0).start()
            }
            return true
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    if (!isInitialized) {
                        updateBounds()
                    }
                    dX = v.getX() - event.rawX
                    dY = v.getY() - event.rawY
                    if (mOnDragActionListener != null) {
                        mOnDragActionListener!!.onDragStart(mView)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun onDragFinish() {
        if (mOnDragActionListener != null) {
            mOnDragActionListener!!.onDragEnd(mView)
        }

        dX = 0f
        dY = 0f
        isDragging = false
    }
}