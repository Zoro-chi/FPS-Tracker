package ng.com.zorochi.fpsgametracker

data class Game(
    val name: String,
    val category: String,
    val rating: Float,
    val imageUrl: String
) {
    val ratingString: String
        get() = rating.toString()
}
