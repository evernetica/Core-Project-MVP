package com.eight.core.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.eight.core.extension.uniqueName
import com.eight.core.presentation.Attachable
import com.eight.core.presentation.Messageable
import com.eight.core.presentation.duration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import toothpick.Scope
import toothpick.ktp.KTP

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment(), Attachable, Messageable {

    protected abstract val layoutResId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override var isAttached = false
        protected set

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (this is DialogInterface.OnShowListener) dialog.setOnShowListener(this)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createScope()
        installModules(scope)
        super.onCreate(savedInstanceState)
        scope.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAttached = true
    }

    override fun onDestroy() {
        KTP.closeScope(uniqueName)
        super.onDestroy()
    }

    override fun onDestroyView() {
        isAttached = false
        super.onDestroyView()
    }

    fun show(manager: FragmentManager) {
        val tag = this::class.java.simpleName
        if (manager.findFragmentByTag(tag) != null) return
        else show(manager, tag)
    }

    protected open fun createScope(): Scope {
        val parent = requireNotNull(parentFragment ?: activity).uniqueName
        return KTP.openScopes(parent, uniqueName)
    }

    protected open fun installModules(scope: Scope) = Unit

    // region Messageable
    override fun message(text: String, duration: Int, @Messageable.Type type: Int) {
        val d = duration(duration, type)

        when (type) {
            Messageable.SNACKBAR -> Snackbar.make(view ?: return, text, d).show()
            Messageable.TOAST,
            Messageable.UNDEFINED -> Toast.makeText(context ?: return, text, d).show()
        }
    }
    // endregion
}