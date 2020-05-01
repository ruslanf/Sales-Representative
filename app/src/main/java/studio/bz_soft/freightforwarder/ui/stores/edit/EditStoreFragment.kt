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
import android.widget.ScrollView
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
    private var isNote = false
    private var isProductsRange = false
    private var isWorkTime = false

    private var g0 = EMPTY_STRING
    private var g1 = EMPTY_STRING
    private var g2 = EMPTY_STRING
    private var g3 = EMPTY_STRING
    private var g4 = EMPTY_STRING
    private var g5 = EMPTY_STRING

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

    private val noteWatcher = textWatcher { text ->
        view?.let { setCorrectIcon(it, noteIV, text.isNotEmpty()) }
        isNote = text.isNotEmpty()
    }

    private var storeName = EMPTY_STRING
    private var taxType = EMPTY_STRING
    private var taxNumberV = EMPTY_STRING
    private var taxNumber1V = EMPTY_STRING
    private var actualAddress = EMPTY_STRING
    private var legalAddress = EMPTY_STRING
    private var phoneV = EMPTY_STRING
    private var emailV = EMPTY_STRING
    private var lprV = EMPTY_STRING
    private var paymentV = EMPTY_STRING
    private var productV = EMPTY_STRING
    private var productList = mutableListOf<Int>()
    private var marketTypeV = EMPTY_STRING
    private var companyTypeV = EMPTY_STRING
    private var workTimeV = EMPTY_STRING
    private var dealerV = EMPTY_STRING
    private var noteV = EMPTY_STRING
    private var photoOutsideV = EMPTY_STRING
    private var photoInsideV = EMPTY_STRING
    private var photoGoodsV = EMPTY_STRING
    private var photoCornerV = EMPTY_STRING
    private var latitudeV = 0.0
    private var longitudeV = 0.0


    private val types = arrayOf("ИП", "ООО", "ПАО", "ЗАО")
    private val payments = arrayOf("Безнал. с НДС", "Безнал. без НДС", "Наличная")
    private val assortment = arrayOf(
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
            setTopScrollView(this)
            fixScroll(this)
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

    private fun fixScroll(v: View) {
        v.apply {
            scrollView.apply {
                descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
                isFocusable = true
                isFocusableInTouchMode = true
                setOnTouchListener { view, _ ->
                    view.requestFocusFromTouch()
                    false
                }
            }
        }
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
                    productList.addAll(it)
                    parseProductRange(it)
                    productV = productsRangeTV.text.toString()
                    setCorrectIcon(v, productsIV, productV.isNotBlank())
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
                note?.let {
                    noteV = it
                    noteET.value = it
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

    private fun parseProductRange(list: List<Int>) {
        list.forEach {
            when (it) {
                1 -> g0 = assortment[0]
                2 -> g1 = assortment[1]
                3 -> g2 = assortment[2]
                4 -> g3 = assortment[3]
                5 -> g4 = assortment[4]
                6 -> g5 = assortment[5]
            }
        }
        val p = "$g0 $g1 $g2 $g3 $g4 $g5"
        productsRangeTV.text = p
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
                    when (val r = presenter.updateTradePoint(token, tpId, StorePointModel(storeName,
                        taxType, taxNumberV, taxNumber1V, actualAddress, latitudeV, longitudeV,
                        legalAddress, phoneV, emailV, lprV, paymentV, productList, marketTypeV,
                        companyTypeV, workTimeV, dealerV, noteV, photoOutsideV, photoInsideV,
                        photoGoodsV, photoCornerV))) {
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
                        true -> R.drawable.ic_correct_new
                        false -> R.drawable.ic_incorrect_new
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
                dialogView.goods0CB.isChecked = g0.isNotBlank()
                dialogView.goods1CB.isChecked = g1.isNotBlank()
                dialogView.goods2CB.isChecked = g2.isNotBlank()
                dialogView.goods3CB.isChecked = g3.isNotBlank()
                dialogView.goods4CB.isChecked = g4.isNotBlank()
                dialogView.goods5CB.isChecked = g5.isNotBlank()
                productList.clear()
                dialogView.goods0CB.setOnCheckedChangeListener { _, isChecked ->
                    g0 = if (isChecked) assortment[0].plus(",") else EMPTY_STRING
                    if (isChecked) productList.add(1)
                }
                dialogView.goods1CB.setOnCheckedChangeListener { _, isChecked ->
                    g1 = if (isChecked) assortment[1].plus(",") else EMPTY_STRING
                    if (isChecked) productList.add(2)
                }
                dialogView.goods2CB.setOnCheckedChangeListener { _, isChecked ->
                    g2 = if (isChecked) assortment[2].plus(",") else EMPTY_STRING
                    if (isChecked) productList.add(3)
                }
                dialogView.goods3CB.setOnCheckedChangeListener { _, isChecked ->
                    g3 = if (isChecked) assortment[3].plus(",") else EMPTY_STRING
                    if (isChecked) productList.add(4)
                }
                dialogView.goods4CB.setOnCheckedChangeListener { _, isChecked ->
                    g4 = if (isChecked) assortment[4].plus(",") else EMPTY_STRING
                    if (isChecked) productList.add(5)
                }
                dialogView.goods5CB.setOnCheckedChangeListener { _, isChecked ->
                    g5 = if (isChecked) assortment[5] else EMPTY_STRING
                    if (isChecked) productList.add(6)
                }
                dialogView.setProductsRangeButton.setOnClickListener {
                    productsRangeButtonListener(this@apply, this)
                }
                show()
            }
        }
    }

    private fun productsRangeButtonListener(v: View, alertDialog: AlertDialog?) {
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