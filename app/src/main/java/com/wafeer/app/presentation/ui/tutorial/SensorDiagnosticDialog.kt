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
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "TargetPulseScale"
    )

    val activeColor by animateColorAsState(
        targetValue = if (diagnosticState.isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
        animationSpec = tween(200),
        label = "ActiveColor"
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
                            imageVector = Icons.Rounded.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "إخفاء الأرقام للخصوصية",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Phone Diagram showing exact sensor placement at top bezel
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top bezel with speaker and camera indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Target zone pulse around top earpiece
                            Box(
                                modifier = Modifier
                                    .size(width = 64.dp, height = 22.dp)
                                    .scale(if (diagnosticState.isNear) 1.05f else pulseScale)
                                    .background(activeColor.copy(alpha = 0.2f), RoundedCornerShape(11.dp))
                                    .border(1.5.dp, activeColor, RoundedCornerShape(11.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Camera dot
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(activeColor, CircleShape)
                                    )
                                    // Earpiece slit
                                    Box(
                                        modifier = Modifier
                                            .size(width = 18.dp, height = 4.dp)
                                            .background(activeColor, RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // Simulated phone screen preview
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (diagnosticState.isNear) Color(0xFF2E7D32).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (diagnosticState.isNear) Color(0xFF2E7D32).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (diagnosticState.isNear) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = null,
                                        tint = if (diagnosticState.isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = if (diagnosticState.isNear) "•••••• ج.م" else "2,450 ج.م",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (diagnosticState.isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Status banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (diagnosticState.isNear) Color(0xFF2E7D32).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (diagnosticState.isNear) Icons.Rounded.CheckCircle else Icons.Rounded.PanTool,
                            contentDescription = null,
                            tint = if (diagnosticState.isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (diagnosticState.isNear) {
                                "تم استشعار اليد، الأرقام محجوبة الآن"
                            } else {
                                "ضع باطن يدك ملاصقاً لأعلى الهاتف عند السماعة"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (diagnosticState.isNear) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Helpful natural explanation
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
                            text = "كيف تعمل الميزة؟",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "في معظم الهواتف الحديثة، الحساس مدمج عند سماعة المكالمات في أعلى الهاتف. لتفعيله بسهولة، غطِّ فتحة السماعة العلوية مباشرة بباطن يدك لثانية واحدة.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Direct Toggle Button
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
                        text = if (isCensored) "الأرقام مشوشة حالياً (إلغاء الإخفاء)" else "إخفاء الأرقام الآن بضغطة زر",
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
                    text = "فهمت الطريقة",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    )
}
