package com.sumanta.noteappktor.ui.notes

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.Adapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sumanta.noteappktor.R
import com.sumanta.noteappktor.databinding.FragmentAllNotesBinding
import com.sumanta.noteappktor.ui.adapter.NoteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllNoteFragment : Fragment(R.layout.fragment_all_notes) {

    private var _binding: FragmentAllNotesBinding? = null
    private val binding: FragmentAllNotesBinding?
        get() = _binding

    private lateinit var noteAdapter: NoteAdapter
    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllNotesBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding!!.customToolBar)

        binding?.newNoteFab?.setOnClickListener {
            findNavController().navigate(R.id.action_allNoteFragment_to_newNoteFragment)
        }
        setUpRecyclerView()
        subscribeToNotes()
        setUpSwipeLayout()
        noteViewModel.syncNotes()


    }

    private fun setUpRecyclerView() {
        noteAdapter = NoteAdapter()
        binding?.noteRecyclerView?.apply {
            noteAdapter.setOnItemClickListener {
                val action = AllNoteFragmentDirections.actionAllNoteFragmentToNewNoteFragment(it)
                findNavController().navigate(action)
            }
            adapter = noteAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            ItemTouchHelper(itemTouchHelperCallback)
                .attachToRecyclerView(this)
        }
    }

    private fun subscribeToNotes() = lifecycleScope.launch {
        noteViewModel.notes.collect {
            noteAdapter.notes = it.filter { localNote ->
                localNote.noteTitle?.contains(noteViewModel.searchQuery, true) == true ||
                        localNote.description?.contains(noteViewModel.searchQuery, true) == true
            }
        }

    }

    private fun setUpSwipeLayout() {
        binding?.swipeRefeeshLayout?.setOnRefreshListener {
            noteViewModel.syncNotes {
                binding?.swipeRefeeshLayout?.isRefreshing = false
            }
        }
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            noteViewModel.deleteNote(note.noteId)
            Snackbar.make(
                requireView(),
                "Note Deleted",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction(
                    "Undo"
                ) {
                    noteViewModel.undoDelete(note)
                }
                show()
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX / 2,
                dY,
                actionState,
                isCurrentlyActive
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                noteViewModel.searchQuery = ""
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                noteViewModel.searchQuery = ""
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchNotes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchNotes(it)
                }
                return true
            }
        })

    }


    private fun searchNotes(query: String) = lifecycleScope.launch {
        noteViewModel.searchQuery = query
        noteAdapter.notes = noteViewModel.notes.first().filter {
            it.noteTitle?.contains(query, true) == true ||
                    it.description?.contains(query, true) == true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.account -> {
                findNavController().navigate(R.id.action_allNoteFragment_to_userInfoFragment)
            }
        }


        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}