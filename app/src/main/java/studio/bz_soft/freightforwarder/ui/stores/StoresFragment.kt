package studio.bz_soft.freightforwarder.ui.stores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_stores.view.*
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.root.formattedDate
import studio.bz_soft.freightforwarder.root.getCurrentDT
import studio.bz_soft.freightforwarder.root.parseDate

class StoresFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_stores, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            getCurrentDate(this)

            addStoreButton.setOnClickListener { storeButtonListener(this) }
        }
    }

    private fun storeButtonListener(v: View) {
        v.apply {
            findNavController().navigate(R.id.addStoreFragment)
        }
    }

    private fun getCurrentDate(v: View) {
        v.apply {
            dateTV.text = currentDate()
        }
    }

    private fun currentDate(): String =
        formattedDate(parseDate(getCurrentDT()))
}