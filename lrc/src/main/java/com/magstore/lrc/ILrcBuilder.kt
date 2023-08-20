package com.magstore.lrc

/**
 * @author majed
 */
interface ILrcBuilder {

    companion object {
        fun getInstance(): ILrcBuilder {
            return DefaultLrcBuilder
        }
    }

    /**
     * [DefaultLrcBuilder.getLrcRows]
     * */
    fun getLrcRows(rawLrc: String): List<LrcRow>?
}