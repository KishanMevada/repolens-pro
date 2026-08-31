package com.repolenspro.util

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun AppPermissionHandler(
    permissions: List<String>,
    onAllGranted: () -> Unit = {},
    onDenied: (List<String>) -> Unit = {}
) {
    val context = LocalContext.current

    // ૧. પહેલા જ ચેક કરી લો કે બધી પરમિશન પહેલેથી આપેલી છે કે નહિ?
    val allPermissionsGranted = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // ૨. પરમિશન માંગવા માટેનો એડવાન્સ્ડ લોન્ચર (Multiple Permissions માટે)
    val permissionLauncher = rememberLauncherForActivityResult (
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // જે પરમિશન યુઝરે 'Deny' કરી હોય તેનું લિસ્ટ કાઢો
        val deniedPermissions = permissionsMap.filterValues { !it }.keys.toList()

        if (deniedPermissions.isEmpty()) {
            onAllGranted()
        } else {
            onDenied(deniedPermissions)
        }
    }

    // ૩. સ્ક્રીન ખૂલે એટલે તરત જ બેકગ્રાઉન્ડમાં લોન્ચરને કૉલ કરો
    LaunchedEffect(permissions) {
        if (!allPermissionsGranted) {
            // જો પરમિશન ના હોય, તો પોપ-અપ બતાવો
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            // જો પહેલેથી હોય, તો સીધું સક્સેસ ફંક્શન ચલાવો
            onAllGranted()
        }
    }
}