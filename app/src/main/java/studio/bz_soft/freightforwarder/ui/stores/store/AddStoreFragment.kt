package studio.bz_soft.freightforwarder.ui.stores.store

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.dialog_products_range.view.*
import kotlinx.android.synthetic.main.dialog_work_time.view.*
import kotlinx.android.synthetic.main.fragment_add_store.*
import kotlinx.android.synthetic.main.fragment_add_store.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.data.models.db.TradePoint
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.KEY_LATITUDE
import studio.bz_soft.freightforwarder.root.Constants.KEY_LONGITUDE
import studio.bz_soft.freightforwarder.root.Constants.KEY_WORK_STARTED
import studio.bz_soft.freightforwarder.root.drawable
import studio.bz_soft.freightforwarder.root.showError
import studio.bz_soft.freightforwarder.root.showToast
import studio.bz_soft.freightforwarder.root.textWatcher
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class AddStoreFragment : Fragment(), CoroutineScope {

    private val logTag = AddStoreFragment::class.java.simpleName

    private val presenter by inject<AddStorePresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token: String = EMPTY_STRING
    private var ex: Exception? = null

    private var isStorePoint = false
//    private var isTax = false
//    private var isTax1 = false
    private var isActualAddress = false
//    private var isLegalAddress = false
//    private var isPhone = false
//    private var isEmail = false
//    private var isLpr = false
//    private var isDealer = false
    private var isProductsRange = false
//    private var isWorkTime = false

    private val storePointNameWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, nameStoreIV, text.isNotEmpty()) }
        isStorePoint = text.isNotEmpty()
    }
    private val taxNumberWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, taxNumberIV, text.isNotEmpty()) }
//        isTax = text.isNotEmpty()
    }
    private val taxNumber1Watcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, taxNumber_1_IV, text.isNotEmpty()) }
//        isTax1 = text.isNotEmpty()
    }
    private val actualAddressWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, actualAddressIV, text.isNotEmpty()) }
        isActualAddress = text.isNotEmpty()
    }
    private val legalAddressWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, legalAddressIV, text.isNotEmpty()) }
//        isLegalAddress = text.isNotEmpty()
    }
    private val phoneWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, phoneIV, text.isNotEmpty()) }
//        isPhone = text.isNotEmpty()
    }
    private val emailWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, emailIV, text.isNotEmpty()) }
//        isEmail = text.isNotEmpty()
    }
    private val lprWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, lprIV, text.isNotEmpty()) }
//        isLpr = text.isNotEmpty()
    }
    private val dealerWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, dealerIV, text.isNotEmpty()) }
//        isDealer = text.isNotEmpty()
    }
    private val noteWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, noteIV, text.isNotEmpty()) }
//        isDealer = text.isNotEmpty()
    }

    private var storeName: String = EMPTY_STRING
    private var taxType: String = EMPTY_STRING
    private var taxNumber: String = EMPTY_STRING
    private var taxNumber1: String = EMPTY_STRING
    private var actualAddress: String = EMPTY_STRING
    private var legalAddress: String = EMPTY_STRING
    private var phone: String = EMPTY_STRING
    private var email: String = EMPTY_STRING
    private var lpr: String = EMPTY_STRING
    private var payment: String = EMPTY_STRING
    private var product: String = EMPTY_STRING
    private var marketType: String = EMPTY_STRING
    private var companyType: String = EMPTY_STRING
    private var workTime: String = EMPTY_STRING
    private var dealer: String = EMPTY_STRING
    private var note: String = EMPTY_STRING
    private var photoOutside: String = EMPTY_STRING
    private var photoInside: String = EMPTY_STRING
    private var photoGoods: String = EMPTY_STRING
    private var photoCorner: String = EMPTY_STRING

    private val types: Array<String> = arrayOf("ИП", "ООО", "ПАО", "ЗАО")
    private val payments: Array<String> = arrayOf("Безнал. с НДС", "Безнал. без НДС", "Наличная")
    private val assortment: Array<String> = arrayOf(
        "Семена и посадочный материал",
        "Товары для дома", "Товары для сада", "Товары для животных",
        "Товары для праздника", "Товары для отдыха"
    )
    private val tradePointSize: Array<String> = arrayOf(
        "Палатка, отдел либо магазин до 20 кв.м.",
        "Магазин от 20 до 100 кв.м.", "Магазин от 100 до 300 кв.м.", "Магазин свыше 300 кв.м."
    )
    private val companyTypeArray: Array<String> = arrayOf(
        "Единичный розничный магазин", "Несколько розничных магазинов у одного собственника",
        "Оптово-розничная компания региональная", "Оптово-розничная федеральная компания ",
        "Сеть магазинов региональная",
        "Сеть магазинов фереральная"
    )

    private var isStoreSaved: Boolean = false
    private var isWorkStarted: Boolean = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getBoolean(KEY_WORK_STARTED)?.let { isWorkStarted = it }
        arguments?.getDouble(KEY_LATITUDE)?.let { latitude = it }
        arguments?.getDouble(KEY_LONGITUDE)?.let { longitude = it }
        presenter.getUserToken()?.let { token = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_store, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            loadSpinnersInfo(this)

            addTextWatcher(this, nameStoreET, storePointNameWatcher)
            addTextWatcher(this, taxNumberET, taxNumberWatcher)
            addTextWatcher(this, taxNumber_1_ET, taxNumber1Watcher)
            addTextWatcher(this, actualAddressET, actualAddressWatcher)
            addTextWatcher(this, legalAddressET, legalAddressWatcher)
            addTextWatcher(this, phoneET, phoneWatcher)
            addTextWatcher(this, emailET, emailWatcher)
            addTextWatcher(this, lprET, lprWatcher)
            addTextWatcher(this, dealerET, dealerWatcher)
            addTextWatcher(this, noteET, noteWatcher)

            productsRangeTV.setOnClickListener { productsRangeListener(this) }
            workTimeTV.setOnClickListener { workTimeListener(this) }

            addPhotoButton.setOnClickListener { addPhotoListener(this) }
            saveStoreInfoButton.setOnClickListener { saveStoreButtonListener(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.GONE
        isStoreSaved = false
    }

    override fun onPause() {
        super.onPause()
        // TODO Place check for store info saved or not if was started editing fields load from DB into fields
    }

    private fun setTopScrollView(v: View) {
        v.apply {
            scrollView.postDelayed(Runnable {
                scrollView.apply {
                    fullScroll(ScrollView.FOCUS_UP)
                    scrollTo(0, 0)
                }
            }, 100L)
        }
    }

    private fun loadSpinnersInfo(v: View) {
        v.apply {
            typeSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, types)
            typeSpinner.prompt = "Организационно-правовая форма"
            paymentsSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, payments)
            marketTypeSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, tradePointSize)
            companyTypeSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, companyTypeArray)
        }
    }

    private fun addTextWatcher(v: View, editText: EditText, textWatcher: TextWatcher) {
        v.apply {
            editText.apply {
                requestFocus()
                addTextChangedListener(textWatcher)
            }
        }
    }

    private fun setCorrectIcon(v: View, image: ImageView, isCorrect: Boolean) {
        v.apply {
            image.setImageDrawable(
                drawable(this,
                    when (isCorrect) {
                        true -> R.drawable.ic_correct
                        false -> R.drawable.ic_incorrect
                    }
                )
            )
        }
    }

    private fun addPhotoListener(v: View) {
        v.apply {
            findNavController().navigate(R.id.imageFragment)
        }
    }

    private fun saveStoreButtonListener(v: View) {
        v.apply {
            saveStoreInfoButton.isEnabled = false
            when (isStorePoint) {
                true -> when (isActualAddress) {
                    true -> when (isProductsRange) {
                        true -> {
                            updateImageUrl()
                            setFields(this)
                            saveStoreIntoServer(this)
                            saveStoreIntoDB(this)
//                removeImageUrl()
                            findNavController().navigateUp()
                        }
                        false -> showToast(this, getString(R.string.fragment_add_store_not_filled_error_message_assortment))
                    }
                    false -> showToast(this, getString(R.string.fragment_add_store_not_filled_error_message_address))
                }
                false -> showToast(this, getString(R.string.fragment_add_store_not_filled_error_message_point))
            }
            saveStoreInfoButton.isEnabled = true
        }
    }

    private fun updateImageUrl() {
        presenter.getImageOutside()?.let { photoOutside = it }
        presenter.getImageInside()?.let { photoInside = it }
        presenter.getImageAssortment()?.let { photoGoods = it }
        presenter.getImageCorner()?.let { photoCorner = it }
    }

    private fun removeImageUrl() {
        presenter.deleteImageOutside()
        presenter.deleteImageInside()
        presenter.deleteImageAssortment()
        presenter.deleteImageCorner()
    }

    private fun setFields(v: View) {
        v.apply {
            storeName = nameStoreET.text.toString()
            taxType = typeSpinner.selectedItem.toString()
            taxNumber = taxNumberET.text.toString()
            taxNumber1 = taxNumber_1_ET.text.toString()
            actualAddress = actualAddressET.text.toString()
            legalAddress = legalAddressET.text.toString()
            phone = phoneET.text.toString()
            email = emailET.text.toString()
            lpr = lprET.text.toString()
            payment = paymentsSpinner.selectedItem.toString()
            product = productsRangeTV.text.toString()
            marketType = marketTypeSpinner.selectedItem.toString()
            companyType = companyTypeSpinner.selectedItem.toString()
            workTime = workTimeTV.text.toString()
            dealer = dealerET.text.toString()
            note = noteET.text.toString()
        }
    }

    private fun saveStoreIntoServer(v: View) {
        v.apply {
            ex = null
            progressBar.visibility = View.VISIBLE
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.saveSalesPoint(token, StorePointModel(storeName, taxType,
                        taxNumber, taxNumber1, actualAddress, legalAddress, phone, email, lpr,
                        payment, product, marketType, companyType, workTime, dealer, note,
                        latitude, longitude, photoOutside, photoInside, photoGoods, photoCorner))) {
                        is Right -> {  }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(context, it, R.string.fragment_add_store_update_error_message, logTag)
                } ?: run {

                }
            }
        }
    }

    private fun saveStoreIntoDB(v: View) {
        v.apply {
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    presenter.saveSalesPointToDB(TradePoint(0, storeName, taxType, taxNumber,
                        taxNumber1, actualAddress, legalAddress, phone, email, lpr,
                        payment, product, marketType, companyType, workTime, dealer, note,
                        latitude, longitude, photoOutside, photoInside, photoGoods, photoCorner
                    ))
                }
                request.await()
            }
        }
    }

    private fun productsRangeListener(v: View) {
        v.apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_products_range, null)
            val alertDialog = AlertDialog.Builder(context).create()
            with(alertDialog) {
                setView(dialogView)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                var g0 = EMPTY_STRING
                var g1 = EMPTY_STRING
                var g2 = EMPTY_STRING
                var g3 = EMPTY_STRING
                var g4 = EMPTY_STRING
                var g5 = EMPTY_STRING
                dialogView.goods0CB.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) g0 = assortment[0].plus(",")
                }
                dialogView.goods1CB.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) g1 = assortment[1].plus(",")
                }
                dialogView.goods2CB.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) g2 = assortment[2].plus(",")
                }
                dialogView.goods3CB.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) g3 = assortment[3].plus(",")
                }
                dialogView.goods4CB.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) g4 = assortment[4].plus(",")
                }
                dialogView.goods5CB.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) g5 = assortment[5]
                }
                dialogView.setProductsRangeButton.setOnClickListener {
                    productsRangeButtonListener(this@apply, this,
                    g0, g1, g2, g3, g4, g5)
                }
                show()
            }
        }
    }

    private fun productsRangeButtonListener(v: View, alertDialog: AlertDialog?, g0: String, g1: String,
                                            g2: String, g3: String, g4: String, g5: String) {
        v.apply {
            alertDialog?.dismiss()
            val products = "$g0 $g1 $g2 $g3 $g4 $g5"
            productsRangeTV.text = if (products.isNotBlank()) products else getString(R.string.fragment_add_store_products_range)
            productsRangeTV.isFocusable = true
            setCorrectIcon(this, productsIV, products.isNotBlank())
            isProductsRange = products.isNotBlank()
        }
    }

    private fun workTimeListener(v: View) {
        v.apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_work_time, null)
            val alertDialog = AlertDialog.Builder(context).create()
            with(alertDialog) {
                setView(dialogView)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogView.startHourNP.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 0
                    maxValue = 23
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.startMinNP.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 0
                    maxValue = 59
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.endHourNP.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 0
                    maxValue = 23
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.endMinNP.apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    minValue = 0
                    maxValue = 59
                    wrapSelectorWheel = false
                    isClickable = false
                }
                dialogView.setWorkTimeButton.setOnClickListener {
                    setWorkTimeListener(this@apply, this,
                        dialogView.startHourNP.value,
                        dialogView.startMinNP.value,
                        dialogView.endHourNP.value,
                        dialogView.endMinNP.value)
                }
                show()
            }
        }
    }

    private fun setWorkTimeListener(v: View, alertDialog: AlertDialog?, startH: Int, startM: Int, endH: Int, endM: Int) {
        v.apply {
            alertDialog?.dismiss()
            val workTime = "$startH:$startM $endH:$endM"
            workTimeTV.text = workTime
            workTimeTV.isFocusable = true
//            isWorkTime = true
        }
    }

    companion object {
        fun instance(isWorkStarted: Boolean, latitude: Double, longitude: Double): AddStoreFragment = AddStoreFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_WORK_STARTED, isWorkStarted)
                putDouble(KEY_LATITUDE, latitude)
                putDouble(KEY_LONGITUDE, longitude)
            }
        }
    }
}