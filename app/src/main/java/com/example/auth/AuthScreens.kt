package com.example.auth

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onFinished()
    }
    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_posterflow_p),
                contentDescription = "PosterFlow logo",
                modifier = Modifier.size(124.dp)
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "PosterFlow",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Create Professional\nPosters & Videos",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AuthenticationScreen(
    isLoading: Boolean,
    onGoogleLogin: () -> Unit
) {
    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically { it / 5 }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(18.dp))
                        Text("Welcome", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Create Professional Posters & Videos in Minutes.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(26.dp))
                        ElevatedButton(
                            onClick = onGoogleLogin,
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(Modifier.size(10.dp))
                            Text("Continue with Google")
                        }
                        if (isLoading) {
                            Spacer(Modifier.height(18.dp))
                            CircularProgressIndicator(modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.height(22.dp))
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = {}) { Text("Privacy Policy") }
                            TextButton(onClick = {}) { Text("Terms & Conditions") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompleteProfileScreen(
    user: AuthUser?,
    isLoading: Boolean,
    onSave: (CompleteProfileForm) -> Unit
) {
    var form by remember(user) {
        mutableStateOf(
            CompleteProfileForm(
                name = user?.name.orEmpty(),
                mobileNumber = user?.phone.orEmpty(),
                profilePhotoUri = user?.profilePhotoUrl
                    ?.takeIf { it.isNotBlank() }
                    ?.let(Uri::parse)
            )
        )
    }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) form = form.copy(profilePhotoUri = it)
    }
    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) form = form.copy(companyLogoUri = it)
    }

    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(20.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Complete Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("This information will automatically fill your poster and video templates.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        ImagePickCard("Profile Photo", form.profilePhotoUri?.toString(), Icons.Default.AccountCircle) { photoPicker.launch("image/*") }
                        ImagePickCard("Company Logo", form.companyLogoUri?.toString(), Icons.Default.Business) { logoPicker.launch("image/*") }
                    }
                    AuthTextField(form.name, { form = form.copy(name = it) }, "Full Name", Icons.Default.AccountCircle)
                    AuthTextField(form.companyName, { form = form.copy(companyName = it) }, "Company Name", Icons.Default.Business)
                    AuthTextField(form.website, { form = form.copy(website = it) }, "Website", Icons.Default.Language)
                    AuthTextField(form.mobileNumber, { form = form.copy(mobileNumber = it) }, "Mobile Number", Icons.Default.Phone, KeyboardType.Phone)
                    AuthTextField(form.businessAddress, { form = form.copy(businessAddress = it) }, "Business Address", Icons.Default.Business)
                    Button(
                        onClick = { onSave(form) },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun AuthScaffold(
    errorMessage: String?,
    onConsumeError: () -> Unit,
    content: @Composable (SnackbarHostState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage)
            onConsumeError()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp)
    ) {
        Box(Modifier.padding(it)) {
            content(snackbarHostState)
        }
    }
}

@Composable
private fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
private fun AuthFormScreen(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(22.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.Center
        ) {
            Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))) {
                Column(Modifier.padding(20.dp)) {
                    TextButton(onClick = onBack) { Text("Back") }
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(20.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun RowScope.ImagePickCard(label: String, model: String?, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.weight(1f).height(116.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (!model.isNullOrBlank()) {
                AsyncImage(model = model, contentDescription = label, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)), contentScale = ContentScale.Crop)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(label, style = MaterialTheme.typography.labelLarge)
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = label != "Business Address",
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
