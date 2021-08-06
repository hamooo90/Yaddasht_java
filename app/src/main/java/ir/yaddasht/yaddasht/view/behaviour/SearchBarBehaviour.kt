package ir.yaddasht.yaddasht.view.behaviour

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout

class SearchBarBehaviour(
        context: Context,
        attrs: AttributeSet
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private var previousY: Float = 0f

    override fun layoutDependsOn(
            parent: CoordinatorLayout,
            child: View, dependency: View
    ): Boolean = dependency is AppBarLayout

    override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: View,
            dependency: View
    ): Boolean {
        val currentY = dependency.y
        // When the AppBarLayout scrolls, scroll the RecyclerView for the same distance
        (child as SwipeRefreshLayout)
                .getChildAt(0)
                .scrollBy(0, (previousY - currentY).toInt())

        previousY = currentY
        return false
    }
}