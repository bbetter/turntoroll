package com.owlsoft.turntoroll.encounter

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.owlsoft.shared.model.Character
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterDetailsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterDetailsFragment : Fragment(R.layout.encounter_details_fragment) {

    lateinit var binding: EncounterDetailsFragmentBinding

    private val adapter = EncounterParticipantsAdapter()
    private val viewModel: EncounterDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EncounterDetailsFragmentBinding.bind(view)

        with(binding) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            setupView()
            setupSubscription()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.encounter_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.encounterDetailsActionButton) {
            lifecycleScope.launchWhenResumed {
                val code = viewModel.createEncounter()
                findNavController().navigate(
                    R.id.action_encounterDetailsFragment_to_encounterFragment,
                    bundleOf(
                        "code" to code
                    )
                )
            }
        }
        return true
    }

    private fun EncounterDetailsFragmentBinding.setupView() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
        }

        participantsList.adapter = adapter

        participantsList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        addParticipantButton.setOnClickListener {
            val encounter = Character(
                "",
                nameEditText.text.toString(),
                initiativeEditText.text.toString().toIntOrNull() ?: 0,
                dexterityEditText.text.toString().toIntOrNull() ?: 0
            )

            viewModel.addParticipant(encounter)
        }
    }

    private fun setupSubscription() {
        viewModel.participantsLiveData.observe(viewLifecycleOwner) {
            adapter.updateParticipants(it)
        }
    }
}