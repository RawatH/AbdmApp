package org.commcare.dalvik.abha.ui.main.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R

class TimeProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var counter: Int = 0
    val pb: CircularProgressIndicator by lazy { findViewById(R.id.progressBar) }
    val counterView: TextView by lazy { findViewById(R.id.progressCount) }
    val timestate:MutableStateFlow<ProgressState> = MutableStateFlow(ProgressState.TimeoutOver)


    fun getMaxAllowedCount(): Int = pb.max

    init {
        inflate(getContext(), R.layout.number_progress, this)
        attrs?.let {
            context.obtainStyledAttributes(
                it,
                R.styleable.custom_number_progress_attributes, 0, counter
            ).apply {
                try {
                    pb.max =
                        getInt(R.styleable.custom_number_progress_attributes_maxCounter, 60)
                    pb.progress = pb.max
                } finally {
                    recycle()
                }
            }
        }
//        visibility = GONE
    }

    fun startTimer(){
        CoroutineScope(Dispatchers.Main).launch {
            timestate.emit(ProgressState.TimeoutStarted)
            visibility = View.VISIBLE
            while (pb.progress > 0){
                delay(100)
                pb.progress = pb.progress - 1
                counterView.text = pb.progress.toString()
            }
            visibility = View.GONE
            timestate.emit(ProgressState.TimeoutOver)
        }
    }
}

sealed class ProgressState {
    object TimeoutStarted:ProgressState()
    object TimeoutOver:ProgressState()
}
