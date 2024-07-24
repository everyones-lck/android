package umc.everyones.lck.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class ProfileRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "umc.everyones.lck.prefs"
        private const val KEY_PROFILE_IMAGE_URI = "profileImageUri"
    }

    fun setProfileImageUri(uri: Uri?) {
        sharedPreferences.edit().putString(KEY_PROFILE_IMAGE_URI, uri?.toString()).apply()
    }

    fun getProfileImageUri(): Uri? {
        val uriString = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null)
        return uriString?.let { Uri.parse(it) }
    }
}
