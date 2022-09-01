package lahds.hasten.ui

import android.content.res.ColorStateList
import android.view.View
import lahds.hasten.databinding.ActivityCreateChatBinding
import lahds.hasten.ui.components.BaseFragment
import lahds.hasten.ui.components.Theme

class CreateChatActivity : BaseFragment() {
    private lateinit var binding: ActivityCreateChatBinding

    override fun createView(): View {
        binding = ActivityCreateChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        binding.toolbar.setNavigationOnClickListener {
            LaunchActivity.activity.onBackPressed()
        }
    }

    override fun updateViews() {
        binding.toolbar.setBackgroundColor(Theme.toolbar)
        binding.toolbar.setNavigationIconTint(Theme.icon)
        binding.fab.backgroundTintList = ColorStateList.valueOf(Theme.primary)
    }
}