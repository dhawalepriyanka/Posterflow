package com.example.ui.branding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

val PosterFlowBrandColor = Color(0xFF4F46E5)

@Composable
fun BrandWordmark(
    modifier: Modifier = Modifier,
    logoSize: Dp = 34.dp,
    fontSize: TextUnit = 31.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    letterSpacing: TextUnit = 0.5.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_posterflow_p),
            contentDescription = "PosterFlow logo",
            modifier = Modifier.size(logoSize)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.Black)) {
                    append("Poster")
                }
                withStyle(SpanStyle(color = PosterFlowBrandColor)) {
                    append("Flow")
                }
            },
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize,
                fontWeight = fontWeight,
                letterSpacing = letterSpacing
            )
        )
    }
}
