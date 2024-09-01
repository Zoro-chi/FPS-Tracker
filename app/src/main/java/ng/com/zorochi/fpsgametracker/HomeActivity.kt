package ng.com.zorochi.fpsgametracker

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: GamesAdapter
    private val gamesList = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_home)

        showToast("Test Toast Message")

        val gamesRecyclerView = findViewById<RecyclerView>(R.id.gamesRecyclerView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = GamesAdapter(gamesList) { game, favoriteButton ->
            handleFavoriteClick(game, favoriteButton)
        }
        gamesRecyclerView.adapter = adapter

        fetchGames()
    }

    private fun fetchGames() {
        val db = FirebaseFirestore.getInstance()

        db.collection("games")
            .get()
            .addOnSuccessListener { result ->
                val fetchedGamesList = mutableListOf<Game>()
                for (document in result) {
                    val game = document.toObject(Game::class.java)
                    fetchedGamesList.add(game)
                }
                gamesList.clear()
                gamesList.addAll(fetchedGamesList)
                adapter.notifyDataSetChanged()
                fetchFavorites() // Call fetchFavorites after games are fetched
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Failed to fetch games", exception)
            }
    }

    private fun fetchFavorites() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance("https://fps-game-tracker-default-rtdb.europe-west1.firebasedatabase.app/").reference

        if (userId != null) {
            val favoritesRef = database.child("users").child(userId).child("favorites")

            favoritesRef.get().addOnSuccessListener { snapshot ->
                val favoriteGameIds = snapshot.children.map { it.key }
                Log.d("HomeActivity", "Favorite game ids: $favoriteGameIds")

                gamesList.forEach { game ->
                    game.isFavorite = favoriteGameIds.contains(game.name)
                }

                adapter.notifyDataSetChanged()
                showToast("Fetched favorites")
            }.addOnFailureListener { exception ->
                showToast("Failed to fetch favorites")
                Log.e("HomeActivity", "Failed to fetch favorites", exception)
            }
        } else {
            showToast("User is not authenticated")
        }
    }

    private fun handleFavoriteClick(game: Game, favoriteButton: ImageButton) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            if (game.isFavorite) {
                removeGameFromFavorites(userId, game.name)
                game.isFavorite = false
                favoriteButton.setImageResource(R.drawable.favorite_unfilled)
                showToast("Removed from favorites")
            } else {
                addGameToFavorites(userId, game.name)
                game.isFavorite = true
                favoriteButton.setImageResource(R.drawable.favorite_filled)
                showToast("Added to favorites")
            }
        } else {
            showToast("User is not authenticated")
        }

        adapter.notifyItemChanged(gamesList.indexOf(game))
    }

    private fun addGameToFavorites(userId: String, gameName: String) {
        val database = FirebaseDatabase.getInstance("https://fps-game-tracker-default-rtdb.europe-west1.firebasedatabase.app/").reference
        val favoriteRef = database.child("users").child(userId).child("favorites").child(gameName)
        Log.d("HomeActivity", "FavoriteRef: $favoriteRef")

        Log.d("HomeActivity", "Adding game to favorites: $gameName for user: $userId")
        favoriteRef.setValue(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Game added to favorites")
                Log.d("HomeActivity", "Game added to favorites: $gameName")
            } else {
                showToast("Failed to add game to favorites")
                Log.e("HomeActivity", "Failed to add game to favorites", task.exception)
            }
        }
    }

    private fun removeGameFromFavorites(userId: String, gameName: String) {
        val database = FirebaseDatabase.getInstance("https://fps-game-tracker-default-rtdb.europe-west1.firebasedatabase.app/").reference
        val favoriteRef = database.child("users").child(userId).child("favorites").child(gameName)

        Log.d("HomeActivity", "Removing game from favorites: $gameName for user: $userId")
        favoriteRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Game removed from favorites")
                Log.d("HomeActivity", "Game removed from favorites: $gameName")
            } else {
                showToast("Failed to remove game from favorites")
                Log.e("HomeActivity", "Failed to remove game from favorites", task.exception)
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}