package org.yasin.federationofconsciouscitizensmindmapgraph

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.yasin.federationofconsciouscitizensmindmapgraph.databinding.FragmentCardViewScrollingBinding
import org.yasin.federationofconsciouscitizensmindmapgraph.databinding.FragmentSecondBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class CardViewScrollingFragment : Fragment() {

    private var _binding: FragmentCardViewScrollingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

   /* override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_view_scrolling, container, false)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardViewScrollingBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_cardViewScrollingFragment_to_FirstFragment)
        }*/

        val args: CardViewScrollingFragmentArgs by navArgs()
        val myArgument = args.myArgument2


        try {// Обращаемся к textView прямо здесь
            val textView = binding.text
            textView.text = myArgument
            //showText(requireContext(), textView, myArgument)
        } catch (e: Exception) {
            e.localizedMessage?.let { MyApp.log(it) }
        }

    }

    //
    private fun showText(context: Context, textView: TextView, text: String) {
        try {

            textView.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}