package ng.com.zorochi.fpsgametracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//class GamesAdapter(private var gamesList: List<Game>) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {
//
//    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val gameImage: ImageView = itemView.findViewById(R.id.gameImageView)
//        val gameName: TextView = itemView.findViewById(R.id.gameNameTextView)
//        val gameCategory: TextView = itemView.findViewById(R.id.gameCategoryTextView)
//        val gameRating: TextView = itemView.findViewById(R.id.gameRatingTextView)
//        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)  // Add this line
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_game, parent, false)
//        return GameViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
//        val game = gamesList[position]
//        holder.gameName.text = game.name
//        holder.gameCategory.text = game.category
//        holder.gameRating.text = "Rating: ${game.rating}"
//        Glide.with(holder.itemView.context).load(game.image_url).into(holder.gameImage)
//
//        // Update favorite button state
//        val favoriteIcon = if (game.isFavorite) R.drawable.favorite_filled else R.drawable.favorite_unfilled
//        holder.favoriteButton.setImageResource(favoriteIcon)
//
//        // Handle favorite button click
//        holder.favoriteButton.setOnClickListener {
//            // Perform your action here, e.g., toggle favorite status or notify a listener
//            // For example, you might call a method to update the favorite status in your model
//            handleFavoriteButtonClick(game, holder)
//        }
//    }
//
//    private fun handleFavoriteButtonClick(game: Game, holder: GameViewHolder) {
////        TODO: Implement this method
//        // Perform your action here, e.g., toggle favorite status or notify a listener
//        // For example, you might call a method to update the favorite status in your model
//        val prefs = holder.itemView.context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
//        prefs.edit().putBoolean(game.name, game.isFavorite).apply()
//    }
//
//    override fun getItemCount() = gamesList.size
//
//    // Method to update the list and notify the adapter
//    fun submitList(newGamesList: List<Game>) {
//        gamesList = newGamesList
//        notifyDataSetChanged()
//    }
//}

class GamesAdapter(private var gamesList: List<Game>) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

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

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gamesList[position]
        holder.gameName.text = game.name
        holder.gameCategory.text = game.category
        holder.gameRating.text = "Rating: ${game.rating}"
        Glide.with(holder.itemView.context).load(game.image_url).into(holder.gameImage)

        // Update favorite button state
        val favoriteIcon = if (game.isFavorite) R.drawable.favorite_filled else R.drawable.favorite_unfilled
        holder.favoriteButton.setImageResource(favoriteIcon)

        // Handle favorite button click
        holder.favoriteButton.setOnClickListener {
            game.isFavorite = !game.isFavorite
            // Update the button icon based on the new favorite status
            val newFavoriteIcon = if (game.isFavorite) R.drawable.favorite_filled else R.drawable.favorite_unfilled
            holder.favoriteButton.setImageResource(newFavoriteIcon)

            // Notify that this item has changed
            notifyItemChanged(position)

            // Perform additional actions (e.g., save to local storage or database)
            handleFavoriteButtonClick(game, holder)
        }
    }

    private fun handleFavoriteButtonClick(game: Game, holder: GameViewHolder) {
        // Perform your action here, e.g., save the favorite status to a database or shared preferences
        val prefs = holder.itemView.context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(game.name, game.isFavorite).apply()
    }

    override fun getItemCount() = gamesList.size

    // Method to update the list and notify the adapter
    fun submitList(newGamesList: List<Game>) {
        gamesList = newGamesList
        notifyDataSetChanged()
    }
}
