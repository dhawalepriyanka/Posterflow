package com.example.templates

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Poster
import com.example.ui.PosterViewModel
import com.example.ui.ProfileSettings
import com.example.ui.screens.saveBitmapToGallery
import com.example.ui.screens.sharePosterBitmap
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reusable Welcome template content and branding data model.
 * Bridges template definitions, user-entered guest data, and profile business branding.
 */
data class WelcomeTemplateData(
    val templateId: Int,
    val templateName: String,
    val welcomeTitle: String = "WELCOME",
    val welcomeSubtitle: String = "",
    val guestName: String = "",
    val guestDesignation: String = "",
    val welcomeMessage: String = "",
    val guestPhoto: String = "",
    val optionalSupportingPhotos: List<String> = emptyList(),
    val businessLogo: String = "",
    val userProfilePhoto: String = "",
    val userName: String = "",
    val userDesignation: String = "",
    val businessName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val website: String = "",
    val tagline: String = "",
    val primaryColour: String = "#0A0520",
    val secondaryColour: String = "#1A0F30",
    val accentColour: String = "#F5C542",
    val aspectRatio: Float = 1.0f,
    val brandingStyle: String = "fixed_footer"
)

/**
 * Reusable Registry for all 12 Welcome templates.
 */
object WelcomeTemplateRegistry {
    val templates: List<PosterTemplate>
        get() = StarterTemplates.all.filter { it.category.equals("Welcome", ignoreCase = true) }

    fun getTemplateById(id: Int): PosterTemplate? =
        templates.firstOrNull { it.id == id }

    fun createTemplateData(
        template: PosterTemplate,
        profile: ProfileSettings,
        guestName: String = "",
        guestDesignation: String = "",
        welcomeMessage: String = "",
        guestPhoto: String = ""
    ): WelcomeTemplateData {
        return WelcomeTemplateData(
            templateId = template.id,
            templateName = template.name,
            welcomeTitle = template.staticTexts.firstOrNull()?.text ?: "WELCOME",
            welcomeSubtitle = template.staticTexts.getOrNull(1)?.text ?: "",
            guestName = guestName.ifBlank { "YOUR NAME" },
            guestDesignation = guestDesignation,
            welcomeMessage = welcomeMessage,
            guestPhoto = guestPhoto,
            businessLogo = profile.companyLogoUri,
            userProfilePhoto = profile.profilePhotoUri,
            userName = profile.userName,
            userDesignation = profile.designation,
            businessName = profile.companyName,
            phoneNumber = profile.mobileNumber,
            email = profile.businessEmail.ifBlank { profile.userEmail },
            website = profile.websiteName,
            tagline = profile.tagline,
            primaryColour = template.baseColor,
            secondaryColour = template.footer.backgroundColor,
            accentColour = template.accentColor,
            aspectRatio = 1.0f,
            brandingStyle = "fixed_footer"
        )
    }

    fun toGeneratedPoster(data: WelcomeTemplateData, template: PosterTemplate): GeneratedPoster {
        val values = mutableMapOf<String, String>()
        if (data.guestName.isNotBlank()) values[TemplateField.NAME.name] = data.guestName
        if (data.guestDesignation.isNotBlank()) values[TemplateField.DESIGNATION.name] = data.guestDesignation
        if (data.welcomeMessage.isNotBlank()) values[TemplateField.MESSAGE.name] = data.welcomeMessage

        val branding = BusinessBranding(
            logo = data.businessLogo,
            company = data.businessName,
            phone = data.phoneNumber,
            website = data.website,
            email = data.email,
            tagline = data.tagline,
            profilePhoto = data.userProfilePhoto,
            ownerName = data.userName,
            ownerDesignation = data.userDesignation
        )

        return GeneratedPoster(
            template = template,
            values = values,
            photo = data.guestPhoto,
            branding = branding
        )
    }
}

/**
 * Reusable Compose fixed branding layer for Welcome and custom posters.
 * Adheres strictly to the fixed layout:
 * - Profile photo circle
 * - User name + designation
 * - Business logo
 * - Phone, email, website contact strip with icons
 * - Business name & tagline
 * Colors adapt cleanly to the provided palette.
 */
@Composable
fun FixedBrandingSection(
    branding: BusinessBranding,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF0F172A),
    textColor: Color = Color.White,
    accentColor: Color = Color(0xFFF5C542),
    height: Dp = 100.dp
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Divider top stroke
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(accentColor.copy(alpha = 0.4f))
        )

        // Row 1: Profile photo | User Name & Role | Business Logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Profile photo circle
                val profileBmp = remember(branding.profilePhoto) {
                    if (branding.profilePhoto.isNotBlank()) TemplateImages.read(context, branding.profilePhoto) else null
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, accentColor, CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBmp != null) {
                        androidx.compose.foundation.Image(
                            bitmap = profileBmp.asImageBitmap(),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Name + Designation
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = branding.ownerName.ifBlank { branding.company.ifBlank { "YOUR NAME" } }
                    Text(
                        text = displayName,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (branding.ownerDesignation.isNotBlank()) {
                        Text(
                            text = branding.ownerDesignation,
                            color = accentColor,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Business Logo
            val logoBmp = remember(branding.logo) {
                if (branding.logo.isNotBlank()) TemplateImages.read(context, branding.logo) else null
            }
            if (logoBmp != null) {
                androidx.compose.foundation.Image(
                    bitmap = logoBmp.asImageBitmap(),
                    contentDescription = "Business Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )
            } else if (branding.company.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = branding.company.take(1).uppercase(),
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Row 2: Contact Strip (Phone, Website, Email)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (branding.phone.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = accentColor, modifier = Modifier.size(11.dp))
                    Text(branding.phone, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (branding.website.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = accentColor, modifier = Modifier.size(11.dp))
                    Text(branding.website, color = textColor, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            if (branding.email.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = accentColor, modifier = Modifier.size(11.dp))
                    Text(branding.email, color = textColor, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Row 3: Company name centered
        if (branding.company.isNotBlank()) {
            Text(
                text = branding.company.uppercase(),
                color = textColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Reusable horizontal thumbnail carousel for switching between templates instantly.
 */
@Composable
fun TemplateThumbnailCarousel(
    templates: List<PosterTemplate>,
    selectedTemplateId: Int,
    onSelectTemplate: (PosterTemplate) -> Unit,
    currentPersonPhoto: String,
    profile: ProfileSettings,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        items(templates, key = { it.id }) { t ->
            val isSelected = t.id == selectedTemplateId
            val thumbDesign = remember(t.id, currentPersonPhoto, profile) {
                GeneratedPoster(
                    template = t,
                    photo = currentPersonPhoto,
                    branding = profile.businessBranding()
                )
            }

            Card(
                modifier = Modifier
                    .size(86.dp)
                    .aspectRatio(1f)
                    .clickable { onSelectTemplate(t) },
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) BorderStroke(3.dp, Color(0xFFFFB703))
                else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ReadyPosterPreview(design = thumbDesign, resolution = 200)
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(3.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB703)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * High-resolution export and persistence manager for posters.
 */
object PosterExportManager {
    suspend fun exportHighResBitmap(
        context: Context,
        design: GeneratedPoster,
        resolution: Int = 1080
    ): Bitmap = withContext(Dispatchers.Default) {
        TemplateRenderer.render(context, design, resolution)
    }

    suspend fun saveToGalleryAndDesigns(
        context: Context,
        viewModel: PosterViewModel,
        design: GeneratedPoster,
        resolution: Int = 1080
    ): Bitmap {
        val bitmap = exportHighResBitmap(context, design, resolution)
        val path = withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "poster_thumbnails").apply { mkdirs() }
            File(dir, "ready_${UUID.randomUUID()}.png").also { out ->
                out.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }.absolutePath
        }
        val record = design.template.asPoster().copy(
            title = design.template.name,
            backgroundType = "generated_template",
            backgroundImageRes = TemplateJson.encodeDesign(design),
            thumbnailPath = path,
            timestamp = System.currentTimeMillis()
        )
        viewModel.savePosterRecordNow(record, "", path)
        withContext(Dispatchers.IO) {
            saveBitmapToGallery(context, bitmap, design.template.name)
        }
        return bitmap
    }

    suspend fun sharePoster(
        context: Context,
        design: GeneratedPoster,
        resolution: Int = 1080
    ) {
        val bitmap = exportHighResBitmap(context, design, resolution)
        sharePosterBitmap(context, bitmap, design.template.name)
    }
}
