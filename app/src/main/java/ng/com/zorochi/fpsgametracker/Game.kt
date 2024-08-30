package ng.com.zorochi.fpsgametracker

data class Game(
    val name: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val image_url: String = "",
    var isFavorite: Boolean = false,
) {
    val ratingString: String
        get() = rating.toString()
}
