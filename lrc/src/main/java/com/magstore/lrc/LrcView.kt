package com.magstore.lrc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class LrcView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ILrcView {
    val TAG = "LrcView"

    /** normal display mode */
    private  val DISPLAY_MODE_NORMAL = 0

    /** seek display mode  */
    private val DISPLAY_MODE_SEEK = 1

    /** scale display mode ,scale font size */
    private val DISPLAY_MODE_SCALE = 2

    private var mLrcRows // all lrc rows of one lrc file
            : List<LrcRow>? = null
    private val mMinSeekFiredOffset = 10 // min offset for fire seek action, px;

    private var mHignlightRow = 0 // current singing row , should be highlighted.

    private val mHignlightRowColor = resources.getColor(android.R.color.holo_red_dark)
    private val mNormalRowColor = Color.WHITE
    private val mSeekLineColor = Color.CYAN
    private val mSeekLineTextColor = Color.CYAN
    private var mSeekLineTextSize = 18
    private val mMinSeekLineTextSize = 18
    private val mMaxSeekLineTextSize = 22
    private var mLrcFontSize = 35// font size of lrc

    private val mMinLrcFontSize = 15
    private val mMaxLrcFontSize = 30
    private val mPaddingY = 10 // padding of each row

    private val mSeekLinePaddingX = 0 // Seek line padding x

    private var mDisplayMode = DISPLAY_MODE_NORMAL
    private var mLrcViewListener: ILrcView.LrcViewListener? = null

    private var mLoadingLrcTip: String? = "Downloading lrc..."

    private var mPaint: Paint? = null

    init{
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.textSize = 25F
    }

    override fun setListener(l: ILrcView.LrcViewListener?) {
        mLrcViewListener = l
    }

    fun setLoadingTipText(text: String?) {
        mLoadingLrcTip = text
    }

    override fun onDraw(canvas: Canvas) {
        val height = height // height of this view
        val width = width // width of this view
        if (mLrcRows == null || mLrcRows!!.isEmpty()) {
            if (mLoadingLrcTip != null) {
                // draw tip when no lrc.
                mPaint!!.color = mHignlightRowColor
                mPaint!!.textSize = 25F
                mPaint!!.textAlign = Align.CENTER
                canvas.drawText(
                    mLoadingLrcTip!!, (width / 2).toFloat(), (height / 2 - mLrcFontSize).toFloat(),
                    mPaint!!
                )
            }
            return
        }
        var rowY: Int // vertical point of each row.
        val rowX = width / 2
        var rowNum: Int

        // 1, draw highlight row at center.
        // 2, draw rows above highlight row.
        // 3, draw rows below highlight row.

        // 1 highlight row
        val highlightText = mLrcRows!![mHignlightRow].content
        val highlightRowY = height / 2 - mLrcFontSize
        mPaint!!.color = mHignlightRowColor
        mPaint!!.textSize = 25F
        mPaint!!.textAlign = Align.CENTER
        canvas.drawText(highlightText!!, rowX.toFloat(), highlightRowY.toFloat(), mPaint!!)
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            // draw Seek line and current time when moving.
            mPaint!!.color = mSeekLineColor
            canvas.drawLine(
                mSeekLinePaddingX.toFloat(),
                highlightRowY.toFloat(),
                (width - mSeekLinePaddingX).toFloat(),
                highlightRowY.toFloat(),
                mPaint!!
            )
            mPaint!!.color = mSeekLineTextColor
            mPaint!!.textSize = mSeekLineTextSize.toFloat()
            mPaint!!.textAlign = Align.LEFT
            canvas.drawText(
                mLrcRows!![mHignlightRow].strTime!!, 0f, highlightRowY.toFloat(),
                mPaint!!
            )
        }

        // 2 above rows
        mPaint!!.color = mNormalRowColor
        mPaint!!.textSize = 25F
        mPaint!!.textAlign = Align.CENTER
        rowNum = mHignlightRow - 1
        rowY = highlightRowY - mPaddingY - mLrcFontSize
        while (rowY > -mLrcFontSize && rowNum >= 0) {
            val text = mLrcRows!![rowNum].content
            canvas.drawText(text!!, rowX.toFloat(), rowY.toFloat(), mPaint!!)
            rowY -= mPaddingY + mLrcFontSize
            rowNum--
        }

        // 3 below rows
        rowNum = mHignlightRow + 1
        rowY = highlightRowY + mPaddingY + mLrcFontSize
        while (rowY < height && rowNum < mLrcRows!!.size) {
            val text = mLrcRows!![rowNum].content
            canvas.drawText(text!!, rowX.toFloat(), rowY.toFloat(), mPaint!!)
            rowY += mPaddingY + mLrcFontSize
            rowNum++
        }
    }

    fun seekLrc(position: Int, cb: Boolean) {
        if (mLrcRows == null || position < 0 || position > mLrcRows!!.size) {
            return
        }
        val lrcRow = mLrcRows!![position]
        mHignlightRow = position
        invalidate()
        if (mLrcViewListener != null && cb) {
            mLrcViewListener!!.onLrcSeeked(position, lrcRow)
        }
    }

    private var mLastMotionY = 0f
    private val mPointerOneLastMotion = PointF()
    private val mPointerTwoLastMotion = PointF()
    private var mIsFirstMove =
        false // whether is first move , some events can't not detected in touch down,

    // such as two pointer touch, so it's good place to detect it in first move

    // such as two pointer touch, so it's good place to detect it in first move
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mLrcRows == null || mLrcRows!!.isEmpty()) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "down,mLastMotionY:$mLastMotionY")
                mLastMotionY = event.y
                mIsFirstMove = true
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    Log.d(TAG, "two move")
                    doScale(event)
                    return true
                }
                Log.d(TAG, "one move")
                // single pointer mode ,seek
                if (mDisplayMode == DISPLAY_MODE_SCALE) {
                    //if scaling but pointer become not two ,do nothing.
                    return true
                }
                doSeek(event)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mDisplayMode == DISPLAY_MODE_SEEK) {
                    seekLrc(mHignlightRow, true)
                }
                mDisplayMode = DISPLAY_MODE_NORMAL
                invalidate()
            }
        }
        return true
    }

    private fun doScale(event: MotionEvent) {
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            // if Seeking but pointer become two, become to scale mode
            mDisplayMode = DISPLAY_MODE_SCALE
            Log.d(TAG, "two move but teaking ...change mode")
            return
        }
        // two pointer mode , scale font
        if (mIsFirstMove) {
            mDisplayMode = DISPLAY_MODE_SCALE
            invalidate()
            mIsFirstMove = false
            setTwoPointerLocation(event)
        }
        val scaleSize = getScale(event)
        Log.d(TAG, "scaleSize:$scaleSize")
        if (scaleSize != 0) {
            setNewFontSize(scaleSize)
            invalidate()
        }
        setTwoPointerLocation(event)
    }

    private fun doSeek(event: MotionEvent) {
        val y = event.y
        val offsetY = y - mLastMotionY // touch offset.
        if (Math.abs(offsetY) < mMinSeekFiredOffset) {
            // move to short ,do not fire seek action
            return
        }
        mDisplayMode = DISPLAY_MODE_SEEK
        val rowOffset = abs(offsetY.toInt() / mLrcFontSize) // highlight row offset.
        Log.d(
            TAG,
            "move new hightlightrow : $mHignlightRow offsetY: $offsetY rowOffset:$rowOffset"
        )
        if (offsetY < 0) {
            // finger move up
            mHignlightRow += rowOffset
        } else if (offsetY > 0) {
            // finger move down
            mHignlightRow -= rowOffset
        }
        mHignlightRow = max(0, mHignlightRow)
        mHignlightRow = mHignlightRow.coerceAtMost(mLrcRows!!.size - 1)
        if (rowOffset > 0) {
            mLastMotionY = y
            invalidate()
        }
    }

    private fun setTwoPointerLocation(event: MotionEvent) {
        mPointerOneLastMotion.x = event.getX(0)
        mPointerOneLastMotion.y = event.getY(0)
        mPointerTwoLastMotion.x = event.getX(1)
        mPointerTwoLastMotion.y = event.getY(1)
    }

    private fun setNewFontSize(scaleSize: Int) {
        mLrcFontSize += scaleSize
        mSeekLineTextSize += scaleSize
        mLrcFontSize = max(mLrcFontSize, mMinLrcFontSize)
        mLrcFontSize = min(mLrcFontSize, mMaxLrcFontSize)
        mSeekLineTextSize = max(mSeekLineTextSize, mMinSeekLineTextSize)
        mSeekLineTextSize = min(mSeekLineTextSize, mMaxSeekLineTextSize)
    }

    // get font scale offset
    private fun getScale(event: MotionEvent): Int {
        Log.d(TAG, "scaleSize getScale")
        val x0 = event.getX(0)
        val y0 = event.getY(0)
        val x1 = event.getX(1)
        val y1 = event.getY(1)
        val maxOffset: Float // max offset between x or y axis,used to decide scale size
        val zoomin: Boolean
        val oldXOffset = abs(mPointerOneLastMotion.x - mPointerTwoLastMotion.x)
        val newXoffset = abs(x1 - x0)
        val oldYOffset = abs(mPointerOneLastMotion.y - mPointerTwoLastMotion.y)
        val newYoffset = abs(y1 - y0)
        maxOffset = abs(newXoffset - oldXOffset).coerceAtLeast(abs(newYoffset - oldYOffset))
        zoomin = if (maxOffset == abs(newXoffset - oldXOffset)) {
            newXoffset > oldXOffset
        } else {
            newYoffset > oldYOffset
        }
        Log.d(TAG, "scaleSize maxOffset:$maxOffset")
        return if (zoomin) (maxOffset / 10).toInt() else -(maxOffset / 10).toInt()
    }

    override fun setLrc(lrcRows: List<LrcRow>?) {
        mLrcRows = lrcRows
        invalidate()
    }

    override fun seekLrcToTime(time: Long) {
        if (mLrcRows == null || mLrcRows!!.isEmpty()) {
            return
        }
        if (mDisplayMode != DISPLAY_MODE_NORMAL) {
            // touching
            return
        }
        Log.d(TAG, "seekLrcToTime:$time")
        // find row
        for (i in mLrcRows!!.indices) {
            val current = mLrcRows!![i]
            val next = if (i + 1 == mLrcRows!!.size) null else mLrcRows!![i + 1]
            if (time >= current.time && next != null && time < next.time
                || time > current.time && next == null
            ) {
                seekLrc(i, false)
                return
            }
        }
    }

}