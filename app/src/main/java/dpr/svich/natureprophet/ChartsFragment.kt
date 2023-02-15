package dpr.svich.natureprophet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dpr.svich.natureprophet.repository.Params
import dpr.svich.natureprophet.viewmodel.ChartsViewModel
import dpr.svich.natureprophet.viewmodel.ParamsViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChartsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val chartsViewModel: ChartsViewModel by activityViewModels {
        ParamsViewModelFactory((activity?.application as ThisApplication).repository)
    }

    private lateinit var humidityChart: LineChart
    private lateinit var airTempChart: LineChart
    private lateinit var groundTempChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_charts, container, false)
        humidityChart = view.findViewById(R.id.humidityChart)
        airTempChart = view.findViewById(R.id.tempAirChart)
        groundTempChart = view.findViewById(R.id.tempGroundChart)
        chartsViewModel.paramsList.observe(viewLifecycleOwner, Observer {
            it?.let{
                updateCharts(it)
            }
        })
        return view
    }

    //  Update data inside charts
    private fun updateCharts(list: List<Params>){
        val entriesHumidity = ArrayList<Entry>()
        val entriesAirTemp = ArrayList<Entry>()
        val entriesGroundTemp = ArrayList<Entry>()
        var count = 1
        for(p in list.asReversed()){
            entriesHumidity.add(Entry(count.toFloat(), p.humidity!!.toFloat()))
            entriesAirTemp.add(Entry(count.toFloat(), p.tempAir!!.toFloat()))
            entriesGroundTemp.add(Entry(count.toFloat(), p.tempGround!!.toFloat()))
            count++
        }
        var dataSet = LineDataSet(entriesHumidity, "humidity")
        dataSet.fillDrawable = ContextCompat
            .getDrawable(requireContext(), R.drawable.humidity_gradient)
        dataSet.setDrawFilled(true)
        var lineData = LineData(dataSet)
        humidityChart.data = lineData
        humidityChart.invalidate()

        dataSet = LineDataSet(entriesAirTemp, "Air temperature")
        dataSet.fillDrawable = ContextCompat
            .getDrawable(requireContext(), R.drawable.air_gradient)
        dataSet.setDrawFilled(true)
        lineData = LineData(dataSet)
        airTempChart.data = lineData
        airTempChart.invalidate()

        dataSet = LineDataSet(entriesGroundTemp, "Ground temperature")
        dataSet.fillDrawable = ContextCompat
            .getDrawable(requireContext(), R.drawable.ground_gradient)
        dataSet.setDrawFilled(true)
        lineData = LineData(dataSet)
        groundTempChart.data = lineData
        groundTempChart.invalidate() 
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChartsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChartsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}