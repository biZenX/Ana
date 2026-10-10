package com.wafeer.app.presentation.ui.tutorial

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafeer.app.R
import com.wafeer.app.presentation.util.LocalCensorMode
import com.wafeer.app.presentation.util.ProximityDiagnosticState

@Composable
fun SensorDiagnosticDialog(
    diagnosticState: ProximityDiagnosticState,
    onToggleCensor: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isCensored = LocalCensorMode.current
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "RadarScale"
    )

    val radarColor by animateColorAsState(
        targetValue = if (diagnosticState.isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
        animationSpec = tween(200),
        label = "RadarColor"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Sensors,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "فاحص مستشعر الخصوصية",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Interactive Radar Meter
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(if (diagnosticState.isNear) 1.05f else pulseScale)
                        .background(radarColor.copy(alpha = 0.12f), CircleShape)
                        .border(2.dp, radarColor.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .background(radarColor.copy(alpha = 0.25f), CircleShape)
                            .border(2.5.dp, radarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (diagnosticState.isNear) Icons.Rounded.CheckCircle else Icons.Rounded.Sensors,
                            contentDescription = null,
                            tint = radarColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Status banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (diagnosticState.isNear) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (diagnosticState.isNear) {
                                "🟢 تم رصد اليد! المستشعر في هذا الموضع"
                            } else {
                                "⚪ حرّك يدك ببطء حول الحافة العلوية أو سماعة الأذن"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (diagnosticState.isNear) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (diagnosticState.currentDistance >= 0f) {
                                "المسافة المقروءة: ${if (diagnosticState.isNear) "0" else String.format("%.1f", diagnosticState.currentDistance)} سم"
                            } else {
                                "بانتظار حركة اليد بالقرب من الهاتف..."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (diagnosticState.isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Dynamic Hardware Diagnostic Card
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "بيانات المستشعر المكتشفة في جهازك:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "• الاسم: ${diagnosticState.sensorName.ifBlank { "مستشعر القرب" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (diagnosticState.vendor.isNotBlank()) {
                            Text(
                                text = "• الجهة المصنعة: ${diagnosticState.vendor}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "• النوع: ${if (diagnosticState.isVirtual) "افتراضي بالموجات / شاشة (Virtual)" else "عتادي بصري (Hardware)"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "ملاحظة: تختلف الهواتف في موضع المستشعر؛ فبعضها يضعه بجوار الكاميرا، وبعضها داخل فتحة سماعة المكالمات العلوية، والبعض أسفل الشاشة. بتحريك يدك وملاحظة إشارة الرادار بالأعلى، ستكتشف مكانه الدقيق فوراً.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                // Quick Blur Test Button
                Button(
                    onClick = onToggleCensor,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCensored) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isCensored) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isCensored) "الأرقام مشوشة حالياً (اضغط لإلغاء التعتيم)" else "جرّب تشويش الأرقام الآن",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = "حسناً، عرفت الموضع",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    )
}
