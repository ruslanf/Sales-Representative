package studio.bz_soft.freightforwarder.ui.route.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import studio.bz_soft.freightforwarder.R
import kotlin.coroutines.CoroutineContext

class AddStoreFragment : Fragment(), CoroutineScope {

    private val logTag = AddStoreFragment::class.java.simpleName

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_addstore, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {

        }
    }

    companion object {
        fun instance() = AddStoreFragment()
    }
}