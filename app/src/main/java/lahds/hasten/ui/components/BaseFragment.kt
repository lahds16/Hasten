package lahds.hasten.ui.components

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import lahds.hasten.ui.LaunchActivity

open class BaseFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, true
        ).apply {
            duration = 300L
        }
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, false
        ).apply {
            duration = 300L
        }
        enterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, true
        ).apply {
            duration = 300L
        }
        returnTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, false
        ).apply {
            duration = 300L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = createView()
        parent.isClickable = true
        parent.isFocusable = true
        parent.setBackgroundColor(Theme.background)
        return parent
    }

    open fun createView(): View {
        return FrameLayout(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = LaunchActivity.auth
        database = LaunchActivity.database
        updateViews()
        initialize()
    }

    open fun initialize() {}

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        updateViews()
    }

    open fun updateViews() {}
}