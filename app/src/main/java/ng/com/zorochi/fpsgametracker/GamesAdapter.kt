package ng.com.zorochi.fpsgametracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GamesAdapter(private val gamesList: List<Game>) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameName: TextView = itemView.findViewById(R.id.gameNameTextView)
        val gameCategory: TextView = itemView.findViewById(R.id.gameCategoryTextView)
        val gameRating: TextView = itemView.findViewById(R.id.gameRatingTextView)
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
        Glide.with(holder.itemView.context).load(game.imageUrl).into(holder.gameImage)
    }

    override fun getItemCount() = gamesList.size
}
