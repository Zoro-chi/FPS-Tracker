package ng.com.zorochi.fpsgametracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val name: String = "",
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "rating") val rating: Float = 0f,
    @ColumnInfo(name = "image_url") val image_url: String = "",
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean = false
) {
    val ratingString: String
        get() = rating.toString()
}
