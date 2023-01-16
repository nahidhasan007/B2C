package net.sharetrip.tracker.view.cirium.model

enum class FlightStatus(val status: String) {
    Active ("A"),
    Scheduled ("S"),
    Landed ("L"),
    Canceled ("C"),
    Redirected ("R"),
    Diverted ("D"),
    DataSourceNeeded ("DN"),
    NoOperation ("NO"),
    Unknown ("U")
}
