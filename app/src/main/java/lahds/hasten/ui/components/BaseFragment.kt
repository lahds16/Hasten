package lahds.hasten.ui.components

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
            duration = 200L
        }
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, false
        ).apply {
            duration = 200L
        }
        enterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, true
        ).apply {
            duration = 200L
        }
        returnTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z, false
        ).apply {
            duration = 200L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView()
    }

    open fun createView(): View {
        return FrameLayout(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = LaunchActivity.auth
        database = LaunchActivity.database

        initialize()
    }

    open fun initialize() {}

    open fun presentFragment(fragment: BaseFragment, withBackStack: Boolean = false) {
        LaunchActivity.presentFragment(fragment, withBackStack)
    }
}