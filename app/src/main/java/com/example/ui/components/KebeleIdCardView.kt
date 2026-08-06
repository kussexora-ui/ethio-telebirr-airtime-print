package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.db.IdCardEntity
import com.example.ui.theme.*
import com.example.util.QrCodeUtils

import com.example.util.LanguageUtils

@Composable
fun KebeleIdCardView(
    card: IdCardEntity,
    modifier: Modifier = Modifier,
    localLanguageCode: String = "am",
    isBackSideDefault: Boolean = false,
    showFlipButton: Boolean = true
) {
    var isBackVisible by remember { mutableStateOf(isBackSideDefault) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card Flip Toggle Button
        if (showFlipButton) {
            Row(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { isBackVisible = !isBackVisible }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Flip Card",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBackVisible) "የፊት ገፅ (Front Side)" else "የጀርባ ገፅ (Back Side)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ID Card Box Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.58577f) // ISO/IEC 7810 ID-1 standard ratio (85.60mm x 53.98mm)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFD))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFF1F5F9),
                                Color(0xFFE2E8F0)
                            )
                        )
                    )
            ) {
                // Background Ethiopian Ribbon Top & Bottom
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Ethiopian Ribbon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(EthioGreen))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(EthioYellow))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(EthioRed))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom Ethiopian Ribbon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(EthioGreen))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(EthioYellow))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(EthioRed))
                    }
                }

                // Front Side Content vs Back Side Content
                if (!isBackVisible) {
                    KebeleIdFrontContent(card = card, localLanguageCode = localLanguageCode)
                } else {
                    KebeleIdBackContent(card = card, localLanguageCode = localLanguageCode)
                }

                // SAMPLE Watermark Overlay if unpaid
                if (card.paymentStatus != "APPROVED") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.03f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.rotate(-22f)
                        ) {
                            Text(
                                text = "SAMPLE / ናሙና",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xCCDC2626), // Semi-transparent bold red
                                letterSpacing = 3.sp
                            )
                            Surface(
                                color = Color(0xCCDC2626),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = if (card.paymentStatus == "PENDING") "በምርመራ ላይ (PENDING APPROVAL)" else "ያልተከፈለ (UNPAID)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AspectRatio,
                    contentDescription = "Standard Size",
                    tint = EthioNavy,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ዓለም አቀፍ ደረጃውን የጠበቀ ID-1 መጠን (ISO/IEC 7810: 85.60 × 53.98 mm)",
                    fontSize = 10.sp,
                    color = EthioNavy,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun KebeleIdFrontContent(
    card: IdCardEntity,
    localLanguageCode: String = "am"
) {
    val context = LocalContext.current
    val labels = remember(localLanguageCode) { LanguageUtils.getIdLabels(localLanguageCode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Federal & Regional Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ethiopian Emblem Crest Icon
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(EthioNavy)
                    .border(1.dp, EthioYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Emblem",
                    tint = EthioYellow,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                val headerTitle = if (card.customRegionHeader.isNotBlank()) card.customRegionHeader else labels.cardTitle
                Text(
                    text = headerTitle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy,
                    maxLines = 2,
                    lineHeight = 11.sp
                )
                Text(
                    text = "${card.region} • ${card.zoneSubCity} • ${card.woreda} ${card.kebele}",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EthioGreen,
                    maxLines = 1
                )
            }

            // Hologram Badge
            Surface(
                color = Color(0xFF0F766E),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.border(1.dp, Color(0xFF5EEAD4), RoundedCornerShape(6.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Hologram",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "ETHIO ID",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 3.dp),
            thickness = 1.dp,
            color = Color(0xFFCBD5E1)
        )

        // Middle Section: Photo + Dynamic Bilingual Fields
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            // Photo Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(72.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 68.dp, height = 82.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, EthioNavy, RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!card.photoUri.isNull_or_blank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(card.photoUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "User Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Photo Placeholder",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "ፎቶ",
                                fontSize = 8.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // ID Number Badge
                Surface(
                    color = EthioNavy,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = card.idNumber,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioYellow,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Dynamic Bilingual Text Details Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Name Row
                TextDetailRow(
                    label = labels.nameLabel,
                    value = "${card.fullNameAmharic} (${card.fullNameEnglish})",
                    isHighlight = true
                )

                // Sex / DOB Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TextDetailRow(label = labels.sexLabel, value = card.gender)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TextDetailRow(label = labels.dobLabel, value = card.dob)
                    }
                }

                // Address Row
                TextDetailRow(
                    label = labels.addressLabel,
                    value = "${card.region}, ${card.zoneSubCity}, ${card.woreda}, No. ${card.houseNo}"
                )

                // Dates Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Renewal Date
                    Column {
                        Text(labels.renewalLabel, fontSize = 6.5.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Text(card.renewalDate, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    // Automated Expiration (+1 Year Highlight)
                    Surface(
                        color = Color(0xFFDC2626).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFDC2626))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)) {
                            Text(labels.expiryLabel, fontSize = 6.5.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Black)
                            Text(card.expiryDate, fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFB91C1C))
                        }
                    }
                }
            }
        }

        // Bottom Stamp & Official Signature Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Stamp Seal Graphics
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, Color(0xFF003399), CircleShape)
                        .background(Color(0xFF003399).copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Kebele Seal",
                            tint = Color(0xFF003399),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = labels.sealLabel,
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003399)
                    )
                    Text(
                        text = card.woreda,
                        fontSize = 6.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Administrator Signature & Name
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = card.officialName,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive,
                    color = EthioNavy
                )
                HorizontalDivider(
                    modifier = Modifier.width(65.dp),
                    thickness = 0.8.dp,
                    color = EthioNavy
                )
                Text(
                    text = labels.signatureLabel,
                    fontSize = 6.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun KebeleIdBackContent(
    card: IdCardEntity,
    localLanguageCode: String = "am"
) {
    val labels = remember(localLanguageCode) { LanguageUtils.getIdLabels(localLanguageCode) }
    val qrBitmap = remember(card.qrCodeData, card.idNumber) {
        val payload = if (card.qrCodeData.isNotBlank()) card.qrCodeData
        else "ETHIO-ID|${card.idNumber}|${card.fullNameEnglish}|EXP:${card.expiryDate}"
        QrCodeUtils.generateQrBitmap(payload, 200, 200)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = labels.backHeader,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EthioNavy
            )
            Surface(
                color = EthioGreen,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${labels.bloodTypeLabel}: ${card.bloodType}",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color(0xFFCBD5E1))

        // Body: QR Code & Emergency Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR Code Box
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Security QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${labels.emergencyLabel}:",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = card.emergencyContact,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${labels.packageTypeLabel}:",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = if (card.packageType == "DIGITAL_COPY") "ዲጂታል ኮፒ (Softcopy - 50 ETB)" else "የታተመ ህትመት (Hardcopy - 400 ETB)",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )
            }
        }

        // Legal Disclaimer
        Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = labels.legalDisclaimer,
                fontSize = 6.5.sp,
                color = Color(0xFF475569),
                modifier = Modifier.padding(5.dp),
                lineHeight = 8.5.sp
            )
        }
    }
}

@Composable
private fun TextDetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 1.dp)) {
        Text(
            text = label,
            fontSize = 7.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = if (isHighlight) 9.5.sp else 8.5.sp,
            fontWeight = if (isHighlight) FontWeight.Black else FontWeight.Bold,
            color = if (isHighlight) EthioNavy else TextPrimary,
            maxLines = 1
        )
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.isBlank()
}
