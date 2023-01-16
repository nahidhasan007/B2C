package net.sharetrip.shared.utils

enum class RemoteConfigParam(val key: String) {

    PAYMENT_SUCCESS_URL("success_urls"),
    FORCE_UPDATE_ENABLE("is_force_update_enable_for_android"),
    ANDROID_CODE_VERSION("android_code_version"),
    ANDROID_APP_VERSION("android_app_version"),
    ENCRYPT_KEY("encrypt_key"),
    BOOK_TODAY_FLIGHT("book_today_flight"),
    NEED_TO_SHOW_BANNER("need_to_show_banner"),
    HOTEL_DISCOUNT_OPTIONS("hotel_discount_options"),
    FLIGHT_DISCOUNT_OPTIONS("flight_discount_options"),
    QUIZ_TIMEOUT_TIME("quiz_timeout_time"),
    QUIZ_HOMEPAGE_PROMOTION_TEXT("quiz_homepage_promotion_text"),
    QUIZ_FAQ("quiz_faq"),
    QUIZ_TERM_AND_CONDITION("quiz_term_and_condition"),
    FLIGHT_SEARCH_THRESHOLD_TIME("flight_search_threshold_time"),
    FLIGHT_DISCOUNT_BANK_LIST("flight_discount_offer_bank_list"),
    CRISP_WEBSITE_ID("crisp_website_id")

}