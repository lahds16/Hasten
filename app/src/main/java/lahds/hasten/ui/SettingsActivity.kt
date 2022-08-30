package lahds.hasten.ui

import android.view.View
import lahds.hasten.databinding.ActivitySettingsBinding
import lahds.hasten.ui.components.BaseFragment

class SettingsActivity : BaseFragment() {
    private lateinit var binding: ActivitySettingsBinding

    override fun createView(): View {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initialize() {

    }
}