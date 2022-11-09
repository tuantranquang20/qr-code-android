package vn.app.qrcode.data.model.qrcodeobject

import net.glxn.qrgen.core.scheme.Schema
import net.glxn.qrgen.core.scheme.SchemeUtil
import vn.app.qrcode.data.constant.VCARD_BEGIN
import vn.app.qrcode.data.constant.VCARD_END
import vn.app.qrcode.data.constant.VCARD_PREFIX_ADDRESS
import vn.app.qrcode.data.constant.VCARD_PREFIX_BIRTHDAY
import vn.app.qrcode.data.constant.VCARD_PREFIX_COMPANY
import vn.app.qrcode.data.constant.VCARD_PREFIX_EMAIL
import vn.app.qrcode.data.constant.VCARD_PREFIX_NAME
import vn.app.qrcode.data.constant.VCARD_PREFIX_NOTE
import vn.app.qrcode.data.constant.VCARD_PREFIX_PHONE
import vn.app.qrcode.data.constant.VCARD_PREFIX_TITLE
import vn.app.qrcode.data.constant.VCARD_PREFIX_WEB

/**
 * A simple wrapper for vCard data to use with ZXing QR Code generator.
 *
 *
 * See also http://zxing.appspot.com/generator/ and Contact Information
 *
 */
class VCard : Schema {
    var name: String? = null
        private set
    var company: String? = null
        private set
    var title: String? = null
        private set
    var phoneNumber: String? = null
        private set
    var email: String? = null
        private set
    var address: String? = null
        private set
    var website: String? = null
        private set
    var birthday: String? = null
        private set
    var note: String? = null

    constructor() : super() {}
    constructor(name: String?) {
        this.name = name
    }

    fun setName(name: String?): VCard {
        this.name = name
        return this
    }

    fun setCompany(company: String?): VCard {
        this.company = company
        return this
    }

    fun setPhoneNumber(phoneNumber: String?): VCard {
        this.phoneNumber = phoneNumber
        return this
    }

    fun setTitle(title: String?): VCard {
        this.title = title
        return this
    }

    fun setEmail(email: String?): VCard {
        this.email = email
        return this
    }

    fun setAddress(address: String?): VCard {
        this.address = address
        return this
    }

    fun setWebsite(website: String?): VCard {
        this.website = website
        return this
    }

    fun setBirthday(birthday: String?): VCard {
        this.birthday = birthday
        return this
    }

    override fun parseSchema(code: String): Schema {
        require(!(code == null || !code.startsWith(VCARD_BEGIN)))
        val parameters = SchemeUtil.getParameters(code)
        if (parameters.containsKey(VCARD_PREFIX_NAME)) {
            setName(parameters[VCARD_PREFIX_NAME])
        }
        if (parameters.containsKey(VCARD_PREFIX_TITLE)) {
            setTitle(parameters[VCARD_PREFIX_TITLE])
        }
        if (parameters.containsKey(VCARD_PREFIX_COMPANY)) {
            setCompany(parameters[VCARD_PREFIX_COMPANY])
        }
        if (parameters.containsKey(VCARD_PREFIX_ADDRESS)) {
            setAddress(parameters[VCARD_PREFIX_ADDRESS])
        }
        if (parameters.containsKey(VCARD_PREFIX_EMAIL)) {
            setEmail(parameters[VCARD_PREFIX_EMAIL])
        }
        if (parameters.containsKey(VCARD_PREFIX_WEB)) {
            setWebsite(parameters[VCARD_PREFIX_WEB])
        }
        if (parameters.containsKey(VCARD_PREFIX_PHONE)) {
            setPhoneNumber(parameters[VCARD_PREFIX_PHONE])
        }
        if (parameters.containsKey(VCARD_PREFIX_BIRTHDAY)) {
            setBirthday(parameters[VCARD_PREFIX_BIRTHDAY])
        }
        if (parameters.containsKey(VCARD_PREFIX_NOTE)) {
            note = parameters[VCARD_PREFIX_NOTE]
        }
        return this
    }

    override fun generateString(): String {
        val sb = StringBuilder()
        sb.append(VCARD_BEGIN).append(SchemeUtil.LINE_FEED)
        if (name != null) {
            sb.append(VCARD_PREFIX_NAME).append(name)
        }
        if (company != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_COMPANY)
                .append(company)
        }
        if (title != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_TITLE).append(title)
        }
        if (phoneNumber != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_PHONE)
                .append(phoneNumber)
        }
        if (website != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_WEB).append(website)
        }
        if (email != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_EMAIL).append(email)
        }
        if (address != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_ADDRESS)
                .append(address)
        }
        if (note != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_NOTE).append(note)
        }
        if (birthday != null) {
            sb.append(SchemeUtil.LINE_FEED).append(VCARD_PREFIX_BIRTHDAY).append(birthday)
        }
        sb.append(SchemeUtil.LINE_FEED).append(VCARD_END)
        return sb.toString()
    }

    /**
     * Returns the textual representation of this vcard of the form
     *
     *
     * BEGIN:VCARD N:John Doe ORG:Company TITLE:Title TEL:1234 URL:www.example.org
     * EMAIL:john.doe@example.org ADR:Street END:VCARD
     *
     */
    override fun toString(): String {
        return generateString()
    }

    companion object {

        fun parse(code: String?): VCard {
            val vCard = VCard()
            if (code != null) {
                vCard.parseSchema(code)
            }
            return vCard
        }
    }
}