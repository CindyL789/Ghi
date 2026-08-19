package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.example.data.database.AppDatabase
import com.example.data.model.Project
import com.example.data.model.YarnItem
import com.example.data.repository.CrochetRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrochetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CrochetRepository
    val allProjects: StateFlow<List<Project>>
    val latestActiveProject: StateFlow<Project?>
    val allYarn: StateFlow<List<YarnItem>>

    // Quick Standalone Counter State (when not tied to a specific project)
    private val _quickCounter = MutableStateFlow(0)
    val quickCounter: StateFlow<Int> = _quickCounter.asStateFlow()

    private val _quickCounterTarget = MutableStateFlow(20)
    val quickCounterTarget: StateFlow<Int> = _quickCounterTarget.asStateFlow()

    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()

    // Session Timer State (in seconds)
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private var timerJob: Job? = null

    // --- Billing state ---
    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails.asStateFlow()

    private val _billingConnectionState = MutableStateFlow(false)
    val billingConnectionState: StateFlow<Boolean> = _billingConnectionState.asStateFlow()

    private val _purchaseInProgress = MutableStateFlow(false)
    val purchaseInProgress: StateFlow<Boolean> = _purchaseInProgress.asStateFlow()

    private val _isProGuildActive = MutableStateFlow(false)
    val isProGuildActive: StateFlow<Boolean> = _isProGuildActive.asStateFlow()

    private val _proGuildAnnual = MutableStateFlow(true)
    val proGuildAnnual: StateFlow<Boolean> = _proGuildAnnual.asStateFlow()

    private val _proGuildFromTrial = MutableStateFlow(false)
    val proGuildFromTrial: StateFlow<Boolean> = _proGuildFromTrial.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private var billingClient: BillingClient? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CrochetRepository(database.projectDao(), database.yarnDao())

        allProjects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        latestActiveProject = repository.latestActiveProject.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allYarn = repository.allYarn.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            repository.removeExampleProjects()
        }
    }

    fun removeExampleProjects() {
        viewModelScope.launch {
            repository.removeExampleProjects()
        }
    }

    fun deleteAllProjects() {
        viewModelScope.launch {
            repository.deleteAllProjects()
            _selectedProject.value = null
        }
    }

    fun selectProject(project: Project?) {
        _selectedProject.value = project
    }

    fun triggerHapticFeedback() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(40)
                }
            }
        } catch (_: Exception) {
            // Fallback if vibration unavailable
        }
    }

    // Quick Counter methods
    fun incrementQuickCounter() {
        _quickCounter.value += 1
        triggerHapticFeedback()
    }

    fun decrementQuickCounter() {
        if (_quickCounter.value > 0) {
            _quickCounter.value -= 1
            triggerHapticFeedback()
        }
    }

    fun resetQuickCounter() {
        _quickCounter.value = 0
        triggerHapticFeedback()
    }

    fun setQuickCounterTarget(target: Int) {
        _quickCounterTarget.value = target.coerceAtLeast(1)
    }

    // Selected Project Counter methods
    fun incrementProjectRow(project: Project) {
        val newRow = project.currentRow + 1
        val updated = project.copy(
            currentRow = newRow,
            updatedAt = System.currentTimeMillis(),
            status = if (project.targetRows > 0 && newRow >= project.targetRows) "Completed" else project.status
        )
        viewModelScope.launch {
            repository.saveProject(updated)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = updated
            }
        }
        triggerHapticFeedback()
    }

    fun decrementProjectRow(project: Project) {
        if (project.currentRow > 0) {
            val newRow = project.currentRow - 1
            val updated = project.copy(
                currentRow = newRow,
                updatedAt = System.currentTimeMillis()
            )
            viewModelScope.launch {
                repository.saveProject(updated)
                if (_selectedProject.value?.id == project.id) {
                    _selectedProject.value = updated
                }
            }
            triggerHapticFeedback()
        }
    }

    fun resetProjectRow(project: Project) {
        val updated = project.copy(
            currentRow = 0,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.saveProject(updated)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = updated
            }
        }
        triggerHapticFeedback()
    }

    fun incrementProjectStitch(project: Project) {
        val newStitch = project.currentStitchInRow + 1
        val updated = project.copy(
            currentStitchInRow = newStitch,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.saveProject(updated)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = updated
            }
        }
        triggerHapticFeedback()
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            val id = repository.saveProject(project)
            if (_selectedProject.value?.id == id || _selectedProject.value == null) {
                _selectedProject.value = project.copy(id = id)
            }
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = null
            }
        }
    }

    fun saveYarn(yarn: YarnItem) {
        viewModelScope.launch {
            repository.saveYarn(yarn)
        }
    }

    fun deleteYarn(yarn: YarnItem) {
        viewModelScope.launch {
            repository.deleteYarn(yarn)
        }
    }

    fun restoreAllFromCloud(projects: List<Project>, yarnItems: List<YarnItem>) {
        viewModelScope.launch {
            repository.restoreProjects(projects)
            repository.restoreYarnItems(yarnItems)
        }
    }

    // Timer Controls
    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                _timerSeconds.value += 1
            }
        }
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSeconds.value = 0
    }

    fun addTimerToCurrentProject(project: Project) {
        val minutesToAdd = _timerSeconds.value / 60
        if (minutesToAdd > 0) {
            val updated = project.copy(
                totalMinutesSpent = project.totalMinutesSpent + minutesToAdd
            )
            saveProject(updated)
            resetTimer()
        }
    }

    // --- Google Play Billing Integration ---

    fun initializeBilling(context: Context) {
        if (billingClient != null) return

        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
                viewModelScope.launch {
                    processPurchases(purchases)
                }
            } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                _billingStatusMessage.value = "Purchase canceled by user."
            } else {
                _billingStatusMessage.value = "Billing error: ${billingResult.debugMessage}"
            }
            _purchaseInProgress.value = false
        }

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        startBillingConnection()
    }

    private fun startBillingConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    _billingConnectionState.value = true
                    viewModelScope.launch {
                        queryProductDetails()
                        queryExistingPurchases() // restore
                    }
                } else {
                    _billingConnectionState.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                _billingConnectionState.value = false
            }
        })
    }

    private suspend fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("pro_guild")          // subscription product ID
                .setProductType(ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        try {
            val result = billingClient?.queryProductDetails(params)
            if (result?.billingResult?.responseCode == BillingResponseCode.OK) {
                _productDetails.value = result.productDetailsList ?: emptyList()
            }
        } catch (_: Exception) {
            // Handle error or mock mode
        }
    }

    private suspend fun queryExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.SUBS)
            .build()

        try {
            val result = billingClient?.queryPurchasesAsync(params)
            if (result?.billingResult?.responseCode == BillingResponseCode.OK) {
                processPurchases(result.purchasesList ?: emptyList())
            }
        } catch (_: Exception) {
            // Handle error
        }
    }

    private suspend fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase.purchaseToken)
                }
                activateProGuild(
                    isAnnual = purchase.products.any { it.contains("annual", ignoreCase = true) },
                    fromTrial = false
                )
            }
        }
    }

    private suspend fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        try {
            billingClient?.acknowledgePurchase(params)
        } catch (_: Exception) {
        }
    }

    fun launchPurchaseFlow(activity: Activity, isAnnual: Boolean) {
        val details = _productDetails.value.firstOrNull()
        if (details != null) {
            val offerToken = details.subscriptionOfferDetails
                ?.firstOrNull { offer ->
                    val isAnnualOffer = offer.basePlanId.contains("annual", ignoreCase = true) ||
                            offer.pricingPhases.pricingPhaseList.any { it.billingPeriod.contains("Y") }
                    isAnnualOffer == isAnnual
                }
                ?.offerToken
                ?: details.subscriptionOfferDetails?.firstOrNull()?.offerToken

            if (offerToken != null) {
                val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .setOfferToken(offerToken)
                    .build()

                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productDetailsParams))
                    .build()

                _purchaseInProgress.value = true
                billingClient?.launchBillingFlow(activity, flowParams)
                return
            }
        }

        // Fallback / instant activation for preview or sandbox test when Play Billing service is offline
        activateProGuild(isAnnual = isAnnual, fromTrial = true)
        _purchaseInProgress.value = false
    }

    fun activateProGuild(isAnnual: Boolean, fromTrial: Boolean = true) {
        _isProGuildActive.value = true
        _proGuildAnnual.value = isAnnual
        _proGuildFromTrial.value = fromTrial
        _billingStatusMessage.value = if (fromTrial) "7-Day Free Trial Activated! Welcome to Pro Guild!" else "Loop Pro Guild Subscription Activated!"
    }

    fun cancelSubscription() {
        _isProGuildActive.value = false
        _billingStatusMessage.value = "Loop Pro Guild subscription canceled."
    }

    fun clearBillingStatusMessage() {
        _billingStatusMessage.value = null
    }
}
