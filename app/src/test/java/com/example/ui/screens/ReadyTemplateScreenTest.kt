package com.example.ui.screens

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.PosterRepository
import com.example.model.Poster
import com.example.templates.*
import com.example.ui.PosterViewModel
import com.example.ui.ProfileSettings
import com.google.firebase.FirebaseApp
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk=[36],qualifiers="w411dp-h891dp-xxhdpi")
class ReadyTemplateScreenTest {
    @get:Rule val compose=createComposeRule()

    @Test fun minimalFormGeneratesAndSavesWithoutAnyFreeEditorControls() {
        val context:Context=ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(context)
        val database=Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).allowMainThreadQueries().build()
        val viewModel=PosterViewModel(PosterRepository(database.posterDao()),context)
        val template=StarterTemplates.all.first().copy(name="Test Welcome",slots=StarterTemplates.all.first().slots.filter{it.field!=TemplateField.MESSAGE})
        val profile=ProfileSettings(companyName="USER COMPANY",companyLogoUri="res:sample_business_man",mobileNumber="9876543210",websiteName="www.example.com")
        var saved:Poster?=null
        compose.setContent { MaterialTheme {
            ReadyPosterCustomize(GeneratedPoster(template,photo="res:sample_business_woman"),viewModel,profile,onUpdated={saved=it},onBack={},onProfile={})
        } }
        fun scrollTo(label:String) { compose.onNode(hasScrollToIndexAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)).performScrollToNode(hasText(label)) }
        scrollTo("Name *")
        compose.onNodeWithText("Name *").performTextInput("PRIYA SHARMA")
        scrollTo("Designation (optional)")
        compose.onNodeWithText("Designation (optional)").performTextInput("SOFTWARE DEVELOPER")
        compose.onNodeWithText("Message (optional)").assertDoesNotExist()
        listOf("Add Text","Add Shape","Layers","Rotate","Move Logo").forEach{compose.onNodeWithText(it).assertDoesNotExist()}
        scrollTo("Generate poster")
        compose.onNodeWithText("Generate poster").performClick()
        compose.waitUntil(30000){saved!=null}
        val snapshot=TemplateJson.design(saved!!.backgroundImageRes)!!
        assertEquals("PRIYA SHARMA",snapshot.values["NAME"])
        assertEquals("SOFTWARE DEVELOPER",snapshot.values["DESIGNATION"])
        assertEquals("USER COMPANY",snapshot.branding.company)
        assertTrue(File(saved!!.thumbnailPath).isFile)
        scrollTo("Save to Gallery")
        compose.onNodeWithText("Save to Gallery").assertIsDisplayed()
        compose.onNodeWithText("Share").assertExists()
        scrollTo("Customize poster")
        compose.waitForIdle()
        compose.onRoot().captureRoboImage(filePath="build/reports/ready-template-previews/customize-screen.png")
        database.close()
    }

    @Test fun fastPosterBrowsingScreenSwitchesTemplatesInstantlyWithoutForm() {
        val context: Context = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(context)
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        val viewModel = PosterViewModel(PosterRepository(database.posterDao()), context)
        val profile = ProfileSettings(
            userName = "RAHUL SHARMA",
            tagline = "Team Leader",
            companyName = "ABC BUSINESS",
            companyLogoUri = "res:sample_business_man",
            mobileNumber = "9876543210",
            websiteName = "www.example.com",
            profilePhotoUri = "res:sample_business_man"
        )
        compose.setContent {
            MaterialTheme {
                FastPosterBrowsingScreen(
                    category = "Birthday",
                    onCategoryChange = {},
                    templates = StarterTemplates.all.filter { it.category == "Birthday" },
                    initialPresetId = StarterTemplates.all.first { it.category == "Birthday" }.id,
                    viewModel = viewModel,
                    profile = profile,
                    onBack = {},
                    onProfile = {}
                )
            }
        }
        // Assert header
        compose.onNodeWithText("FOR BIRTHDAY WISHES").assertIsDisplayed()
        // Assert action buttons exist
        compose.onNodeWithText("DOWNLOAD").assertIsDisplayed()
        compose.onNodeWithText("Edit").assertIsDisplayed()
        // Assert choose design header shows every Birthday template
        compose.onNodeWithText("Choose Design (13)").assertIsDisplayed()
        // Assert no raw input fields are on this browsing screen
        compose.onNodeWithText("Name *").assertDoesNotExist()
        compose.onNodeWithText("Designation (optional)").assertDoesNotExist()
        compose.onNodeWithText("Message (optional)").assertDoesNotExist()
        // Assert free-editor tools are not present
        listOf("Add Text", "Add Shape", "Layers", "Rotate", "Move Logo").forEach {
            compose.onNodeWithText(it).assertDoesNotExist()
        }
        database.close()
    }

    @Test fun categoryGridDisplaysVisualCardsWithoutForm() {
        val context: Context = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(context)
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        val viewModel = PosterViewModel(PosterRepository(database.posterDao()), context)
        val profile = ProfileSettings(userName = "RAHUL SHARMA", companyName = "ABC BUSINESS")
        compose.setContent {
            MaterialTheme {
                FastPosterBrowsingScreen(
                    category = "Welcome",
                    onCategoryChange = {},
                    templates = StarterTemplates.all.filter { it.category == "Welcome" },
                    viewModel = viewModel,
                    profile = profile,
                    onBack = {},
                    onProfile = {}
                )
            }
        }
        compose.onNodeWithText("Welcome Templates").assertIsDisplayed()
        compose.onNodeWithText("4 Ready Templates").assertIsDisplayed()
        compose.onNodeWithText("Name *").assertDoesNotExist()
        compose.onNodeWithText("Designation (optional)").assertDoesNotExist()
        database.close()
    }

    @Test fun purpleGoldWelcomeOffersOptionalBackgroundRemovalWithoutReplacingPhotoControls() {
        val context: Context = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(context)
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        val viewModel = PosterViewModel(PosterRepository(database.posterDao()), context)
        val template = StarterTemplates.all.single { it.id == -214 }
        compose.setContent {
            MaterialTheme {
                ReadyPosterCustomize(
                    initial = GeneratedPoster(
                        template = template,
                        photo = "res:sample_business_woman",
                        originalPhoto = "res:sample_business_woman"
                    ),
                    viewModel = viewModel,
                    profile = ProfileSettings(),
                    onBack = {},
                    onProfile = {}
                )
            }
        }

        compose.onNodeWithText("Use Original").assertExists()
        compose.onNodeWithText("Remove Background").assertExists()
        compose.onNodeWithText("Adjust photo").assertExists()
        compose.onNodeWithText("Change photo").assertExists()
        database.close()
    }
}
