package vn.app.qrcode.data.model.qrcodeobject

import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.SchemeUtil
import vn.app.qrcode.data.constant.EMAIL_PREFIX_ADDRESS
import vn.app.qrcode.data.constant.EMAIL_PREFIX_BODY
import vn.app.qrcode.data.constant.EMAIL_PREFIX_SUBJECT
import vn.app.qrcode.data.constant.EMAIL_SUFFIX_ADDRESS
import vn.app.qrcode.data.constant.EMAIL_SUFFIX_OTHER
import vn.app.qrcode.data.constant.TEXT_EMPTY


/**
 * Encodes a e-mail address, format is: `mailto:mail@address.com`
 *
 */
class EMail : Schema {
    var email: String? = null
    var subject: String? = null
    var body: String? = null

    /**
     * Default constructor to construct new e-mail object.
     */
    constructor() : super() {}

    constructor(email: String?, subject: String?, body: String?) : super() {
        this.email = email
        this.subject = subject
        this.body = body
    }

    constructor(email: String?, body: String?) : super() {
        this.email = email
        this.body = body
    }

    override fun parseSchema(code: String): Schema {
        require(!(code == null || !code.toLowerCase().startsWith(EMAIL_PREFIX_ADDRESS)))
        val parameters = SchemeUtil.getParameters(code.toLowerCase())
        if (parameters.containsKey(EMAIL_PREFIX_ADDRESS)) {
            email = parameters[EMAIL_PREFIX_ADDRESS]
        }
        return this
    }

    override fun generateString(): String {
        val sb = StringBuilder()
        if (email != TEXT_EMPTY) {
            sb.append(EMAIL_PREFIX_ADDRESS).append(email)
        }
        if (subject != TEXT_EMPTY || body != TEXT_EMPTY) {
            sb.append(EMAIL_SUFFIX_ADDRESS)
        }
        if (subject != TEXT_EMPTY) {
            sb.append(EMAIL_PREFIX_SUBJECT).append(subject)
        }
        if (body != TEXT_EMPTY) {
            if (subject != TEXT_EMPTY) {
                sb.append(EMAIL_SUFFIX_OTHER)
            }
            sb.append(EMAIL_PREFIX_BODY).append(body)
        }
        return sb.toString()
    }

    override fun toString(): String {
        return generateString()
    }

    companion object {

        fun parse(emailCode: String): EMail {
            val mail = EMail()
            mail.parseSchema(emailCode)
            return mail
        }
    }
}
