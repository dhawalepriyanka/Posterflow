package com.example.templates

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.PosterRepository
import com.example.ui.screens.fittedPosterRect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk=[36])
class TemplateWorkflowTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private class Role(var allowed:Boolean):AdminAuthorizer { override suspend fun isAdmin() = allowed }

    @Test fun allThirtyStartersUseOneValidatedSquareModel() {
        val all=StarterTemplates.all
        assertEquals(94,all.size)
        assertEquals(94,all.map{it.id}.distinct().size)
        assertEquals(mapOf("Welcome" to 12,"Birthday" to 13,"Achievement" to 6,"Income" to 6,"Festival" to 8,"Motivation" to 9,"Business" to 3,"Good Morning" to 3,"Good Night" to 3,"Anniversary" to 3,"Offers" to 2,"Events" to 2,"Political" to 3,"Real Estate" to 3,"Restaurant" to 3,"Healthcare" to 3,"Education" to 3,"Job Vacancy" to 3,"Quotes" to 3,"Devotional" to 3),all.groupingBy{it.category}.eachCount())
        all.forEach{assertNull("${it.name}: ${it.validationError()}",it.validationError());assertEquals(1080,it.canvasWidth);assertEquals(it,TemplateJson.template(it.asPoster().backgroundImageRes))}
    }

    @Test fun fieldsAreDefinedByTemplateNotByCategory() {
        val template=StarterTemplates.all.first().copy(name="Test Welcome",slots=StarterTemplates.all.first().slots.filter{it.field!=TemplateField.MESSAGE})
        assertEquals(setOf(TemplateField.PHOTO,TemplateField.NAME,TemplateField.DESIGNATION),template.visibleFields.toSet())
        assertFalse(TemplateField.MESSAGE in template.visibleFields)
        assertEquals(setOf(TemplateField.PHOTO,TemplateField.NAME),GeneratedPoster(template).missingFields().toSet())
        assertTrue(StarterTemplates.all.filter{it.category=="Festival"}.all{it.requiredFields.isEmpty()&&it.visibleFields.isEmpty()})
        assertTrue(StarterTemplates.all.filter{it.category=="Income"}.all{TemplateField.AMOUNT in it.requiredFields})
    }

    @Test fun brandingComesFromUserAndMissingDataIsExplicit() {
        val template=StarterTemplates.all.first()
        assertEquals(listOf("Company logo","Company name","Phone","Website"),GeneratedPoster(template).missingBranding())
        val a=BusinessBranding(logo="logo.png",company="Company A",phone="1234567890",website="a.example")
        val b=a.copy(company="Company B",website="b.example")
        assertTrue(GeneratedPoster(template,branding=a).missingBranding().isEmpty())
        assertFalse(TemplateJson.encodeTemplate(template).contains("Company A"))
        assertNotEquals(TemplateJson.encodeDesign(GeneratedPoster(template,branding=a)),TemplateJson.encodeDesign(GeneratedPoster(template,branding=b)))
        assertTrue(GeneratedPoster(template.copy(header=template.header.copy(enabled=false),footer=template.footer.copy(enabled=false))).missingBranding().isEmpty())
    }

    @Test fun adminCrudOrderingSoftDeleteAndRestart() = runBlocking {
        val role=Role(true)
        val repository=LocalTemplateRepository(context,role)
        repository.load()
        val created=repository.save(StarterTemplates.all.first().copy(id=0,name="Test Welcome",status=TemplateStatus.INACTIVE,slots=StarterTemplates.all.first().slots.filter{it.field!=TemplateField.MESSAGE},order=99))
        assertTrue(created.id>0)
        repository.setStatus(created,TemplateStatus.ACTIVE)
        assertTrue(repository.templates.value.any{it.name=="Test Welcome"&&it.status==TemplateStatus.ACTIVE})
        val duplicate=repository.duplicate(created)
        assertNotEquals(created.id,duplicate.id)
        assertEquals(TemplateStatus.INACTIVE,duplicate.status)
        repository.move(duplicate,-1)
        assertTrue(repository.templates.value.first{it.id==duplicate.id}.order<repository.templates.value.first{it.id==created.id}.order)
        repository.setStatus(created,TemplateStatus.DELETED)
        val reopened=LocalTemplateRepository(context,role)
        reopened.load()
        assertEquals(TemplateStatus.DELETED,reopened.templates.value.first{it.id==created.id}.status)
        assertFalse(reopened.templates.value.filter{it.status==TemplateStatus.ACTIVE}.any{it.id==created.id})
    }

    @Test fun normalUsersAndRevokedAdminsCannotWrite() = runBlocking {
        val role=Role(false)
        val repository=LocalTemplateRepository(context,role)
        assertTrue(runCatching{repository.save(StarterTemplates.all.first())}.exceptionOrNull() is IllegalStateException)
        assertTrue(runCatching{repository.duplicate(StarterTemplates.all.first())}.isFailure)
        assertTrue(runCatching{repository.move(StarterTemplates.all.first(),1)}.isFailure)
        role.allowed=true
        val template=repository.save(StarterTemplates.all.first().copy(id=0,name="Admin created"))
        role.allowed=false
        assertTrue(runCatching{repository.setStatus(template,TemplateStatus.DELETED)}.isFailure)
    }

    @Test fun savedRoomSnapshotSurvivesTemplateDeletionAndRestoresAllValues() = runBlocking {
        val database=Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).allowMainThreadQueries().build()
        try {
            val repository=PosterRepository(database.posterDao())
            val template=StarterTemplates.all.first()
            val document=adminDemo(template).copy(crop=PhotoCrop(2.3f,-.3f,.6f),branding=BusinessBranding("asset.png","My Company","9876543210","example.com"))
            val saved=repository.insertPoster(template.asPoster().copy(id=0,backgroundType="generated_template",backgroundImageRes=TemplateJson.encodeDesign(document)))
            val restored=TemplateJson.design(saved.backgroundImageRes)
            assertEquals(document,restored)
            val library=LocalTemplateRepository(context,Role(true))
            library.setStatus(template,TemplateStatus.DELETED)
            assertEquals(TemplateStatus.ACTIVE,restored!!.template.status)
            assertEquals("My Company",restored.branding.company)
            assertEquals(2.3f,restored.crop.scale)
        } finally {database.close()}
    }

    @Test fun importedArtworkAndPortraitSurviveSourceRemoval() = runBlocking {
        val original=File(context.cacheDir,"picker.png")
        Bitmap.createBitmap(160,80,Bitmap.Config.ARGB_8888).also{it.eraseColor(Color.MAGENTA);original.outputStream().use{out->it.compress(Bitmap.CompressFormat.PNG,100,out)};it.recycle()}
        val copied=TemplateImages.import(context,Uri.fromFile(original))
        assertNotEquals(original.absolutePath,copied)
        assertTrue(original.delete())
        assertTrue(File(copied).isFile)
        assertNotNull(TemplateImages.read(context,copied))
    }

    @Test fun changingBandHeightReflowsDynamicSlotsAndCircleStaysRound() {
        StarterTemplates.all.forEach { template->
            val changed=template.withBand(template.header.copy(layout="center",height=220f),true)
                .withBand(template.footer.copy(height=170f),false)
            assertNull("${template.name}: ${changed.validationError()}",changed.validationError())
            changed.slots.filter{it.field==TemplateField.PHOTO&&it.shape=="circle"}.forEach{assertEquals(it.width,it.height)}
        }
    }

    @Test fun videoFitNeverStretchesOrCrops() {
        assertEquals(RectF(0f,0f,720f,720f),fittedPosterRect(1080,1080,720,720))
        assertEquals(RectF(0f,280f,720f,1000f),fittedPosterRect(1080,1080,720,1280))
        assertEquals(RectF(157.5f,0f,562.5f,720f),fittedPosterRect(1080,1920,720,720))
    }

    @Test fun rendererCreatesThirtyPostersAndVisualContactSheets() {
        val dir=File("build/reports/ready-template-previews").apply{mkdirs()}
        val paint=Paint(Paint.ANTI_ALIAS_FLAG).apply{color=Color.WHITE;textSize=18f}
        StarterTemplates.categories.forEach { category->
            val templates=StarterTemplates.all.filter{it.category==category}
            val sheet=Bitmap.createBitmap(1080,((templates.size+2)/3)*395,Bitmap.Config.ARGB_8888)
            sheet.eraseColor(Color.rgb(15,20,30))
            val canvas=Canvas(sheet)
            templates.forEachIndexed{index,template->
                val full=TemplateRenderer.render(context,adminDemo(template))
                assertEquals(1080,full.width);assertEquals(1080,full.height)
                assertEquals(255,Color.alpha(full.getPixel(0,0)))
                val decoded=TemplateJson.design(TemplateJson.encodeDesign(adminDemo(template)))!!
                val repeat=TemplateRenderer.render(context,decoded)
                assertTrue("Reopened render must be identical",full.sameAs(repeat));repeat.recycle()
                File(dir,"${template.id}.png").outputStream().use{full.compress(Bitmap.CompressFormat.PNG,100,it)}
                val x=(index%3)*360f;val y=(index/3)*395f
                canvas.drawBitmap(full,null,RectF(x,y,x+360,y+360),null)
                canvas.drawText(template.name,x+8,y+382,paint)
                full.recycle()
            }
            File(dir,"$category-collection.png").outputStream().use{sheet.compress(Bitmap.CompressFormat.PNG,100,it)}
            sheet.recycle()
        }
    }

    @Test fun longTextPortraitAndLogoPreserveAspectRatio() {
        val dir=File("build/reports/ready-template-previews").apply{mkdirs()}
        val logo=Bitmap.createBitmap(400,100,Bitmap.Config.ARGB_8888)
        logo.eraseColor(Color.YELLOW)
        val logoFile=File(context.filesDir,"wide_logo.png")
        logoFile.outputStream().use{logo.compress(Bitmap.CompressFormat.PNG,100,it)};logo.recycle()
        val headerTemplate = StarterTemplates.all.first { it.header.enabled && it.header.showLogo }
        val document=adminDemo(headerTemplate).copy(branding=BusinessBranding(logoFile.absolutePath,"A company with a long but real business name","+91 98765 43210","www.example-long-domain.com"),
            values=mapOf("NAME" to "Priya Sharma Kulkarni Deshmukh","DESIGNATION" to "Senior Business Development Consultant","MESSAGE" to "We are delighted to welcome you to our team. Let us learn, grow and succeed together."),crop=PhotoCrop(3.5f,-1f,1f))
        val bitmap=TemplateRenderer.render(context,document)
        // The wide logo is contained in a square region: background must remain above/below it.
        assertNotEquals(Color.YELLOW,bitmap.getPixel(60,18))
        assertEquals(Color.YELLOW,bitmap.getPixel(60,55))
        File(dir,"long-text-crop.png").outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        bitmap.recycle()
    }
}
