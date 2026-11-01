package com.ma.calculator.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.calculator.data.local.CalculationEntity
import com.ma.calculator.ui.theme.LocalCalculatorColors
import com.ma.calculator.ui.theme.PlusJakartaSans
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    historyList: List<CalculationEntity>,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSelectHistoryItem: (CalculationEntity) -> Unit,
    onDeleteHistoryItem: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    if (!isOpen) return

    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val timeFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = colors.textSecondary.copy(alpha = 0.4f)
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = colors.accentOrangeStart,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Calculation History",
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (historyList.isNotEmpty()) {
                    IconButton(
                        onClick = onClearAll,
                        modifier = Modifier.testTag("btn_clear_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear All History",
                            tint = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (historyList.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = colors.textSecondary.copy(alpha = 0.35f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No calculations yet",
                        color = colors.textPrimary,
                        fontSize = 17.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completed calculations will appear here for easy reuse",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaSans,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = colors.numberButton,
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.numberButtonBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSelectHistoryItem(item) }
                                .testTag("history_item_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.expression,
                                        color = colors.textSecondary,
                                        fontSize = 15.sp,
                                        fontFamily = PlusJakartaSans
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "= ${item.result}",
                                        color = colors.textPrimary,
                                        fontSize = 22.sp,
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = timeFormat.format(Date(item.timestamp)),
                                        color = colors.textSecondary.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        fontFamily = PlusJakartaSans
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Copy result
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Result", item.result)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied ${item.result}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy result",
                                            tint = colors.textSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Delete item
                                    IconButton(
                                        onClick = { onDeleteHistoryItem(item.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete item",
                                            tint = colors.textSecondary.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
