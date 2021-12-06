package com.sumanta.noteappktor.ui.notes

import android.os.Bundle
import android.view.*
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sumanta.noteappktor.R
import com.sumanta.noteappktor.databinding.FragmentAllNotesBinding
import com.sumanta.noteappktor.ui.adapter.NoteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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


    }

    private fun setUpRecyclerView(){
        noteAdapter = NoteAdapter()
        binding?.noteRecyclerView?.apply {
            noteAdapter.setOnItemClickListener {
                val action = AllNoteFragmentDirections.actionAllNoteFragmentToNewNoteFragment(it)
                findNavController().navigate(action)
            }
            adapter = noteAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun subscribeToNotes() = lifecycleScope.launch{
        noteViewModel.notes.collect{
            noteAdapter.notes = it
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