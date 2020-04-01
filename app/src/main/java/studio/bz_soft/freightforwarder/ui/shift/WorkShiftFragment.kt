package studio.bz_soft.freightforwarder.ui.shift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import kotlin.coroutines.CoroutineContext

class WorkShiftFragment : Fragment(), CoroutineScope {

    private val logTag = WorkShiftFragment::class.java.simpleName

    private val presenter by inject<WorkShiftPresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_work_shift, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {

        }
    }
}