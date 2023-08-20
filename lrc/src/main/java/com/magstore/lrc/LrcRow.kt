package com.magstore.lrc

import android.util.Log

class LrcRow() : Comparable<LrcRow> {

    private val TAG = "LrcRow"

    /** begin time of this lrc row  */
    var time: Long = 0

    /** content of this lrc  */
    var content: String? = null

    var strTime: String? = null

     constructor(strTime: String, time: Long, content: String) : this() {
        this.strTime = strTime
        this.time = time
        this.content = content
        Log.d(
            TAG,
            "strTime:$strTime time:$time content:$content"
        )
    }

    /**
     * create LrcRows by standard Lrc Line , if not standard lrc line,
     * return false<br></br>
     * [00:00:20] balabalabalabala
     */
    fun createRows(standardLrcLine: String): List<LrcRow>? {
        return try {
            if (standardLrcLine.indexOf("[") != 0 || standardLrcLine.indexOf("]") != 9) {
                return null
            }
            val lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]")
            val content =
                standardLrcLine.substring(lastIndexOfRightBracket + 1, standardLrcLine.length)

            // times [mm:ss.SS][mm:ss.SS] -> *mm:ss.SS**mm:ss.SS*
            val times = standardLrcLine.substring(0, lastIndexOfRightBracket + 1).replace("[", "-")
                .replace("]", "-")
            val arrTimes = times.split("-").toTypedArray()
            val listTimes: ArrayList<LrcRow> = ArrayList()
            for (temp in arrTimes) {
                if (temp.trim { it <= ' ' }.isEmpty()) {
                    continue
                }
                val lrcRow = LrcRow(temp, timeConvert(temp), content)
                listTimes.add(lrcRow)
            }
            listTimes
        } catch (e: Exception) {
            Log.e(TAG, "createRows exception:" + e.message)
            null
        }
    }

    private fun timeConvert(timeString: String): Long {
        var timeString = timeString
        timeString = timeString.replace('.', ':')
        val times = timeString.split(":").toTypedArray()
        // mm:ss:SS
        return (Integer.valueOf(times[0]) * 60 * 1000 + Integer.valueOf(times[1]) * 1000 +
                Integer.valueOf(times[2])).toLong()
    }

    override operator fun compareTo(another: LrcRow): Int {
        return (time - another.time).toInt()
    }
}