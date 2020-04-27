package studio.bz_soft.freightforwarder.ui.stores.edit

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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.dialog_products_range.view.*
import kotlinx.android.synthetic.main.dialog_work_time.view.*
import kotlinx.android.synthetic.main.fragment_edit_store.*
import kotlinx.android.synthetic.main.fragment_edit_store.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.http.Left
import studio.bz_soft.freightforwarder.data.http.Right
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.root.Constants.EMPTY_STRING
import studio.bz_soft.freightforwarder.root.Constants.KEY_TOKEN
import studio.bz_soft.freightforwarder.root.Constants.KEY_TRADE_POINT_ID
import studio.bz_soft.freightforwarder.root.drawable
import studio.bz_soft.freightforwarder.root.showError
import studio.bz_soft.freightforwarder.root.textWatcher
import studio.bz_soft.freightforwarder.root.value
import studio.bz_soft.freightforwarder.ui.root.RootActivity
import kotlin.coroutines.CoroutineContext

class EditStoreFragment : Fragment(), CoroutineScope {

    private val logTag = EditStoreFragment::class.java.simpleName

    private val presenter by inject<EditStorePresenter>()

    private var job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    private var token = EMPTY_STRING
    private var tpId = 0
    private var storePointModel: StorePointModel? = null
    private var ex: Exception? = null

    private var isStorePoint = false
    private var isTax = false
    private var isTax1 = false
    private var isActualAddress = false
    private var isLegalAddress = false
    private var isPhone = false
    private var isEmail = false
    private var isLpr = false
    private var isDealer = false
    private var isProductsRange = false
    private var isWorkTime = false

    private val storePointNameWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, nameStoreIV, text.isNotEmpty()) }
        isStorePoint = text.isNotEmpty()
    }
    private val taxNumberWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, taxNumberIV, text.isNotEmpty()) }
        isTax = text.isNotEmpty()
    }
    private val taxNumber1Watcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, taxNumber_1_IV, text.isNotEmpty()) }
        isTax1 = text.isNotEmpty()
    }
    private val actualAddressWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, actualAddressIV, text.isNotEmpty()) }
        isActualAddress = text.isNotEmpty()
    }
    private val legalAddressWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, legalAddressIV, text.isNotEmpty()) }
        isLegalAddress = text.isNotEmpty()
    }
    private val phoneWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, phoneIV, text.isNotEmpty()) }
        isPhone = text.isNotEmpty()
    }
    private val emailWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, emailIV, text.isNotEmpty()) }
        isEmail = text.isNotEmpty()
    }
    private val lprWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, lprIV, text.isNotEmpty()) }
        isLpr = text.isNotEmpty()
    }
    private val dealerWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, dealerIV, text.isNotEmpty()) }
        isDealer = text.isNotEmpty()
    }

    private var storeName: String = EMPTY_STRING
    private var taxType: String = EMPTY_STRING
    private var taxNumberV: String = EMPTY_STRING
    private var taxNumber1V: String = EMPTY_STRING
    private var actualAddress: String = EMPTY_STRING
    private var legalAddress: String = EMPTY_STRING
    private var phoneV: String = EMPTY_STRING
    private var emailV: String = EMPTY_STRING
    private var lprV: String = EMPTY_STRING
    private var paymentV: String = EMPTY_STRING
    private var productV: String = EMPTY_STRING
    private var marketTypeV: String = EMPTY_STRING
    private var companyTypeV: String = EMPTY_STRING
    private var workTimeV: String = EMPTY_STRING
    private var dealerV: String = EMPTY_STRING
    private var noteV: String = EMPTY_STRING
    private var photoOutsideV: String = EMPTY_STRING
    private var photoInsideV: String = EMPTY_STRING
    private var photoGoodsV: String = EMPTY_STRING
    private var photoCornerV: String = EMPTY_STRING
    private var latitudeV = 0.0
    private var longitudeV = 0.0


    private val types: Array<String> = arrayOf("ИНН", "ООО", "ПАО", "ЗАО")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(KEY_TRADE_POINT_ID)?.let { tpId = it }
        arguments?.getString(KEY_TOKEN)?.let { token = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.fragment_edit_store, container, false)

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

            loadTradePoint(this)
            productsRangeTV.setOnClickListener { productsRangeListener(this) }
            workTimeTV.setOnClickListener { workTimeListener(this) }

            addPhotoButton.setOnClickListener { addPhotoListener(this) }
            saveStoreInfoButton.setOnClickListener { saveStoreButtonListener(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).mainBottomNavigationMenu.visibility = View.GONE
    }

    private fun loadSpinnersInfo(v: View) {
        v.apply {
            typeSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, types)
            paymentsSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, payments)
            marketTypeSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, tradePointSize)
            companyTypeSpinner.adapter = ArrayAdapter(context, R.layout.spinner_item_start, companyTypeArray)
        }
    }

    private fun loadTradePoint(v: View) {
        v.apply {
            progressBar.visibility = View.VISIBLE
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.getTradePoint(token, tpId)) {
                        is Right -> { storePointModel = r.value }
                        is Left -> { ex = r.value }
                    }
                }
                request.await()
                progressBar.visibility = View.GONE
                ex?.let {
                    showError(context, it, R.string.fragment_store_error_load_trade_point, logTag)
                } ?: run {
                    storePointModel?.let { fillTradePoint(this@apply, it) }
                }
            }
        }
    }

    private fun fillTradePoint(v: View, model: StorePointModel) {
        v.apply {
            model.apply {
                storePoint?.let {
                    storeName = it
                    nameStoreET.value = it
                }
                type?.let {
                    taxType = it
                    types.forEachIndexed { index, t ->
                        if (t == it) typeSpinner.setSelection(index)
                    }
                }
                taxNumber?.let {
                    taxNumberV = it
                    taxNumberET.value = it
                }
                taxNumber_1?.let {
                    taxNumber1V = it
                    taxNumber_1_ET.value = it
                }
                addressActual?.let {
                    actualAddress = it
                    actualAddressET.value = it
                }
                addressLegal?.let {
                    legalAddress = it
                    legalAddressET.value = it
                }
                phone?.let {
                    phoneV = it
                    phoneET.value = it
                }
                email?.let {
                    emailV = it
                    emailET.value = it
                }
                lprName?.let {
                    lprV = it
                    lprET.value = it
                }
                paymentType?.let {
                    paymentV = it
                    payments.forEachIndexed { index, t ->
                        if (t == it) paymentsSpinner.setSelection(index)
                    }
                }
                productsRange?.let {
                    productV = it
                    productsRangeTV.text = it
                    setCorrectIcon(v, productsIV, it.isNotBlank())
                }
                marketType?.let {
                    marketTypeV = it
                    tradePointSize.forEachIndexed { index, t ->
                        if (t == it) marketTypeSpinner.setSelection(index)
                    }
                }
                companyType?.let {
                    companyTypeV = it
                    companyTypeArray.forEachIndexed { index, t ->
                        if (t == it) companyTypeSpinner.setSelection(index)
                    }
                }
                workTime?.let {
                    workTimeV = it
                    workTimeTV.text = it
                }
                dealer?.let {
                    dealerV = it
                    dealerET.value = it
                }
                latitude?.let { latitudeV = it }
                longitude?.let { longitudeV = it }
                photoOutside?.let { photoOutsideV = it }
                photoInside?.let { photoInsideV = it }
                photoGoods?.let { photoGoodsV = it }
                photoCorner?.let { photoCornerV = it }
            }
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
//            if (isStorePoint && isTax && isTax1 && isActualAddress &&
//                isLegalAddress && isPhone && isEmail && isLpr && isDealer && isProductsRange &&
//                isWorkTime) {
                updateImageUrl()
                setFields(this)
                updateStoreIntoServer(this)
//                removeImageUrl()
                findNavController().navigateUp()
//            } else showToast(this, getString(R.string.fragment_add_store_not_filled_error_message))
            saveStoreInfoButton.isEnabled = true
        }
    }

    private fun updateImageUrl() {
        presenter.getImageOutside()?.let { photoOutsideV = it }
        presenter.getImageInside()?.let { photoInsideV = it }
        presenter.getImageAssortment()?.let { photoGoodsV = it }
        presenter.getImageCorner()?.let { photoCornerV = it }
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
            taxNumberV = taxNumberET.text.toString()
            taxNumber1V = taxNumber_1_ET.text.toString()
            actualAddress = actualAddressET.text.toString()
            legalAddress = legalAddressET.text.toString()
            phoneV = phoneET.text.toString()
            emailV = emailET.text.toString()
            lprV = lprET.text.toString()
            paymentV = paymentsSpinner.selectedItem.toString()
            marketTypeV = marketTypeSpinner.selectedItem.toString()
            companyTypeV = companyTypeSpinner.selectedItem.toString()
            dealerV = dealerET.text.toString()
            noteV = noteET.text.toString()
            productV = productsRangeTV.text.toString()
            workTimeV = workTimeTV.text.toString()
        }
    }

    private fun updateStoreIntoServer(v: View) {
        v.apply {
            ex = null
            progressBar.visibility = View.VISIBLE
            launch {
                val request = async(SupervisorJob(job) + Dispatchers.IO) {
                    when (val r = presenter.updateTradePoint(token, tpId, StorePointModel(storeName, taxType,
                        taxNumberV, taxNumber1V, actualAddress, legalAddress, phoneV, emailV, lprV,
                        paymentV, productV, marketTypeV, companyTypeV, workTimeV, dealerV, noteV,
                        latitudeV, longitudeV, photoOutsideV, photoInsideV, photoGoodsV, photoCornerV))) {
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
            isWorkTime = true
        }
    }

    companion object {
        fun instance(id: Int): EditStoreFragment = EditStoreFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_TRADE_POINT_ID, id)
            }
        }
    }
}