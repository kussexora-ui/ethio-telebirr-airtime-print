package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.TopNavBar
import com.example.ui.screens.*
import com.example.ui.theme.EthioIdTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EthioIdViewModel
import com.example.util.LanguageUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EthioIdTheme {
                EthioIdMainApp()
            }
        }
    }
}

@Composable
fun EthioIdMainApp(viewModel: EthioIdViewModel = viewModel()) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val cards by viewModel.cards.collectAsState()
    val selectedCard by viewModel.selectedCard.collectAsState()
    val stamps by viewModel.stamps.collectAsState()
    val signatures by viewModel.signatures.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifCount by viewModel.unreadNotificationCount.collectAsState()

    val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsState()
    val adminLoginError by viewModel.adminLoginError.collectAsState()

    val uiLabels = remember(selectedLanguage) { LanguageUtils.getUiLabels(selectedLanguage.code) }

    val screenTitle = when (currentScreen) {
        AppScreen.SPLASH -> "እንኳን በደህና መጡ"
        AppScreen.AUTH_LOGIN -> "መግቢያ (Account Login)"
        AppScreen.CUSTOMER_HOME -> uiLabels.appTitle
        AppScreen.NEW_ID_FORM -> uiLabels.newIdNav
        AppScreen.PAYMENT_VERIFICATION -> uiLabels.paymentNav
        AppScreen.ID_PREVIEW -> uiLabels.previewNav
        AppScreen.ADMIN_DASHBOARD -> uiLabels.adminNav
        AppScreen.QR_SCANNER -> uiLabels.qrNav
        AppScreen.NOTIFICATIONS -> uiLabels.notifNav
    }

    Scaffold(
        topBar = {
            if (currentScreen != AppScreen.SPLASH) {
                TopNavBar(
                    title = screenTitle,
                    isAdminMode = isAdminMode,
                    unreadNotificationCount = unreadNotifCount,
                    currentLanguage = selectedLanguage,
                    onLanguageSelect = { viewModel.setLanguage(it) },
                    onRoleSwitchToggle = { viewModel.toggleAdminMode() },
                    onNotificationClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                    onQrScanClick = { viewModel.navigateTo(AppScreen.QR_SCANNER) },
                    onBackClick = if (currentScreen != AppScreen.CUSTOMER_HOME && currentScreen != AppScreen.ADMIN_DASHBOARD && currentScreen != AppScreen.AUTH_LOGIN) {
                        { viewModel.navigateTo(if (isAdminMode) AppScreen.ADMIN_DASHBOARD else AppScreen.CUSTOMER_HOME) }
                    } else null
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (currentScreen == AppScreen.SPLASH) PaddingValues(0.dp) else innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.SPLASH -> SplashScreen(
                    onSplashFinished = { viewModel.navigateTo(AppScreen.AUTH_LOGIN) }
                )

                AppScreen.AUTH_LOGIN -> LoginAuthScreen(
                    viewModel = viewModel,
                    onCustomerLoginSuccess = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) },
                    onAdminLoginSuccess = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                )

                AppScreen.CUSTOMER_HOME -> CustomerHomeScreen(
                    viewModel = viewModel,
                    cards = cards,
                    onNavigateToNewForm = { viewModel.navigateTo(AppScreen.NEW_ID_FORM) },
                    onNavigateToPreview = { id -> viewModel.navigateTo(AppScreen.ID_PREVIEW, id) },
                    onNavigateToPayment = { id -> viewModel.navigateTo(AppScreen.PAYMENT_VERIFICATION, id) }
                )

                AppScreen.NEW_ID_FORM -> NewIdFormScreen(
                    viewModel = viewModel,
                    onNavigateToPayment = { id -> viewModel.navigateTo(AppScreen.PAYMENT_VERIFICATION, id) },
                    onBack = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) }
                )

                AppScreen.PAYMENT_VERIFICATION -> PaymentScreen(
                    viewModel = viewModel,
                    card = selectedCard,
                    onPaymentSubmitted = { viewModel.navigateTo(AppScreen.ID_PREVIEW, selectedCard?.id) },
                    onBack = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) }
                )

                AppScreen.ID_PREVIEW -> IdPreviewScreen(
                    viewModel = viewModel,
                    card = selectedCard,
                    onNavigateToPayment = { id -> viewModel.navigateTo(AppScreen.PAYMENT_VERIFICATION, id) },
                    onBack = { viewModel.navigateTo(if (isAdminMode) AppScreen.ADMIN_DASHBOARD else AppScreen.CUSTOMER_HOME) }
                )

                AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(
                    viewModel = viewModel,
                    cards = cards,
                    stamps = stamps,
                    signatures = signatures,
                    onPreviewCard = { id -> viewModel.navigateTo(AppScreen.ID_PREVIEW, id) }
                )

                AppScreen.QR_SCANNER -> QrScannerScreen(
                    viewModel = viewModel,
                    cards = cards,
                    onBack = { viewModel.navigateTo(if (isAdminMode) AppScreen.ADMIN_DASHBOARD else AppScreen.CUSTOMER_HOME) }
                )

                AppScreen.NOTIFICATIONS -> NotificationScreen(
                    viewModel = viewModel,
                    notifications = notifications,
                    onNotificationClick = { cardId ->
                        if (cardId != null) viewModel.navigateTo(AppScreen.ID_PREVIEW, cardId)
                    },
                    onBack = { viewModel.navigateTo(if (isAdminMode) AppScreen.ADMIN_DASHBOARD else AppScreen.CUSTOMER_HOME) }
                )
            }

            if (showAdminLoginDialog) {
                AdminLoginDialog(
                    errorMessage = adminLoginError,
                    onDismiss = { viewModel.dismissAdminLoginDialog() },
                    onLoginSubmit = { email, pass ->
                        viewModel.attemptAdminLogin(email, pass)
                    }
                )
            }
        }
    }
}
