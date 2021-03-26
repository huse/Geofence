package com.hus.geofence.locationreminders.savereminder.selectreminderlocation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavArgs
import com.hus.geofence.locationreminders.reminderslist.ReminderDataItem
import java.io.Serializable

class SelectLocationFragmentArgs(
    val reminder: ReminderDataItem? = null
) : NavArgs {
    @Suppress("CAST_NEVER_SUCCEEDS")
    fun toBundle(): Bundle {
        val result = Bundle()
        if (Parcelable::class.java.isAssignableFrom(ReminderDataItem::class.java)) {
            result.putParcelable("reminder", this.reminder as Parcelable?)
        } else if (Serializable::class.java.isAssignableFrom(ReminderDataItem::class.java)) {
            result.putSerializable("reminder", this.reminder as Serializable?)
        }
        return result
    }

    companion object {
        @JvmStatic
        fun fromBundle(bundle: Bundle): SelectLocationFragmentArgs {
            bundle.setClassLoader(SelectLocationFragmentArgs::class.java.classLoader)
            val __reminder : ReminderDataItem?
            if (bundle.containsKey("reminder")) {
                if (Parcelable::class.java.isAssignableFrom(ReminderDataItem::class.java) ||
                    Serializable::class.java.isAssignableFrom(ReminderDataItem::class.java)) {
                    __reminder = bundle.get("reminder") as ReminderDataItem?
                } else {
                    throw UnsupportedOperationException(ReminderDataItem::class.java.name +
                            " must implement Parcelable or Serializable or must be an Enum.")
                }
            } else {
                __reminder = null
            }
            return SelectLocationFragmentArgs(__reminder)
        }
    }
}