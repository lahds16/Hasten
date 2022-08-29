package lahds.hasten.ui

import android.view.View
import lahds.hasten.databinding.ActivityHomeBinding
import lahds.hasten.ui.components.BaseFragment

class HomeActivity : BaseFragment() {
    private lateinit var binding: ActivityHomeBinding

    override fun createView(): View {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        return binding.root
    }
}