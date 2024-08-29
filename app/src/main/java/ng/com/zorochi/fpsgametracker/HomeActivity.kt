package ng.com.zorochi.fpsgametracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val gamesRecyclerView = findViewById<RecyclerView>(R.id.gamesRecyclerView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data
        val gamesList = listOf(
            Game("Game 1", "FPS", 4.5f, "https://example.com/image1.jpg"),
            Game("Game 2", "FPS", 4.2f, "https://example.com/image2.jpg"),
            // Add more games here...
        )

        val adapter = GamesAdapter(gamesList)
        gamesRecyclerView.adapter = adapter
    }
}