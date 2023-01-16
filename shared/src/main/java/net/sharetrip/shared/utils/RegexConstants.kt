package net.sharetrip.shared.utils

const val PASSPORT_REGEX = "(?![a-zA-Z]*\$)[a-zA-Z0-9]{7,9}"
const val NAME_REGEX = "[a-zA-Z][a-zA-Z ]*"
const val PHONE_VALIDATION_REGEX = "^(?:\\+?88)?01[13-9][0-9]{8}$"

const val emailFirstPart = "([A-Z0-9a-z._%+-]{3,30})"
const val emailSecondPart = "([A-Z0-9a-z]([A-Z0-9a-z-]{0,30}[A-Z0-9a-z])?\\.){1,5}"
const val EMAIL_REGEX = "$emailFirstPart@$emailSecondPart[A-Za-z]{2,8}"
const val PASSWORD_REGEX = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*"
