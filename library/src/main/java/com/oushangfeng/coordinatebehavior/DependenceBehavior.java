package com.oushangfeng.coordinatebehavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Oubowu on 2016/6/20 18:25.
 */
public class DependenceBehavior extends CoordinatorLayout.Behavior<View>{

    private int mDependenceScrollOrientation;

    private int mDependenceId;

    private int mRelativeToDependence;

    private int mRelativeOffset;

    @IntDef
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelativeToDependence {

        int TO_LEFT = 1,

        TO_RIGHT = 2,

        TO_TOP = 3,

        TO_BOTTOM = 4,

        ALIGN_LEFT = 5,

        ALIGN_RIGHT = 6,

        ALIGN_TOP = 7,

        ALIGN_BOTTOM = 8,

        FOLLOW = 9;

    }

    public DependenceBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DependenceBehavior);
        mDependenceId = ta.getResourceId(R.styleable.DependenceBehavior_dependence_id, -1);
        mDependenceScrollOrientation = ta
                .getInt(R.styleable.DependenceBehavior_dependence_scroll_orientation, ScrollBehavior.ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME);
        mRelativeToDependence = ta.getInt(R.styleable.DependenceBehavior_relative_to_dependence, RelativeToDependence.TO_LEFT);
        ta.recycle();
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency.getId() == mDependenceId) {
            if (mDependenceScrollOrientation == ScrollBehavior.ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE || mDependenceScrollOrientation == ScrollBehavior.ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME) {
                switch (mRelativeToDependence) {
                    case RelativeToDependence.FOLLOW:
                        setOffsetY(child, dependency);
                        break;
                    case RelativeToDependence.TO_LEFT:
                        child.setX(dependency.getX() - child.getWidth());
                        setOffsetY(child, dependency);
                        break;
                    case RelativeToDependence.TO_RIGHT:
                        child.setX(dependency.getX() + dependency.getWidth());
                        setOffsetY(child, dependency);
                        break;
                    case RelativeToDependence.TO_TOP:
                        child.setY(dependency.getY() - child.getHeight());
                        break;
                    case RelativeToDependence.TO_BOTTOM:
                        child.setY(dependency.getY() + dependency.getHeight());
                        break;
                    case RelativeToDependence.ALIGN_LEFT:
                        child.setX(dependency.getX());
                        setOffsetY(child, dependency);
                        break;
                    case RelativeToDependence.ALIGN_RIGHT:
                        child.setX(dependency.getX() - (child.getWidth() - dependency.getWidth()));
                        setOffsetY(child, dependency);
                        break;
                    case RelativeToDependence.ALIGN_TOP:
                        child.setY(dependency.getY());
                        break;
                    case RelativeToDependence.ALIGN_BOTTOM:
                        child.setY(dependency.getY() - (child.getHeight() - dependency.getHeight()));
                        break;
                }
            } else {
                switch (mRelativeToDependence) {
                    case RelativeToDependence.FOLLOW:
                        setOffsetY(child, dependency);
                        break;
                    case RelativeToDependence.TO_LEFT:
                        child.setX(dependency.getX() - child.getWidth());
                        break;
                    case RelativeToDependence.TO_RIGHT:
                        child.setX(dependency.getX() + dependency.getWidth());
                        break;
                    case RelativeToDependence.TO_TOP:
                        child.setY(dependency.getY() - child.getHeight());
                        setOffsetX(child, dependency);
                        break;
                    case RelativeToDependence.TO_BOTTOM:
                        child.setY(dependency.getY() + dependency.getHeight());
                        setOffsetX(child, dependency);
                        break;
                    case RelativeToDependence.ALIGN_LEFT:
                        child.setX(dependency.getX());
                        break;
                    case RelativeToDependence.ALIGN_RIGHT:
                        child.setX(dependency.getX() - (child.getWidth() - dependency.getWidth()));
                        break;
                    case RelativeToDependence.ALIGN_TOP:
                        child.setY(dependency.getY());
                        setOffsetX(child, dependency);
                        break;
                    case RelativeToDependence.ALIGN_BOTTOM:
                        child.setY(dependency.getY() - (child.getHeight() - dependency.getHeight()));
                        setOffsetX(child, dependency);
                        break;
                }
            }
            return true;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void setOffsetX(View child, View dependency) {
        if (mRelativeOffset == 0) {
            mRelativeOffset = (int) (child.getX() - dependency.getX());
        }
        child.setX(dependency.getX() + mRelativeOffset);
    }

    private void setOffsetY(View child, View dependency) {
        if (mRelativeOffset == 0) {
            mRelativeOffset = (int) (child.getY() - dependency.getY());
        }
        child.setY(dependency.getY() + mRelativeOffset);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return mDependenceId == dependency.getId();
    }

}
