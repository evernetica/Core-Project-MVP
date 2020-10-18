package com.eight.core.ui.text

import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import androidx.core.text.getSpans
import com.eight.core.common.Constant
import org.xml.sax.XMLReader

class ListTagHandler(
    private val bulletRadiusDp: Int,
    private val gapWidthDp: Int,
    private val color: Int
) : Html.TagHandler {

    private var start = -1

    override fun handleTag(
        opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader
    ) = when (tag) {
        UL -> ul(opening, output)
        LI -> li(opening, output)
        else -> Unit
    }

    fun process(source: Spanned): SpannableStringBuilder {
        val builder = SpannableStringBuilder(source)

        val bullets = builder.getSpans<BulletSpan>(0, builder.length)
        for (bullet in bullets) {
            val start = builder.getSpanStart(bullet)
            val end = builder.getSpanEnd(bullet)

            builder.removeSpan(bullet)
            builder.setSpan(
                ImprovedBulletSpan(bulletRadiusDp, gapWidthDp, color),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        val index = builder.lastIndexOf(NEW_LINE)
        if (bullets.isNotEmpty() && index > 0) builder.replace(index, index + 1, Constant.EMPTY)

        return builder
    }

    private fun li(opening: Boolean, output: Editable) {
        if (!opening) {
            output.append(NEW_LINE)
            val end = output.length
            if (start != -1) output.setSpan(
                ImprovedBulletSpan(bulletRadiusDp, gapWidthDp, color),
                start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )

        } else {
            output.append(NEW_LINE)
            start = output.length
        }
    }

    private fun ul(opening: Boolean, output: Editable) {
        if (opening) output.append(NEW_LINE)
    }

    private companion object {
        const val UL = "ul"
        const val LI = "li"
        const val NEW_LINE = '\n'
    }
}