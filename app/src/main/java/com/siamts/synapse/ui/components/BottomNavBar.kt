package com.siamts.synapse.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.navigation.NavTabs
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SurfaceBorder

data class BottomNavItem(
    val title: String,
    val icon: String,
    val tabIndex: Int,
    val route: String
)

val BottomNavItems = listOf(
    BottomNavItem(title = "Home", icon = "🏠", tabIndex = NavTabs.HOME, route = "home"),
    BottomNavItem(title = "Learn", icon = "📖", tabIndex = NavTabs.LEARN, route = "learn"),
    BottomNavItem(title = "Quiz", icon = "❓", tabIndex = NavTabs.QUIZ, route = "quiz"),
    BottomNavItem(title = "Notes", icon = "📝", tabIndex = NavTabs.NOTES, route = "notes"),
    BottomNavItem(title = "Profile", icon = "👤", tabIndex = NavTabs.PROFILE, route = "profile")
)

/**
 * Unified, persistent Bottom Navigation Bar for SYNAPSE AI.
 * Styled in Dark Sunset Amber (#13141B) with active glowing orange (#FF6A00)
 * and inactive muted grey (#8E92A4).
 */
@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
            BottomNavItems.forEach { item ->
                val isSelected = item.tabIndex == selectedTab
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onTabSelect(item.tabIndex)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.icon,
                        fontSize = 18.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.title,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
}
