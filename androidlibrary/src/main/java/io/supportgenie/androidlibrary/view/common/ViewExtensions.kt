package io.supportgenie.androidlibrary.view.common

import android.app.Activity
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.fragment.app.*
import kotlin.reflect.KClass

fun <T : ViewModel> FragmentActivity.getViewModel(modelClass: KClass<T>): T {
    return ViewModelProviders.of(this).get(modelClass.java)
}

fun <T : ViewModel> Fragment.getViewModel(modelClass: KClass<T>): T {
    return ViewModelProviders.of(this).get(modelClass.java)
}

fun AppCompatActivity.replaceFragment(viewGroupId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(viewGroupId, fragment)  // Replaces given view group with fragment
        .commit()
}

fun AppCompatActivity.addFragmentToState(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String
) {
    supportFragmentManager.beginTransaction()
        .add(containerViewId, fragment, tag)  // Stores fragment with given tag
        .commit()
}
