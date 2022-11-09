package vn.app.qrcode.data.model.qrcodeobject

import java.net.MalformedURLException
import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.Url

/**
 * Encodes a url connection, format is: `HTTP://URL`
 *
 */
class TextQRCodeSchema : Schema {
    private var text: String? = null

    constructor() : super() {}

    constructor(text: String?) : super() {
        this.text = text
    }

    fun TextQRCode(name: String) {
        this.text = name
    }
    fun getText(): String? {
        return if (text != null) {
            text
        } else null
    }

    fun setText(text: String?) {
        try {
            this.text = text
        } catch (e: MalformedURLException) {
            // ignore
            this.text = null
        }
    }

    override fun parseSchema(code: String): Schema {
        setText(code.trim { it <= ' ' })
        return this
    }

    override fun generateString(): String {
        return getText()!!
    }

    override fun toString(): String {
        return generateString()
    }

    companion object {
        fun parse(code: String): Url {
            val u = Url()
            u.parseSchema(code)
            return u
        }
    }
}