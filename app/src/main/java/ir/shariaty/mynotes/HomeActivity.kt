package ir.shariaty.mynotes

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(), NotesAdapter.OnItemClickListener {

    private lateinit var logoutButton: Button
    private lateinit var addNoteButton: Button
    private lateinit var deleteNoteButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var notesList: MutableList<Note>
    private lateinit var firestore: FirebaseFirestore
    private var isSelectionMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        logoutButton = findViewById(R.id.logoutButton)
        addNoteButton = findViewById(R.id.addNoteButton)
        deleteNoteButton = findViewById(R.id.deleteNoteButton)
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        notesList = mutableListOf()
        notesAdapter = NotesAdapter(notesList, this)
        notesRecyclerView.adapter = notesAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        loadNotes()
        logoutButton.setOnClickListener {
            mAuth.signOut()
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST_CODE)
        }
        deleteNoteButton.setOnClickListener {
            if (notesAdapter.getSelectedNotes().isEmpty()) {
                Toast.makeText(this, "No notes selected", Toast.LENGTH_SHORT).show()
            } else {
                showDeleteConfirmationDialog()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val note = data?.getParcelableExtra<Note>("note")
            val position = data?.getIntExtra("position", -1)
            if (note != null) {
                if (requestCode == ADD_NOTE_REQUEST_CODE) {
                    notesList.add(note)
                } else if (requestCode == EDIT_NOTE_REQUEST_CODE && position != null && position >= 0) {
                    notesList[position] = note
                }
                notesAdapter.notifyDataSetChanged()
            }
        }
    }