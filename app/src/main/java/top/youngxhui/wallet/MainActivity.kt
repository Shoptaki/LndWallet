package top.youngxhui.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import top.youngxhui.wallet.lnd.callback.StartCallback
import top.youngxhui.wallet.ui.theme.WalletTheme


sealed class Screen(val route: String, val icon: ImageVector, @StringRes val resourceId: Int) {
    object Home : Screen("home", Icons.Default.Home, R.string.home)
    object Lightning : Screen("lightning", Icons.Default.Star, R.string.lightning)
    object Personal : Screen("personal", Icons.Default.Person, R.string.personal)
}

class MainActivity : ComponentActivity() {
    val items = listOf(
        Screen.Home,
        Screen.Lightning,
        Screen.Personal
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val startCallBack = remember { StartCallback() }
            val navController = rememberNavController()

            WalletTheme {
                Scaffold(bottomBar = {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.heightIn(min = 80.dp)
                    ) {

                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        items.forEach { screen ->
                            BottomNavigationItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(stringResource(screen.resourceId)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                unselectedContentColor = Color.Black.copy(0.4f),
                                selectedContentColor = Color.Black,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }) { innerPadding ->



                    NavHost(
                        navController,
                        startDestination = Screen.Home.route,
                        Modifier
                            .padding(innerPadding)
                            .then(Modifier.padding(horizontal = 16.dp))
                    ) {

                        composable(Screen.Home.route) { HomeScreen(navController) }
                        composable(Screen.Lightning.route) { BitcoinScreen(navController) }
                        composable(Screen.Personal.route) { PersonalScreen(navController) }
                    }
                }

            }
        }
    }
}
