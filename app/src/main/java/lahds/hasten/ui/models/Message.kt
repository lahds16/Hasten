package lahds.hasten.ui.models

class Message (
    var messageID: String = "",
    var message: String = "",
    var senderID: String = "",
    var timestamp: Long = 0,
    var isRead: Boolean = false
)