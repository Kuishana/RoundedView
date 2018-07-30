package com.kuishana.roundedview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider

class RoundedView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {
    private var path: Path? = null
    private var paint: Paint? = null
    private var topLeftRadius = 0.0f
    private var topRightRadius = 0.0f
    private var bottomLeftRadius = 0.0f
    private var bottomRightRadius = 0.0f

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.RoundedView).run {
                val radius = getDimension(R.styleable.RoundedView_roundedViewRadius, 0.0f)
                topLeftRadius = getDimension(R.styleable.RoundedView_roundedViewTopLeftRadius, radius)
                topRightRadius = getDimension(R.styleable.RoundedView_roundedViewTopRightRadius, radius)
                bottomLeftRadius = getDimension(R.styleable.RoundedView_roundedViewBottomLeftRadius, radius)
                bottomRightRadius = getDimension(R.styleable.RoundedView_roundedViewBottomRightRadius, radius)
                recycle()
            }
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
                    paint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                }
            }
        }
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path?.takeIf { w > 0 && h > 0 }?.run {
            rewind()
            addRoundRect(RectF(0.0f, 0.0f, w.toFloat(), h.toFloat())
                    , floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius
                    , bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius), Path.Direction.CW)
        }
    }

    override fun draw(canvas: Canvas) {
        if (null == path || null == paint) {
            super.draw(canvas)
        } else {
            canvas.run {
                saveLayer(null, null, Canvas.ALL_SAVE_FLAG)
                super.draw(this)
                drawPath(path, paint)
                restore()
            }
        }
    }
}