package vn.app.qrcode.data.model.qrcodeobject

import com.google.gson.Gson
import vn.app.qrcode.data.constant.QrCodeType

class EmailQRCode {
    var email: String = ""
    var bcc: String = ""
    var subject: String = ""
    var body: String = ""

    constructor(email: String, bcc: String, subject: String, body: String) {
        this.email = email
        this.bcc = bcc
        this.subject = subject
        this.body = body
    }

    fun getRawValue(): String {
        return "mailto:${email}?bcc=${bcc}&subject=${subject}&body=${body}"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun getEmailDisplay(): String {
        return if (this.bcc.isEmpty()) {
            this.email
        } else {
            "${this.email}, ${this.bcc}"
        }
    }
}

class MessageQRCode {
    var phones: String = ""
    var message: String = ""

    constructor(phones: String, message: String) {
        this.phones = phones
        this.message = message
    }

    fun getRawValue(): String {
        return "SMSTO:${phones}:${message}"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class UrlQRCode {
    var link: String = ""

    constructor(link: String) {
        this.link = link
    }

    fun getRawValue(): String {
        return "URL:${link}"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class WifiQRCode(var ssid: String, var password: String, var type: String) {

    fun getRawValue(): String {
        return "WIFI:T:${type};S:${ssid};P:${password};;"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class LocationQRCode {
    var latitude: String = ""
    var longitude: String = ""
    var address: String = ""

    constructor(latitude: String, longitude: String, address: String) {
        this.latitude = latitude
        this.longitude = longitude
        this.address = address
    }

    constructor(latitude: String, longitude: String) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun getRawValue(): String {
        return "GEO:$latitude,$longitude\n" +
                "QUERY:$address"
    }

    fun getLocationUri(): String {
        return "geo:$latitude,$longitude?q=$latitude,$longitude($address)"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class EventQRCode {
    var name: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var location: String = ""
    var description: String = ""

    constructor(
        name: String,
        startTime: String,
        endTime: String,
        location: String,
        description: String
    ) {
        this.name = name
        this.startTime = startTime
        this.endTime = endTime
        this.location = location
        this.description = description
    }

    fun getRawValue(): String {
        return "BEGIN:VEVENT\n" +
                "SUMMARY:$name\n" +
                "DESCRIPTION:$description\n" +
                "LOCATION:$location\n" +
                "DTSTART:$startTime\n" +
                "DTEND:$endTime\n" +
                "END:VEVENT"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class TextQRCode {
    var content: String = ""

    constructor(content: String) {
        this.content = content
    }

    fun getRawValue(): String {
        return content
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class NameCardQRCode {
    var name: String = ""
    var phoneNumber = ""
    var email = ""
    var address = ""
    var jobTitle = ""
    var url = ""
    var note = ""

    constructor(
        name: String,
        phoneNumber: String,
        email: String,
        address: String,
        jobTitle: String,
        url: String,
        note: String
    ) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.email = email
        this.address = address
        this.jobTitle = jobTitle
        this.url = url
        this.note = note
    }

    fun getRawValue(): String {
        return "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "TYPE_QRCODE:${QrCodeType.NAME_CARD.ordinal}\n" +
                "FN:${name}\n" +
                "TEL:${phoneNumber}\n" +
                "EMAIL:${email}\n" +
                "ADR:${address}\n" +
                "TITLE:${jobTitle}\n" +
                "URL:${url}\n" +
                "NOTE:${note}\n" +
                "END:VCARD"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

class ContactQRCode {
    var name: String = ""
    var phoneNumber: String = ""
    var email: String = ""
    var address: String = ""
    var jobTitle: String = ""
    var url: String = ""
    var note: String = ""
    var birthday: String = ""

    constructor(
        name: String,
        phoneNumber: String,
        email: String,
        address: String,
        jobTitle: String,
        url: String,
        birthday: String,
        note: String
    ) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.email = email
        this.address = address
        this.jobTitle = jobTitle
        this.url = url
        this.birthday = birthday
        this.note = note
    }

    constructor(
        name: String,
        phoneNumber: String,
        email: String,
        address: String
    ) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.email = email
        this.address = address
    }

    fun getRawValue(): String {
        return "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "TYPE_QRCODE:${QrCodeType.CONTACT.ordinal}\n" +
                "FN:${name}\n" +
                "TEL:${phoneNumber}\n" +
                "EMAIL:${email}\n" +
                "ADR:${address}\n" +
                "TITLE:${jobTitle}\n" +
                "URL:${url}\n" +
                "NOTE:${note}\n" +
                "BDAY:${birthday}\n" +
                "END:VCARD"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}