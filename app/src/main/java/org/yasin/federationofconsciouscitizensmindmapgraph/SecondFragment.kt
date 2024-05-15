package org.yasin.federationofconsciouscitizensmindmapgraph

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.yasin.federationofconsciouscitizensmindmapgraph.databinding.FragmentSecondBinding
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_MainFragment)
        }

     /*   @OptIn(DelicateCoroutinesApi::class)
        lifecycleScope.launch(Dispatchers.Main) {
            var textView: TextView? = null
            while (textView == null) {
                delay(1000) // Проверяем каждую секунду
                textView = findViewById<TextView>(R.id.textview_second)
            }
            // Вызываем showLogs при выборе пункта меню "Настройки"
            showLogs(this@MainActivity, textView)
        }*/

        @OptIn(DelicateCoroutinesApi::class)
        lifecycleScope.launch(Dispatchers.Main) {
            var textView: TextView? = null
            while (textView == null) {
                delay(1000) // Проверяем каждую секунду
                textView = binding.textviewSecond
            }
            // Вызываем showLogs
            showLogs(requireContext(), textView)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Функция для чтения логов из файла и отображения их в TextView
    private fun showLogs(context: Context, textView: TextView) {
        try {
            val fis = context.openFileInput("log.txt")
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                //stringBuilder.append(line).append("\n")
                stringBuilder.insert(0, line + "\n")
                line = bufferedReader.readLine()
            }
            textView.text = stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}