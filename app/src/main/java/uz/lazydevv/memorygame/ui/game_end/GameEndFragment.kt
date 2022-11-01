package uz.lazydevv.memorygame.ui.game_end

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.lazydevv.memorygame.R
import uz.lazydevv.memorygame.databinding.FragmentGameEndBinding

class GameEndFragment : Fragment(R.layout.fragment_game_end) {

    private val binding by viewBinding(FragmentGameEndBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRestart.setOnClickListener { findNavController().popBackStack() }
        }
    }
}