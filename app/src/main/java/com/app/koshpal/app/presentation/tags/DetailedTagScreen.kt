package com.app.koshpal.app.presentation.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.app.koshpal.R
import com.app.koshpal.app.domain.model.*
import com.app.koshpal.app.presentation.globalcomponents.RingChart
import com.app.koshpal.app.domain.model.RingChartSegment
import com.app.koshpal.app.presentation.budget.component.DetailedCategoryCard
import com.app.koshpal.app.presentation.goals.component.GoalCard
import com.app.koshpal.app.presentation.tags.components.TagCategoryChip
import com.app.koshpal.app.presentation.tags.components.LegendItem
import com.app.koshpal.app.presentation.tags.components.ChartColors
import com.app.koshpal.app.presentation.transactions.component.TransactionBar
import com.app.koshpal.app.viewmodels.tagsviewmodel.TagsViewModel
import com.app.koshpal.ui.theme.Jakarta
import com.app.koshpal.ui.theme.Outfit
import com.app.koshpal.ui.theme.SetStatusBarAppearance
import com.app.koshpal.ui.theme.SetStatusBarVisibility
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.flowOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailedTagScreen(
    viewModel: TagsViewModel,
    onToPreviousScreen: () -> Unit,
    onToDetailedGoal: (String) -> Unit = {}
) {
    val analytics by viewModel.detailAnalytics.collectAsStateWithLifecycle()
    val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")) }

    var selectedCategoryForTransactions by remember { mutableStateOf<Category?>(null) }
    val categoryTransactionsState = remember(selectedCategoryForTransactions) {
        if (selectedCategoryForTransactions == null) flowOf(Transactions(emptyList()))
        else viewModel.getTransactionsForCategory(selectedCategoryForTransactions!!.id)
    }.collectAsStateWithLifecycle(Transactions(emptyList()))

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )
    val scope = rememberCoroutineScope()

    if (analytics == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val tag = analytics!!.tag
    val spent = analytics!!.totalSpent
    val baseColor = try {
        Color(tag.colorHex.toColorLong())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }
    val words = tag.name.trim().split("\\s+".toRegex())
    val initials = if (words.size >= 2) "${words[0].first()}${words[1].first()}" else "${words[0].first()}"

    var isStatusBarVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -15f && isStatusBarVisible) {
                    isStatusBarVisible = false
                }
                if (available.y > 15f && !isStatusBarVisible) {
                    isStatusBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    SetStatusBarAppearance(isDarkIcons = true)
    SetStatusBarVisibility(isVisible = isStatusBarVisible)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetDragHandle = {
            if (selectedCategoryForTransactions != null) {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline)
            }
        },
        sheetShadowElevation = 12.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            if (selectedCategoryForTransactions != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .padding(horizontal = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Transactions:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = selectedCategoryForTransactions!!.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (categoryTransactionsState.value.transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No transactions found", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(categoryTransactionsState.value.transactions) { txn ->
                                TransactionBar(
                                    transaction = txn,
                                    formatter = formatter,
                                    classificationName = tag.name,
                                    onTransactionClick = { }
                                )
                            }
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxWidth().height(1.dp))
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(baseColor.copy(alpha = 0.2f))
                .nestedScroll(nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        onClick = onToPreviousScreen
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                                contentDescription = "Back", 
                                modifier = Modifier.size(20.dp),
                                tint = baseColor
                            )
                        }
                    }
                    Text(
                        text = tag.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jakarta,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = baseColor
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.size(100.dp),
                    colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.2f)),
                    shape = CircleShape,
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = initials.uppercase(),
                            color = baseColor,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "₹${formatter.format(spent.toInt())}",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Jakarta,
                    color = baseColor
                )
                Surface(
                    modifier = Modifier.padding(top = 16.dp),
                    color = baseColor.copy(alpha = 0.25f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Across ${analytics!!.transactionCount} transactions",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Outfit,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
            Surface(
                modifier = Modifier.fillMaxSize().weight(0.75f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                           if(analytics!!.categories.isNotEmpty()) {
                               items(analytics!!.categories) { catAnalytics ->
                                   TagCategoryChip(
                                       name = catAnalytics.category.title,
                                       percentage = catAnalytics.percentage,
                                       tint = try {
                                           Color(catAnalytics.category.colorHex.toColorLong())
                                       } catch (_: Exception) {
                                           baseColor
                                       },
                                       icon = catAnalytics.category.iconResId?.toDrawableResId()
                                           ?: R.drawable.category_24px
                                   )
                               }
                           }
                           if (analytics!!.goals.isNotEmpty()){
                               items(analytics!!.goals) { goalsAnalytics ->
                                   TagCategoryChip(
                                       name = goalsAnalytics.title,
                                       percentage = goalsAnalytics.progressPercentage,
                                       tint = try {
                                           Color(goalsAnalytics.colorHex.toColorLong())
                                       } catch (_: Exception) {
                                           baseColor
                                       },
                                       icon = goalsAnalytics.iconResId.toDrawableResId()
                                           ?: R.drawable.category_24px
                                   )
                               }
                           }
                        }
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Budget used",
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontFamily = Outfit,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { analytics!!.progress },
                                    modifier = Modifier
                                        .weight(1f)
                                    .height(10.dp)
                                    .clip(CircleShape),
                                    color = baseColor,
                                    trackColor = baseColor.copy(alpha = 0.15f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${(analytics!!.progress * 100).toInt()}%", 
                                    fontWeight = FontWeight.Bold, 
                                    fontFamily = Jakarta, 
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (analytics!!.progress < 0.5) "You need to speed-up your savings" else "Almost there! You will reach your goal soon",
                                fontSize = 13.sp,
                                color =  MaterialTheme.colorScheme.outline,
                                fontFamily = Jakarta
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                "Summary",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RingChart(
                                    segments = if (analytics!!.categories.isNotEmpty()) {
                                        analytics!!.categories.map { catAnalytic ->
                                            val catColor = try { Color(catAnalytic.category.colorHex.toColorLong()) } catch (_: Exception) { baseColor }
                                            RingChartSegment(
                                                color = catColor, 
                                                percentage = if (analytics!!.totalAllotted > 0) (catAnalytic.spent / analytics!!.totalAllotted).toFloat() else 0f
                                            )
                                        }
                                    } else {
                                        analytics!!.goals.map { goal ->
                                            val goalColor = try { Color(goal.colorHex.toColorLong()) } catch (_: Exception) { baseColor }
                                            RingChartSegment(
                                                color = goalColor,
                                                percentage = if (analytics!!.totalAllotted > 0) (goal.savedAmount / analytics!!.totalAllotted).toFloat() else 0f
                                            )
                                        }
                                    },
                                    centerLabel = "Spent",
                                    centerValue = "${(analytics!!.progress * 100).toInt()}%",
                                    modifier = Modifier.size(110.dp)
                                )
                                Column(
                                    modifier = Modifier.padding(start = 45.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    if (analytics!!.categories.isNotEmpty()) {
                                        analytics!!.categories.forEachIndexed { index, catAnalytic ->
                                            LegendItem(
                                                name = catAnalytic.category.title,
                                                percent = catAnalytic.percentage.toDouble(), 
                                                color = try { Color(catAnalytic.category.colorHex.toColorLong()) } catch(_:Exception) { ChartColors[index % ChartColors.size] }
                                            )
                                        }
                                    }
                                    if (analytics!!.goals.isNotEmpty()){
                                        analytics!!.goals.forEachIndexed { index, goalAnalytic ->
                                            LegendItem(
                                                name = goalAnalytic.title,
                                                percent = goalAnalytic.progressPercentage.toDouble(),
                                                color = try { Color(goalAnalytic.colorHex.toColorLong()) } catch(_:Exception) { ChartColors[index % ChartColors.size] }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (analytics!!.goals.isNotEmpty()) {
                        item {
                            Text(
                                "Goals",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        items(analytics!!.goals, key = { it.id }) { goal ->
                            GoalCard(
                                goal = goal,
                                isEditing = false,
                                isIndividualEditing = "",
                                updateIsIndividualEditing = {},
                                isSelected = false,
                                addSelectedItem = {},
                                removeSelectedItem = {},
                                onDeleteGoal = { viewModel.deleteGoal(it) },
                                parent = "Tags",
                                onClick = { onToDetailedGoal(goal.id) }
                            )
                        }
                    }
                    if (analytics!!.categories.isNotEmpty()) {
                        item {
                            Text(
                                "Categories",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Jakarta,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    items(analytics!!.categories) { catAnalytics ->
                        val category = catAnalytics.category
                        val subCategoriesState = viewModel.getSubCategoriesForCategory(category.id)
                            .collectAsStateWithLifecycle(emptyList())
                        val subCategories = subCategoriesState.value
                        DetailedCategoryCard(
                            category = category,
                            subCategories = subCategories,
                            allottedAmount = catAnalytics.allotted,
                            usedAmount = catAnalytics.spent,
                            formatter = formatter,
                            onClick = {
                                selectedCategoryForTransactions = category
                                scope.launch { scaffoldState.bottomSheetState.expand() }
                            },
                            getSubCategorySpent = { viewModel.getSpentAmountForSubCategory(it) }
                        )
                    }
                }
            }
        }
    }
}
