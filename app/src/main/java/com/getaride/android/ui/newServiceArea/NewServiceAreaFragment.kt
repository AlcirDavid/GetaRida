package com.getaride.android.ui.newServiceArea

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.getaride.android.R
import kotlinx.android.synthetic.main.newservicearea_fragment.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext


class NewServiceAreaFragment : Fragment() {

    companion object {
        fun newInstance() = NewServiceAreaFragment()
        const val TAG = "NewServiceAreaFragment"
    }

    // Lazy property
    val viewModel by viewModel<NewServiceAreaViewModel>()

    private val dataProvider = DataProvider()
//    private val job: AndroidJob = AndroidJob(lifecycle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.newservicearea_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
        button.setOnClickListener { loadData() }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        Log.d("TEST", "onDestroy")
    }

//    private fun loadData() = launch(UI, parent = job) {
    private fun loadData() = launch(UI) {
        showLoading() // ui thread

//        val result = dataProvider.loadData() // non ui thread, suspend until finished
        val result = viewModel.loadUser() // non ui thread, suspend until finished

        showText("Done!") // ui thread
//        showText(result) // ui thread
        hideLoading() // ui thread
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showText(data: String) {
        textView.text = data
    }

    class DataProvider(private val context: CoroutineContext = CommonPool) {

        suspend fun loadData(): String = withContext(context) {
            delay(2, TimeUnit.SECONDS) // imitate long running operation
            "Data is available: ${Random().nextInt()}"
        }
    }
}
