package dpr.svich.natureprophet

import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dpr.svich.natureprophet.repository.Params
import dpr.svich.natureprophet.viewmodel.CurrentStateViewModel
import dpr.svich.natureprophet.viewmodel.ParamsViewModelFactory
import kotlinx.android.synthetic.main.fragment_current_state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.util.*
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [CurrentStateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentStateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val currentStateViewModel: CurrentStateViewModel by activityViewModels{
        ParamsViewModelFactory((activity?.application as ThisApplication).repository)
    }

    private lateinit var colorAnimation:ValueAnimator
    private lateinit var updateTV:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_current_state, container, false)
        // Init view
        val mHumidityTV = view.findViewById<TextView>(R.id.humidityTV)
        val mAirTempTV = view.findViewById<TextView>(R.id.airTempTV)
        val mGroundTempTV = view.findViewById<TextView>(R.id.groundTempTV)
        updateTV = view.findViewById(R.id.updateTextView)
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        initAnimation()

        fab.setOnClickListener {
                currentStateViewModel.insert(
                    Params(
                        0,
                        Random.nextInt(0, 89),
                        Random.nextInt(0, 100),
                        Random.nextInt(0, 100),
                        System.currentTimeMillis()
                    )
                )
        }
        currentStateViewModel.currentParams.observe(viewLifecycleOwner, Observer { params ->
            params?.let{
                "${params.humidity}%".also { mHumidityTV.text = it }
                "${params.tempAir} °C".also { mAirTempTV.text = it }
                "${params.tempGround} °C".also { mGroundTempTV.text = it }
                colorAnimation.start()
                val date = params.time?.let { it1 -> Date(it1) }
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                updateTV.text = "Обновлено ${sdf.format(date)}"
            }
        })
        return view
    }

    private fun initAnimation(){
        val colorFrom = ContextCompat.getColor(requireContext(), R.color.black)
        val colorTo = ContextCompat.getColor(requireContext(), R.color.teal_700)
        colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo, colorFrom)
        colorAnimation.duration = 1200
        colorAnimation.addUpdateListener {
            updateTV.setTextColor(it.animatedValue as Int)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CurrentStateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentStateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}