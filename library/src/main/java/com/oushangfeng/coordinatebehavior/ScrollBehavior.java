package com.oushangfeng.coordinatebehavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Oubowu on 2016/6/20 18:23.
 */
public class ScrollBehavior extends CoordinatorLayout.Behavior<View>{

    private ViewPropertyAnimator mViewPropertyAnimator;


    @IntDef
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollOrientation {

        int SCROLL_ORIENTATION_HORIZONTAL_SAME = 1,

        SCROLL_ORIENTATION_HORIZONTAL_REVERSE = 2,

        SCROLL_ORIENTATION_VERTICAL_SAME = 3,

        SCROLL_ORIENTATION_VERTICAL_REVERSE = 4;

    }

    @ScrollOrientation
    private int mScrollOrientation;

    private boolean mEnableAlphaAnim;

    private boolean mEnableTranslateAnim;

    private boolean mEnableScaleAnim;

    private boolean mEnableSmoothFling;

    private float mTotalOffset = 0;
    private boolean mIsScrolling;
    private int mChildTotalDistance;

    private Interpolator mInterpolator;

    private float mDamping;

    private int mAnimDuration;

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollBehavior);

        mScrollOrientation = ta.getInt(R.styleable.ScrollBehavior_scroll_orientation, ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME);

        mEnableAlphaAnim = ta.getBoolean(R.styleable.ScrollBehavior_enable_alpha, false);

        mEnableTranslateAnim = ta.getBoolean(R.styleable.ScrollBehavior_enable_translate, false);

        mEnableScaleAnim = ta.getBoolean(R.styleable.ScrollBehavior_enable_scale, false);

        mEnableSmoothFling = ta.getBoolean(R.styleable.ScrollBehavior_enable_smooth_fling, false);

        mDamping = ta.getFloat(R.styleable.ScrollBehavior_damping, 1);

        mAnimDuration = ta.getInt(R.styleable.ScrollBehavior_fling_anim_duration, 500);

        ta.recycle();

        mInterpolator = new AccelerateDecelerateInterpolator();

    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {

        switch (mScrollOrientation) {
            case ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_SAME:
            case ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE:
                return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
            case ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME:
            case ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE:
                return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        switch (mScrollOrientation) {
            case ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_SAME:
            case ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE:

                float old = mTotalOffset;

                dxConsumed = mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE ? dxConsumed : -dxConsumed;


                float now = mTotalOffset + dxConsumed * (mDamping > 0 ? mDamping > 1 ? 1 : mDamping : 1);

                if (mChildTotalDistance == 0) {
                    int childWidth = child.getWidth();
                    final ViewGroup.LayoutParams lp = child.getLayoutParams();
                    if (lp instanceof CoordinatorLayout.LayoutParams) {

                        final CoordinatorLayout.LayoutParams cLp = (CoordinatorLayout.LayoutParams) lp;

                        if ((cLp.gravity & Gravity.START) != 0) {
                            // 处于左边
                            childWidth += cLp.leftMargin;
                        } else if ((cLp.gravity & Gravity.END) != 0) {
                            // 处于右边
                            childWidth += cLp.rightMargin;
                        }
                    } else {
                        throw new RuntimeException("Use this behavior parent layout must be coordinatorLayout!");
                    }
                    mChildTotalDistance = childWidth;
                }

                if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE) {
                    now = Math.min(now, mChildTotalDistance);
                    now = Math.max(now, 0);
                } else {
                    now = Math.max(now, -mChildTotalDistance);
                    now = Math.min(now, 0);
                }

                if (old == now) {
                    mIsScrolling = false;
                    return;
                }

                mTotalOffset = now;

                // Log.e("TAG", "ScrollBehavior-172行-onNestedScroll(): " + mTotalOffset + " ; " + mChildTotalDistance);

                mIsScrolling = true;

                if (mEnableTranslateAnim) {
                    ViewCompat.setTranslationX(child, mTotalOffset);
                }

                if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE) {
                    if (mEnableAlphaAnim) {
                        ViewCompat.setAlpha(child, 1 - mTotalOffset * 1.0f / mChildTotalDistance);
                    }
                    if (mEnableScaleAnim) {
                        ViewCompat.setScaleX(child, 1 - mTotalOffset * 1.0f / mChildTotalDistance);
                        ViewCompat.setScaleY(child, 1 - mTotalOffset * 1.0f / mChildTotalDistance);
                    }

                } else {
                    if (mEnableAlphaAnim) {
                        ViewCompat.setAlpha(child, 1 + mTotalOffset * 1.0f / mChildTotalDistance);
                    }
                    if (mEnableScaleAnim) {
                        ViewCompat.setScaleX(child, 1 + mTotalOffset * 1.0f / mChildTotalDistance);
                        ViewCompat.setScaleY(child, 1 + mTotalOffset * 1.0f / mChildTotalDistance);
                    }
                }

                break;
            case ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME:
            case ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE:

                old = mTotalOffset;

                dyConsumed = mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE ? dyConsumed : -dyConsumed;

                now = mTotalOffset + dyConsumed * (mDamping > 0 ? mDamping > 1 ? 1 : mDamping : 1);

                if (mChildTotalDistance == 0) {
                    int childHeight = child.getHeight();
                    final ViewGroup.LayoutParams lp = child.getLayoutParams();
                    if (lp instanceof CoordinatorLayout.LayoutParams) {

                        final CoordinatorLayout.LayoutParams cLp = (CoordinatorLayout.LayoutParams) lp;

                        if ((cLp.gravity & Gravity.BOTTOM) != 0) {
                            // 处于左边
                            childHeight += cLp.bottomMargin;
                        } else if ((cLp.gravity & Gravity.TOP) != 0) {
                            // 处于右边
                            childHeight += cLp.topMargin;
                        }
                    } else {
                        throw new RuntimeException("Use this behavior parent layout must be coordinatorLayout!");
                    }
                    mChildTotalDistance = childHeight;
                }

                if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE) {
                    now = Math.min(now, mChildTotalDistance);
                    now = Math.max(now, 0);
                } else {
                    now = Math.max(now, -mChildTotalDistance);
                    now = Math.min(now, 0);
                }

                if (old == now) {
                    mIsScrolling = false;
                    return;
                }

                mTotalOffset = now;

                mIsScrolling = true;

                if (mEnableTranslateAnim) {
                    ViewCompat.setTranslationY(child, mTotalOffset);
                }

                if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE) {
                    if (mEnableAlphaAnim) {
                        ViewCompat.setAlpha(child, 1 - mTotalOffset * 1.0f / mChildTotalDistance);
                    }
                    if (mEnableScaleAnim) {
                        ViewCompat.setScaleX(child, 1 - mTotalOffset * 1.0f / mChildTotalDistance);
                        ViewCompat.setScaleY(child, 1 - mTotalOffset * 1.0f / mChildTotalDistance);
                    }

                } else {
                    if (mEnableAlphaAnim) {
                        ViewCompat.setAlpha(child, 1 + mTotalOffset * 1.0f / mChildTotalDistance);
                    }
                    if (mEnableScaleAnim) {
                        ViewCompat.setScaleX(child, 1 + mTotalOffset * 1.0f / mChildTotalDistance);
                        ViewCompat.setScaleY(child, 1 + mTotalOffset * 1.0f / mChildTotalDistance);
                    }
                }

                break;
        }

        ViewCompat.dispatchNestedScroll(child, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);

    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {

        if (mViewPropertyAnimator == null && mEnableSmoothFling) {
            mViewPropertyAnimator = child.animate().setInterpolator(mInterpolator).setDuration(mAnimDuration);
        }

        switch (mScrollOrientation) {
            case ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_SAME:
            case ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE:

                if (mChildTotalDistance == 0) {
                    int childWidth = child.getWidth();
                    final ViewGroup.LayoutParams lp = child.getLayoutParams();
                    if (lp instanceof CoordinatorLayout.LayoutParams) {

                        final CoordinatorLayout.LayoutParams cLp = (CoordinatorLayout.LayoutParams) lp;

                        if ((cLp.gravity & Gravity.START) != 0) {
                            // 处于底部
                            childWidth += cLp.leftMargin;
                        } else if ((cLp.gravity & Gravity.END) != 0) {
                            // 处于顶部
                            childWidth += cLp.rightMargin;
                        }
                    } else {
                        throw new RuntimeException("Use this behavior parent layout must be coordinatorLayout!");
                    }
                    mChildTotalDistance = childWidth;
                }

                if (mEnableSmoothFling) {

                    if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_HORIZONTAL_REVERSE) {
                        if (velocityX > 1 && mTotalOffset != mChildTotalDistance) {
                            // 向右快速滑动，隐藏按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (1 - mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = mChildTotalDistance;
                            playFlingAnim(mScrollOrientation, mChildTotalDistance, 0, 0);
                        } else if (velocityX < -1 && mTotalOffset != 0) {
                            // 向左快速滑动，显示按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = 0;
                            playFlingAnim(mScrollOrientation, 0, 1, 1);
                        }
                    } else {
                        if (velocityX > 1 && mTotalOffset != mChildTotalDistance) {
                            // 向上快速滑动，隐藏按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (1 + mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = -mChildTotalDistance;
                            playFlingAnim(mScrollOrientation, -mChildTotalDistance, 0, 0);
                        } else if (velocityX < -1 && mTotalOffset != 0) {
                            // 向下快速滑动，显示按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (-mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = 0;
                            playFlingAnim(mScrollOrientation, 0, 1, 1);
                        }
                    }

                } else {

                    if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE) {

                        if (velocityX > 1000 && mTotalOffset != mChildTotalDistance) {
                            // 向左快速滑动，隐藏按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationX(child, mChildTotalDistance);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 0);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 0);
                                ViewCompat.setScaleY(child, 0);
                            }
                            mTotalOffset = mChildTotalDistance;
                        } else if (velocityX < -1000 && mTotalOffset != 0) {
                            // 向右快速滑动，显示按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationX(child, 0);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 1);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 1);
                                ViewCompat.setScaleY(child, 1);
                            }
                            mTotalOffset = 0;
                        }

                    } else {

                        if (velocityX > 1000 && mTotalOffset != mChildTotalDistance) {
                            // 向上快速滑动，隐藏按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationX(child, -mChildTotalDistance);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 0);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 0);
                                ViewCompat.setScaleY(child, 0);
                            }
                            mTotalOffset = -mChildTotalDistance;
                        } else if (velocityX < -1000 && mTotalOffset != 0) {
                            // 向下快速滑动，显示按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationX(child, 0);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 1);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 1);
                                ViewCompat.setScaleY(child, 1);
                            }
                            mTotalOffset = 0;
                        }

                    }

                }
                ViewCompat.dispatchNestedFling(child, velocityX, velocityY, true);

                return true;
            case ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME:
            case ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE:

                if (mChildTotalDistance == 0) {
                    int childHeight = child.getHeight();
                    final ViewGroup.LayoutParams lp = child.getLayoutParams();
                    if (lp instanceof CoordinatorLayout.LayoutParams) {

                        final CoordinatorLayout.LayoutParams cLp = (CoordinatorLayout.LayoutParams) lp;

                        if ((cLp.gravity & Gravity.BOTTOM) != 0) {
                            // 处于底部
                            childHeight += cLp.bottomMargin;
                        } else if ((cLp.gravity & Gravity.TOP) != 0) {
                            // 处于顶部
                            childHeight += cLp.topMargin;
                        }
                    } else {
                        throw new RuntimeException("Use this behavior parent layout must be coordinatorLayout!");
                    }
                    mChildTotalDistance = childHeight;
                }

                if (mEnableSmoothFling) {

                    if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE) {
                        if (velocityY > 1 && mTotalOffset != mChildTotalDistance) {
                            // 向上快速滑动，隐藏按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (1 - mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = mChildTotalDistance;
                            playFlingAnim(mScrollOrientation, mChildTotalDistance, 0, 0);
                        } else if (velocityY < -1 && mTotalOffset != 0) {
                            // 向下快速滑动，显示按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = 0;
                            playFlingAnim(mScrollOrientation, 0, 1, 1);
                        }
                    } else {
                        if (velocityY > 1 && mTotalOffset != mChildTotalDistance) {
                            // 向上快速滑动，隐藏按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (1 + mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = -mChildTotalDistance;
                            playFlingAnim(mScrollOrientation, -mChildTotalDistance, 0, 0);
                        } else if (velocityY < -1 && mTotalOffset != 0) {
                            // 向下快速滑动，显示按钮
                            mViewPropertyAnimator.setDuration((long) (mAnimDuration * (-mTotalOffset * 1.0f / mChildTotalDistance)));
                            mTotalOffset = 0;
                            playFlingAnim(mScrollOrientation, 0, 1, 1);
                        }
                    }


                } else {

                    if (mScrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE) {

                        if (velocityY > 1000 && mTotalOffset != mChildTotalDistance) {
                            // 向上快速滑动，隐藏按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationY(child, mChildTotalDistance);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 0);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 0);
                                ViewCompat.setScaleY(child, 0);
                            }
                            mTotalOffset = mChildTotalDistance;
                        } else if (velocityY < -1000 && mTotalOffset != 0) {
                            // 向下快速滑动，显示按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationY(child, 0);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 1);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 1);
                                ViewCompat.setScaleY(child, 1);
                            }
                            mTotalOffset = 0;
                        }

                    } else {

                        if (velocityY > 1000 && mTotalOffset != mChildTotalDistance) {
                            // 向上快速滑动，隐藏按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationY(child, -mChildTotalDistance);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 0);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 0);
                                ViewCompat.setScaleY(child, 0);
                            }
                            mTotalOffset = -mChildTotalDistance;
                        } else if (velocityY < -1000 && mTotalOffset != 0) {
                            // 向下快速滑动，显示按钮
                            if (mEnableTranslateAnim) {
                                ViewCompat.setTranslationY(child, 0);
                            }
                            if (mEnableAlphaAnim) {
                                ViewCompat.setAlpha(child, 1);
                            }
                            if (mEnableScaleAnim) {
                                ViewCompat.setScaleX(child, 1);
                                ViewCompat.setScaleY(child, 1);
                            }
                            mTotalOffset = 0;
                        }

                    }

                }

                ViewCompat.dispatchNestedFling(child, velocityX, velocityY, true);

                return true;
        }

        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    private void playFlingAnim(int scrollOrientation, int translation, int alpha, int scale) {
        if (mEnableTranslateAnim) {
            if (scrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_REVERSE || scrollOrientation == ScrollOrientation.SCROLL_ORIENTATION_VERTICAL_SAME) {
                mViewPropertyAnimator.translationY(translation);
            } else {
                mViewPropertyAnimator.translationX(translation);
            }
        }
        if (mEnableAlphaAnim) {
            mViewPropertyAnimator.alpha(alpha);
        }
        if (mEnableScaleAnim) {
            mViewPropertyAnimator.scaleX(scale).scaleY(scale);
        }
        mViewPropertyAnimator.start();
    }


    public int getScrollOrientation() {
        return mScrollOrientation;
    }

    public void setScrollOrientation(@ScrollOrientation int scrollOrientation) {
        mScrollOrientation = scrollOrientation;
    }

    public boolean isEnableAlphaAnim() {
        return mEnableAlphaAnim;
    }

    public void setEnableAlphaAnim(boolean enableAlphaAnim) {
        mEnableAlphaAnim = enableAlphaAnim;
    }

    public boolean isEnableTranslateAnim() {
        return mEnableTranslateAnim;
    }

    public void setEnableTranslateAnim(boolean enableTranslateAnim) {
        mEnableTranslateAnim = enableTranslateAnim;
    }

    public boolean isEnableScaleAnim() {
        return mEnableScaleAnim;
    }

    public void setEnableScaleAnim(boolean enableScaleAnim) {
        mEnableScaleAnim = enableScaleAnim;
    }

    public boolean isEnableSmoothFling() {
        return mEnableSmoothFling;
    }

    public void setEnableSmoothFling(boolean enableSmoothFling) {
        mEnableSmoothFling = enableSmoothFling;
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    public int getChildTotalDistance() {
        return mChildTotalDistance;
    }

    public void setChildTotalDistance(int childTotalDistance) {
        mChildTotalDistance = childTotalDistance;
    }

    public void setFlingInterpolator(AccelerateDecelerateInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    public float getDamping() {
        return mDamping;
    }

    public void setDamping(float damping) {
        mDamping = damping;
    }

    public int getAnimDuration() {
        return mAnimDuration;
    }

    public void setAnimDuration(int animDuration) {
        mAnimDuration = animDuration;
    }

}
