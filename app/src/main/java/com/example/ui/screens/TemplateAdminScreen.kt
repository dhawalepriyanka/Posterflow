package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.templates.*
import com.example.ui.PosterViewModel
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.launch

@Composable
internal fun TemplateAdminScreen(viewModel: PosterViewModel, onBack: () -> Unit) {
    val allowed by viewModel.isAdmin.collectAsStateWithLifecycle()
    val all by viewModel.templates.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var editingJson by rememberSaveable { mutableStateOf<String?>(null) }
    var previewId by rememberSaveable { mutableStateOf<Int?>(null) }
    var deleteTarget by remember { mutableStateOf<PosterTemplate?>(null) }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Welcome") }
    fun action(block: suspend () -> Unit) {
        if(busy) return
        busy = true
        scope.launch { try { block(); status="Template library updated on this device." } catch(e:Exception){status=e.message ?: "Unable to update templates."} finally {busy=false} }
    }
    BackHandler { if(editingJson!=null) editingJson=null else if(previewId!=null)previewId=null else onBack() }
    if(!allowed) {
        Column(Modifier.padding(24.dp).statusBarsPadding()) { Text("Administrator access is required."); TextButton(onClick=onBack){Text("Back") } }
        return
    }
    val editing=editingJson?.let(TemplateJson::template)
    if(editing!=null) {
        TemplateBuilder(editing,busy,onChange={editingJson=TemplateJson.encodeTemplate(it)},onBack={editingJson=null},onSave={ t ->
            action { viewModel.templateRepository.save(t); editingJson=null }
        },status=status)
        return
    }
    previewId?.let { id -> all.firstOrNull{it.id==id}?.let { template ->
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(),contentPadding=PaddingValues(16.dp)) {
            item { TextButton(onClick={previewId=null}){Text("← Management")};Text("Admin preview • sample data only");ReadyPosterPreview(adminDemo(template));Text(template.name) }
        }
        return
    } }
    deleteTarget?.let { template -> AlertDialog(onDismissRequest={deleteTarget=null},title={Text("Delete ${template.name}?")},text={Text("This hides the template from the library. Saved designs keep their original template and artwork.")},confirmButton={TextButton(onClick={action{viewModel.templateRepository.setStatus(template,TemplateStatus.DELETED)};deleteTarget=null}){Text("Delete")}},dismissButton={TextButton(onClick={deleteTarget=null}){Text("Cancel")}}) }
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { TextButton(onClick=onBack){Text("← Profile")};Text("Template Management",style=MaterialTheme.typography.headlineSmall);Text("Local library • changes do not sync to other phones.",style=MaterialTheme.typography.bodySmall)
            Button(onClick={editingJson=TemplateJson.encodeTemplate(StarterTemplates.starter(0,"Untitled template",category).copy(status=TemplateStatus.INACTIVE,order=all.filter{it.category==category}.size))},enabled=!busy){Text("+ Create template")}
            ChoiceRow(category,StarterTemplates.categories){category=it}
            if(status.isNotBlank())Text(status)
            if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        items(all.filter{it.category==category && it.status!=TemplateStatus.DELETED}.sortedBy{it.order},key={it.id}) { template ->
            Card {
                Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        ReadyPosterPreview(adminDemo(template),Modifier.width(120.dp),resolution=240)
                        Column(Modifier.weight(1f)) { Text(template.name,style=MaterialTheme.typography.titleMedium);Text("${template.category} • ${template.status}");Text("Created ${DateFormat.getDateInstance().format(Date(template.createdAt))}",style=MaterialTheme.typography.bodySmall);Text("Updated ${DateFormat.getDateInstance().format(Date(template.updatedAt))}",style=MaterialTheme.typography.bodySmall) }
                    }
                    Row { TextButton(onClick={status="";editingJson=TemplateJson.encodeTemplate(template)},enabled=!busy){Text("Edit")};TextButton(onClick={previewId=template.id},enabled=!busy){Text("Preview")};TextButton(onClick={action{viewModel.templateRepository.duplicate(template)}},enabled=!busy){Text("Duplicate")} }
                    Row { TextButton(onClick={action{viewModel.templateRepository.setStatus(template,if(template.status==TemplateStatus.ACTIVE)TemplateStatus.INACTIVE else TemplateStatus.ACTIVE)}},enabled=!busy){Text(if(template.status==TemplateStatus.ACTIVE)"Deactivate" else "Activate")};TextButton(onClick={deleteTarget=template},enabled=!busy){Text("Delete")} }
                    Row { TextButton(onClick={action{viewModel.templateRepository.move(template,-1)}},enabled=!busy){Text("↑ Move up")};TextButton(onClick={action{viewModel.templateRepository.move(template,1)}},enabled=!busy){Text("↓ Move down")} }
                }
            }
        }
    }
}

@Composable
private fun TemplateBuilder(template:PosterTemplate,busy:Boolean,onChange:(PosterTemplate)->Unit,onBack:()->Unit,onSave:(PosterTemplate)->Unit,status:String) {
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var error by remember { mutableStateOf("") }
    var importing by remember{mutableStateOf(false)}
    var preview by rememberSaveable{mutableStateOf(false)}
    var selected by rememberSaveable{mutableStateOf(TemplateField.PHOTO.name)}
    val latest by rememberUpdatedState(template)
    val upload=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null){importing=true;scope.launch{try{val path=TemplateImages.import(context,uri);onChange(latest.copy(backgroundArtwork=path));error=""}catch(e:Exception){error=e.message.orEmpty()}finally{importing=false}}}}
    val selectedSlot=template.slots.firstOrNull{it.field.name==selected && it.enabled}
    fun editSlot(slot:TemplateSlot){onChange(latest.copy(slots=latest.slots.map{if(it.field==slot.field)slot else it}))}
    val locked=busy||importing
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().imePadding(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { TextButton(onClick=onBack,enabled=!locked){Text("← Management")};Text(if(template.id==0)"Create template" else "Edit template",style=MaterialTheme.typography.headlineSmall)
            Text("Design once. Users only fill the enabled fields.")
            OutlinedTextField(template.name,{onChange(template.copy(name=it.take(80)))},label={Text("Template name")},modifier=Modifier.fillMaxWidth(),enabled=!locked)
            ChoiceRow(template.category,StarterTemplates.categories){ category -> if(!locked)onChange(StarterTemplates.starter(template.id,template.name,category).copy(backgroundArtwork=template.backgroundArtwork,header=template.header,footer=template.footer,status=template.status,order=template.order,createdAt=template.createdAt)) }
            Button(onClick={upload.launch(arrayOf("image/*"))},enabled=!locked){Text("Upload poster artwork")}
            Text("Use square artwork. Header/footer cover their configured bands. Keep dynamic areas clear.",style=MaterialTheme.typography.bodySmall)
            if(template.backgroundArtwork.isNotBlank())TextButton(onClick={onChange(template.copy(backgroundArtwork=""))},enabled=!locked){Text("Use bundled original design instead")}
            ChoiceRow(template.status.name,listOf("ACTIVE","INACTIVE")){if(!locked)onChange(template.copy(status=TemplateStatus.valueOf(it)))}
        }
        item {
            Text("Dynamic fields",style=MaterialTheme.typography.titleMedium)
            TemplateField.entries.forEach { field ->
                val slot=template.slots.firstOrNull{it.field==field}
                Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(slot?.enabled==true,onCheckedChange={enabled->if(!locked){
                        val added=slot?.copy(enabled=enabled) ?: TemplateSlot(field,enabled=true,required=field in listOf(TemplateField.NAME,TemplateField.PHOTO),x=100f,y=400f,width=if(field==TemplateField.PHOTO)320f else 700f,height=if(field==TemplateField.PHOTO)320f else 80f)
                        onChange(template.copy(slots=template.slots.filterNot{it.field==field}+added));selected=field.name
                    }},enabled=!locked)
                    Text(field.name,Modifier.weight(1f))
                    if(slot?.enabled==true){Text("Required",style=MaterialTheme.typography.labelSmall);Switch(slot.required,onCheckedChange={editSlot(slot.copy(required=it))},enabled=!locked)}
                }
            }
        }
        item { Text("Configure layout",style=MaterialTheme.typography.titleMedium);Text("Select a slot, drag to position, or drag its lower-right handle to resize. These tools are admin-only.")
            ChoiceRow(selected,template.slots.filter{it.enabled}.map{it.field.name}){selected=it}
            TextButton(onClick={preview=!preview}){Text(if(preview)"Show slot controls" else "Preview with sample data")}
            Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
                ReadyPosterPreview(adminDemo(template))
                if(!preview) {
                    var sizePx by remember{mutableFloatStateOf(1f)}
                    var dragging by remember{mutableStateOf(false)}
                    var resizing by remember{mutableStateOf(false)}
                    val currentSlot by rememberUpdatedState(selectedSlot)
                    Canvas(Modifier.matchParentSize().onSizeChanged{sizePx=it.width.toFloat()}.pointerInput(selected,locked){if(!locked)detectDragGestures(
                        onDragStart={p->val s=currentSlot;val x=p.x/sizePx*1080;val y=p.y/sizePx*1080;dragging=s!=null&&x in s.x..s.x+s.width+25&&y in s.y..s.y+s.height+25;resizing=s!=null&&x>s.x+s.width-65&&y>s.y+s.height-65},
                        onDragEnd={dragging=false},onDragCancel={dragging=false},onDrag={change,delta->
                            val s=currentSlot
                            if(dragging&&s!=null){change.consume();val dx=delta.x/sizePx*1080;val dy=delta.y/sizePx*1080
                                val top=if(latest.header.enabled)latest.header.height+10 else 20f
                                val bottom=if(latest.footer.enabled)1080-latest.footer.height-10 else 1060f
                                val next=if(resizing)s.copy(width=(s.width+dx).coerceIn(40f,1060-s.x),height=(s.height+dy).coerceIn(30f,(bottom-s.y).coerceAtLeast(30f)))
                                else s.copy(x=(s.x+dx).coerceIn(20f,(1060-s.width).coerceAtLeast(20f)),y=(s.y+dy).coerceIn(top,(bottom-s.height).coerceAtLeast(top)))
                                editSlot(if(next.shape=="circle"&&next.field==TemplateField.PHOTO)next.copy(height=minOf(next.width,next.height),width=minOf(next.width,next.height)) else next)
                            }
                        })}) {
                        val scale=size.width/1080
                        template.slots.filter{it.enabled}.forEach { s->
                            val color=if(s.field.name==selected)Color.Cyan else Color.White.copy(alpha=.5f)
                            drawRect(color,Offset(s.x*scale,s.y*scale),Size(s.width*scale,s.height*scale),style=Stroke(if(s.field.name==selected)3f else 1f))
                            if(s.field.name==selected)drawRect(Color.Cyan,Offset((s.x+s.width)*scale-8,(s.y+s.height)*scale-8),Size(16f,16f))
                        }
                    }
                }
            }
        }
        if(selectedSlot!=null) item {
            val s=selectedSlot
            Text("${s.field} slot",style=MaterialTheme.typography.titleMedium)
            Text("Width ${s.width.toInt()} • Height ${s.height.toInt()}")
            Slider(s.width.coerceIn(40f,1000f),{editSlot(s.copy(width=it.coerceAtMost(1060-s.x)))},valueRange=40f..1000f,enabled=!locked)
            Slider(s.height.coerceIn(30f,760f),{editSlot(s.copy(height=it))},valueRange=30f..760f,enabled=!locked)
            if(s.field==TemplateField.PHOTO) {
                ChoiceRow(s.shape,listOf("circle","rounded","rectangle","oval")){shape->editSlot(s.copy(shape=shape,height=if(shape=="circle")s.width else s.height))}
                Text("Border ${s.borderWidth.toInt()}");Slider(s.borderWidth,{editSlot(s.copy(borderWidth=it))},valueRange=0f..20f,enabled=!locked)
                HexField("Border color",s.borderColor){editSlot(s.copy(borderColor=it))}
            } else {
                ChoiceRow(s.font,listOf("sans-serif","sans-serif-condensed","serif","monospace")){editSlot(s.copy(font=it))}
                Text("Font size ${s.fontSize.toInt()}");Slider(s.fontSize,{editSlot(s.copy(fontSize=it))},valueRange=12f..160f,enabled=!locked)
                Row { FilterChip(s.bold,onClick={editSlot(s.copy(bold=!s.bold))},label={Text("Bold")});Spacer(Modifier.width(8.dp));FilterChip(s.italic,onClick={editSlot(s.copy(italic=!s.italic))},label={Text("Italic")}) }
                ChoiceRow(s.alignment,listOf("left","center","right")){editSlot(s.copy(alignment=it))}
                Text("Maximum lines ${s.maxLines}");Slider(s.maxLines.toFloat(),{editSlot(s.copy(maxLines=it.toInt()))},valueRange=1f..8f,steps=6,enabled=!locked)
                HexField("Text color",s.color){editSlot(s.copy(color=it))}
            }
        }
        item { BandSettings("Header",template.header){onChange(template.withBand(it,true))} }
        item { BandSettings("Footer",template.footer){onChange(template.withBand(it,false))} }
        item {
            if(error.isNotBlank())Text(error,color=MaterialTheme.colorScheme.error)
            if(status.isNotBlank())Text(status)
            template.validationError()?.let { Text(it,color=MaterialTheme.colorScheme.error) }
            Button(onClick={val invalid=template.validationError();if(invalid!=null)error=invalid else onSave(template)},enabled=!locked,modifier=Modifier.fillMaxWidth()){Text("Save template")}
            if(locked)LinearProgressIndicator(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun HexField(label:String,value:String,onChange:(String)->Unit){OutlinedTextField(value,onChange,label={Text(label)},singleLine=true,isError=!validHex(value),modifier=Modifier.fillMaxWidth())}

@Composable
private fun BandSettings(title:String,band:BrandingBand,onChange:(BrandingBand)->Unit) {
    Card { Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){Text("Use $title",Modifier.weight(1f),style=MaterialTheme.typography.titleMedium);Switch(band.enabled,{onChange(band.copy(enabled=it))})}
        if(band.enabled) {
            Text("Layout preset")
            ChoiceRow(band.layout,listOf("left","center","right","company center")){value->onChange(band.copy(layout=value,showLogo=value!="company center",height=if(value=="center")220f else if(title=="Header")110f else 145f))}
            if(title=="Footer")ChoiceRow("",listOf("Logo · Phone · Website","Company + contacts","Full business card","Address + contacts")){preset->onChange(when(preset){
                "Logo · Phone · Website"->band.copy(layout="left",showLogo=true,showCompanyName=false,showPhone=true,showWebsite=true,showEmail=false,showAddress=false)
                "Company + contacts"->band.copy(layout="company center",showLogo=false,showCompanyName=true,showPhone=true,showWebsite=true,showEmail=false,showAddress=false)
                "Full business card"->band.copy(layout="left",showLogo=true,showCompanyName=true,showPhone=true,showWebsite=true,showEmail=true,showAddress=false,height=170f)
                else->band.copy(layout="company center",showLogo=false,showCompanyName=true,showPhone=true,showWebsite=true,showEmail=false,showAddress=true,height=170f)
            })}
            Text("Height ${band.height.toInt()}");Slider(band.height,{onChange(band.copy(height=it))},valueRange=60f..240f)
            HexField("Background color",band.backgroundColor){onChange(band.copy(backgroundColor=it))}
            HexField("Text color",band.textColor){onChange(band.copy(textColor=it))}
            val flags=if(title=="Header")listOf("Logo","Company","Tagline") else listOf("Logo","Company","Phone","Website","Email","Address")
            flags.forEach { label->
                val checked=when(label){"Logo"->band.showLogo;"Company"->band.showCompanyName;"Tagline"->band.showTagline;"Phone"->band.showPhone;"Website"->band.showWebsite;"Email"->band.showEmail;else->band.showAddress}
                Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){Checkbox(checked,{value->onChange(when(label){"Logo"->band.copy(showLogo=value);"Company"->band.copy(showCompanyName=value);"Tagline"->band.copy(showTagline=value);"Phone"->band.copy(showPhone=value);"Website"->band.copy(showWebsite=value);"Email"->band.copy(showEmail=value);else->band.copy(showAddress=value)})});Text(label)}
            }
            Text("Values come from each user's Profile, not from this template.",style=MaterialTheme.typography.bodySmall)
        }
    } }
}
