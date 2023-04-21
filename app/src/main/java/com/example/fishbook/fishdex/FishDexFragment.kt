package com.example.fishbook.fishdex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fishbook.fishdex.FishDexFragmentDirections
import com.example.fishbook.databinding.FragmentFishDexBinding
import kotlinx.coroutines.launch

private const val TAG = "FishDexFragment"

class FishDexFragment : Fragment() {

    private var _binding: FragmentFishDexBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val fishDexViewModel: FishDexViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFishDexBinding.inflate(inflater, container, false)

        binding.fishRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fishDexViewModel.fishSpecies.collect { fishSpecies ->
                    binding.fishRecyclerView.adapter =
                        FishListAdapter(fishSpecies) { fishId ->
                            findNavController().navigate(
                                FishDexFragmentDirections.showSpecies(fishId)
                            )
                        }
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
