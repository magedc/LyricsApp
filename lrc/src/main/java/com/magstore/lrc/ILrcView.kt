package com.magstore.lrc

/**
 * use ILrcView to display lyric, seek and scale.
 * @author majed
 */
interface ILrcView {

    /**
     * set the lyric rows to display
     * [LrcView.setLrc]
     */
    fun setLrc(lrcRows: List<LrcRow>?)

    /**
     * seek lyric row to special time
     * @time time to be seek
     * [LrcView.seekLrcToTime]
     */
    fun seekLrcToTime(time: Long)

    /**
     * [LrcView.setListener]
     * */
    fun setListener(l: LrcViewListener?)

    interface LrcViewListener {
        /**
         * when lyric line was seeked by user
         */
        fun onLrcSeeked(newPosition: Int, row: LrcRow?)
    }
}
