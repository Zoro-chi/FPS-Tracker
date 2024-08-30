package ng.com.zorochi.fpsgametracker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: GamesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val gamesRecyclerView = findViewById<RecyclerView>(R.id.gamesRecyclerView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list
        adapter = GamesAdapter(emptyList())
        gamesRecyclerView.adapter = adapter

        // Fetch data from Firestore
        val db = FirebaseFirestore.getInstance()

        db.collection("games")
            .get()
            .addOnSuccessListener { result ->
                val gamesList = mutableListOf<Game>()
                for (document in result) {
                    val game = document.toObject(Game::class.java)
                    gamesList.add(game)
                }
                // Pass gamesList to your adapter
                adapter.submitList(gamesList)
            }
            .addOnFailureListener { exception ->
                Log.w("HomeActivity", "Error getting documents.", exception)
            }
    }
}