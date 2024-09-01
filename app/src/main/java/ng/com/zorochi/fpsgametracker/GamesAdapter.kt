package ng.com.zorochi.fpsgametracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GamesAdapter(
    private var gamesList: List<Game>,
    private val onFavoriteClick: (Game, ImageButton) -> Unit
) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameName: TextView = itemView.findViewById(R.id.gameNameTextView)
        val gameCategory: TextView = itemView.findViewById(R.id.gameCategoryTextView)
        val gameRating: TextView = itemView.findViewById(R.id.gameRatingTextView)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gamesList[position]
        holder.gameName.text = game.name
        holder.gameCategory.text = game.category
        holder.gameRating.text = "Rating: ${game.rating}"
        Glide.with(holder.itemView.context).load(game.image_url).into(holder.gameImage)

        // Set the favorite button image based on the isFavorite status
        if (game.isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.favorite_filled)
        } else {
            holder.favoriteButton.setImageResource(R.drawable.favorite_unfilled)
        }

        // Set up the click listener for the favorite button
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(game, holder.favoriteButton)
        }
    }
}