package com.kuishana.roundedview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider

class RoundedConstraintLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var path: Path? = null
    private var paint: Paint? = null
    private var topLeftRadius = 0.0f
    private var topRightRadius = 0.0f
    private var bottomLeftRadius = 0.0f
    private var bottomRightRadius = 0.0f

    init {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.RoundedConstraintLayout)
            val radius = attributes.getDimension(R.styleable.RoundedConstraintLayout_roundedConstraintLayoutRadius, 0.0f)
            topLeftRadius = attributes.getDimension(R.styleable.RoundedConstraintLayout_roundedConstraintLayoutTopLeftRadius, radius)
            topRightRadius = attributes.getDimension(R.styleable.RoundedConstraintLayout_roundedConstraintLayoutTopRightRadius, radius)
            bottomLeftRadius = attributes.getDimension(R.styleable.RoundedConstraintLayout_roundedConstraintLayoutBottomLeftRadius, radius)
            bottomRightRadius = attributes.getDimension(R.styleable.RoundedConstraintLayout_roundedConstraintLayoutBottomRightRadius, radius)
            attributes.recycle()
            if (topLeftRadius > 0.0f
                    || topRightRadius > 0.0f
                    || bottomLeftRadius > 0.0f
                    || bottomRightRadius > 0.0f) {
                if (topLeftRadius == topRightRadius
                        && bottomLeftRadius == bottomRightRadius
                        && topLeftRadius == bottomRightRadius
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                outline?.setRoundRect(0, 0, width, height, topRightRadius)
                        }
                    }
                    clipToOutline = true
                } else {
                    setWillNotDraw(false)
                    path = Path()
                    paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    paint?.xfermode = (PorterDuffXfermode(PorterDuff.Mode.DST_IN))
                }
            }
        }
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            path?.let {
                it.rewind()
                val radii = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius
                        , bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius)
                it.addRoundRect(RectF(0.0f, 0.0f, w.toFloat(), h.toFloat()), radii, Path.Direction.CW)
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (null == path || null == paint) {
            super.draw(canvas)
        } else {
            canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG)
            super.draw(canvas)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }
}