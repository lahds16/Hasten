package lahds.hasten.ui

import android.view.View
import lahds.hasten.databinding.ActivityCreateChatBinding
import lahds.hasten.ui.components.BaseFragment

class CreateChatActivity : BaseFragment() {
    private lateinit var binding: ActivityCreateChatBinding

    override fun createView(): View {
        binding = ActivityCreateChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {
        binding.toolbar.setNavigationOnClickListener {
            LaunchActivity.activity.supportFragmentManager.popBackStack()
        }
    }
}