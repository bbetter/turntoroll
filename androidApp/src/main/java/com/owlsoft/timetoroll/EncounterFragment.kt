package com.owlsoft.timetoroll

import android.app.ProgressDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.usecases.CreateEncounterResult
import com.owlsoft.shared.viewmodel.EncounterViewModel
import com.owlsoft.timetoroll.databinding.EncounterFragmentBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterFragment : Fragment(R.layout.encounter_fragment) {

    private val progressDialog by lazy { ProgressDialog(requireContext()).apply {
        setTitle("Processing")
    }}

    lateinit var binding: EncounterFragmentBinding

    private val adapter = EncounterParticipantsEditAdapter(
        onParticipantDelete = this::onParticipantDelete
    )

    private val code by lazy { arguments?.getString("code") }

    private val viewModel: EncounterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        code?.let {
            viewModel.requestParticipants(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EncounterFragmentBinding.bind(view)

        with(binding) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            setupView()
            setupSubscription()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.encounter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_menu_item) {
            if (code != null) {
                viewModel.updateEncounter(
                    code!!,
                    onSuccess = { findNavController().popBackStack() },
                    onError = { showError(it) }
                )
            } else {
                lifecycleScope.launchWhenResumed {
                    viewModel.createEncounter().collect {
                        when (it) {
                            is CreateEncounterResult.Loading -> {
                                item.isEnabled = false
                                progressDialog.show()
                            }
                            is CreateEncounterResult.Success -> {
                                item.isEnabled = true
                                progressDialog.hide()
                                findNavController().goToEncounter(it.code)
                            }
                            is CreateEncounterResult.Error -> {
                                item.isEnabled = true
                                progressDialog.hide()
                                showError(it.message)
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(message)
            .setPositiveButton(R.string.error_confirmation_action_title) { d, _ ->
                d.dismiss()
            }
            .create()
            .show()
    }

    private fun EncounterFragmentBinding.setupView() {

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        participantsList.adapter = adapter
        participantsList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        rollDiceButton.setOnClickListener {
            val initiative = viewModel.rollInitiative()
            initiativeEditText.setText(initiative.toString())
        }

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val initiative = initiativeEditText.text.toString().toIntOrNull() ?: 0
            val dexterity = dexterityEditText.text.toString().toIntOrNull() ?: 0

            try {
                viewModel.addParticipant(name, initiative, dexterity)
                nameEditText.text.clear()
                initiativeEditText.text.clear()
                dexterityEditText.text.clear()
            }
            catch (ex: Exception){
                showError(ex.message ?: "Unknown error")
            }
        }
    }

    private fun onParticipantDelete(participant: Participant) {
        viewModel.removeParticiapant(participant)
    }

    private fun setupSubscription() {
        viewModel.data.watch {
            if (it.isEmpty()) {
                binding.noParticipantsBanner.visibility = View.VISIBLE
                binding.participantsList.visibility = View.GONE
            } else {
                binding.noParticipantsBanner.visibility = View.GONE
                binding.participantsList.visibility = View.VISIBLE
            }
            adapter.updateParticipants(it)
        }
    }

    private fun NavController.goToEncounter(encounterCode: String) {
        navigate(
            R.id.action_encounterFragment_to_encounterSessionFragment,
            bundleOf("code" to encounterCode)
        )
    }

}