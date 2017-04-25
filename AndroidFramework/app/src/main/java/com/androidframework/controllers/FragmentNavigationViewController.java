package com.androidframework.controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import com.androidframework.base_classes.AbstractBaseFragment;

import java.util.ArrayList;


/**
 * This class is to Maintain Stack of Fragments to be pushed and pop
 */
public class FragmentNavigationViewController {

    //home fragments
    public static final byte FRAGMENT_NAME = 0;

    private ArrayList<Fragment> fragmentsStack = new ArrayList<>();
    private FragmentManager fragmentManager;
    private int container_id;


    public FragmentNavigationViewController(@NonNull FragmentManager fragmentManager, int container_id) {
        this.fragmentManager = fragmentManager;
        this.container_id = container_id;
    }

    /**
     * Method to launch the child fragment
     *
     * @param fragmentId
     * @param parcelExtras
     */
    public void push(int fragmentId, @Nullable Object parcelExtras) {
        AbstractBaseFragment abstractBaseFragment = null;
        boolean needPushAnimation = false;

        switch (fragmentId) {
//            case FRAGMENT_FOLLOW_US:
//                abstractBaseFragment = new FollowUsFragment();
//                needPushAnimation = true;
//                break;
        }

        //pushing
        abstractBaseFragment.setNavigationController(this);
        abstractBaseFragment.setParcelExtras(parcelExtras);
        push(abstractBaseFragment, needPushAnimation);
    }


    private void push(Fragment newFragmentToShow, boolean needDefaultAnim) {
        fragmentsStack.add(newFragmentToShow);

        FragmentTransaction childFragTrans = fragmentManager.beginTransaction();
        childFragTrans.replace(container_id, newFragmentToShow);

        if (needDefaultAnim) {
            childFragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        childFragTrans.addToBackStack(null);
        childFragTrans.commit();

    }

    /**
     * It replace the current fragment with the back stack if exist any
     *
     * @return : Tells whether any fragment returned or not.
     */
    public boolean pop() {
        //don't remove any fragment if stack have only one
        if (fragmentsStack.size() == 1) {
            return false;
        }

        fragmentsStack.remove(fragmentsStack.size() - 1);
        FragmentTransaction childFragTrans = fragmentManager.beginTransaction();
        childFragTrans.replace(container_id, fragmentsStack.get(fragmentsStack.size() - 1));
        childFragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        childFragTrans.addToBackStack(null);
        childFragTrans.commit();

        return true;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public ArrayList<Fragment> getFragmentsStack() {
        return fragmentsStack;
    }
}
