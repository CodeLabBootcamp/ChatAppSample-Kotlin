package camp.codelab.firebase

class Message(
    var text: String = "",
    val type: String = Types.TEXT
) {
    object Types {
        const val TEXT = "TEXT"
        const val IMAGE = "IMAGE"
    }
}