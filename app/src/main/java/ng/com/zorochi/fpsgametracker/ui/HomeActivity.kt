@file:Suppress("PackageDirectoryMismatch", "PackageDirectoryMismatch", "PackageDirectoryMismatch")

package ng.com.zorochi.fpsgametracker.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ng.com.zorochi.fpsgametracker.Game
import ng.com.zorochi.fpsgametracker.GamesAdapter
import ng.com.zorochi.fpsgametracker.R
import ng.com.zorochi.fpsgametracker.data.AppDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: GamesAdapter
    private val gamesList = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_home)

        val gamesRecyclerView = findViewById<RecyclerView>(R.id.gamesRecyclerView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = GamesAdapter(gamesList) { game, favoriteButton ->
            handleFavoriteClick(game, favoriteButton)
        }
        gamesRecyclerView.adapter = adapter

        fetchGames()
    }

    private fun fetchGames() {
        val db = AppDatabase.getDatabase(applicationContext)
        val gameDao = db.gameDao()

        gameDao.getAllGames().observe(this, Observer { games ->
            if (games.isNotEmpty()) {
                gamesList.clear()
                gamesList.addAll(games)
                adapter.notifyDataSetChanged()
            } else {
                syncDataWithFirebase()
            }
        })
    }

    private fun syncDataWithFirebase() {
        val firestore = FirebaseFirestore.getInstance()
        val gamesCollection = firestore.collection("games")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance("https://fps-game-tracker-default-rtdb.europe-west1.firebasedatabase.app").reference

        gamesCollection.get().addOnSuccessListener { snapshot ->
            val games = mutableListOf<Game>()
            for (document in snapshot.documents) {
                val game = document.toObject(Game::class.java)
                game?.let { games.add(it) }
            }

            if (userId != null) {
                val favoritesRef = database.child("users").child(userId).child("favorites")

                favoritesRef.get().addOnSuccessListener { snapshot ->
                    val favoriteGameIds = snapshot.children.map { it.key }

                    games.forEach { game ->
                        game.isFavorite = favoriteGameIds.contains(game.name)
                    }

                    GlobalScope.launch {
                        val db = AppDatabase.getDatabase(applicationContext)
                        val gameDao = db.gameDao()
                        for (game in games) {
                            gameDao.insertGame(game)
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("HomeActivity", "Failed to fetch favorites from Realtime Database", exception)
                }
            } else {
                GlobalScope.launch {
                    val db = AppDatabase.getDatabase(applicationContext)
                    val gameDao = db.gameDao()
                    for (game in games) {
                        gameDao.insertGame(game)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("HomeActivity", "Failed to sync data with Firestore", exception)
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

            GlobalScope.launch {
                val db = AppDatabase.getDatabase(applicationContext)
                val gameDao = db.gameDao()
                gameDao.insertGame(game)
            }
        } else {
            showToast("User is not authenticated")
        }

        adapter.notifyItemChanged(gamesList.indexOf(game))
    }

    private fun addGameToFavorites(userId: String, gameName: String) {
        val database = FirebaseDatabase.getInstance("https://fps-game-tracker-default-rtdb.europe-west1.firebasedatabase.app").reference
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
        val database = FirebaseDatabase.getInstance("https://fps-game-tracker-default-rtdb.europe-west1.firebasedatabase.app").reference
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