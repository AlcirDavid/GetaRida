package com.getaride.android.ui.main

//import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.getaride.android.R
import com.getaride.android.api.ApiEmptyResponse
import com.getaride.android.api.ApiSuccessResponse
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

//    private lateinit var viewModel: MainViewModel

    // Lazy property
    val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.loadUser()

        transactionButton.setOnClickListener { view ->
            val channel = viewModel.authenticate()
//            val channel = viewModel.authenticate(activity!!.getString(R.string.gitHub_token))
//            val channel = viewModel.authenticateUser(
//                    username.text.toString(),
//                    passwordText.text.toString()
//            )

            launch(UI) {
                channel.receive().apply {
                    when(this) {
                        is ApiSuccessResponse -> {
                            Timber.e("TEST ApiSuccessResponse -> $this")
                        }

                        is ApiEmptyResponse -> {
                            Timber.e("TEST ApiEmptyResponse -> $this")
                        }
                    }
                }
                view.findNavController().navigate(R.id.action_mainFragment_to_newServiceAreaFragment)
            }

        }
//        transactionButton.setOnClickListener(
//                Navigation.createNavigateOnClickListener(
////                        R.id.action_mainFragment_to_newServiceAreaFragment, null))
//                        R.id.action_mainFragment_to_moviesFragment, null))

    }

}
