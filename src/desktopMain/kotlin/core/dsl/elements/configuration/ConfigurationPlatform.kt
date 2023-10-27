package core.dsl.elements.configuration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.bumble.appyx.navigation.integration.DesktopNodeHost
import core.dsl.elements.shortcut.shorts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import navigation.RootNode
import java.awt.Dimension

sealed class Events {
    object OnBackPressed : Events()
}

actual abstract class ConfigurationPlatform actual constructor() : Configuration() {

    @Composable
    actual open fun render(onCloseRequest: () -> Unit) {
        val events: Channel<Events> = Channel()
        val windowState = rememberWindowState(size = DpSize(480.dp, 658.dp))
        val eventScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

        Window(onCloseRequest, onKeyEvent = { keyEvent ->
            shorts.forEach {
                if (it.condition(keyEvent)) {
                    it.action()
                    return@Window true
                }
            }
            false
        }) {
            window.minimumSize = Dimension(800, 600)
            CompositionLocalProvider(LocalConfiguration provides this@ConfigurationPlatform as ConfigurationImpl) {
                val config = LocalConfiguration.current
                config.theme {
                    DesktopNodeHost(
                        windowState = windowState,
                        onBackPressedEvents = events.receiveAsFlow().mapNotNull {
                            if (it is Events.OnBackPressed) Unit else null
                        }
                    ) { buildContext ->
                        RootNode(buildContext)
                    }
                }
            }
        }
    }
}