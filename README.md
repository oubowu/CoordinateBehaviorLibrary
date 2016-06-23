##一个协调布局的行为库

###ScrollBehavior支持属性

| scroll_orientation 跟随滚动目标的滚动方向     | 
|---                                            | 
| horizontal_same 目标水平滚动跟随它的方向      |
| horizontal_reverse 目标水平滚动跟随它的反方向 |
| vertical_same 目标垂直滚动跟随它的方向        |
| vertical_reverse 目标垂直滚动跟随它的反方向   |

```
enable_alpha 是否开启滚动透明度渐变
```
```
enable_scale 是否开启滚动缩放渐变
```
```
enable_translate 是否开启滚动位移渐变
```
```
enable_smooth_fling 是否开启惯性滚动时顺滑滚动
```
```
damping 0~1，目标滑动距离*damping，类似阻尼
```   
```
fling_anim_duration 若开启惯性滚动时顺滑滚动，可以设置此动画时长
```

### DependenceBehavior支持属性
```
dependence_id 依赖的目标id，用于决定跟随哪个View滚动
```
| dependence_scroll_orientation 依赖的滚动目标的滚动方向  | 
|---                                            | 
| horizontal_same 依赖的目标水平滚动方向      |
| horizontal_reverse 依赖的目标水平滚动方向 |
| vertical_same 依赖的目标垂直方向        |
| vertical_reverse 依赖的目标垂直方向   |

| relative_to_dependence 相对与依赖的滚动目标位置  | 
|---                                            | 
| toLeft 在依赖的左边      |
| toRight 在依赖的右边 |
| toTop 在依赖的上边        |
| toBottom 在依赖的下边   |
| alignLeft 对齐依赖的左边      |
| alignRight 对齐依赖的右边 |
| alignTop 对齐依赖的上边        |
| alignBottom 对齐依赖的下边   |
| follow 仅仅跟随滚动   |

### Gradle
```groovy
compile 'com.oushangfeng:CoordinateBehavior:1.0.0.1'
```

### 使用方式
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:context="com.oushangfeng.coordinatebehaviordemo.MainActivity">

    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/AppTheme.AppBarOverlay">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay"/>

    </android.support.design.widget.AppBarLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="?attr/actionBarSize"
        android:background="#e3e3e3"
        android:orientation="vertical">

        <!--水平滑动的RecyclerView-->
        <android.support.v7.widget.RecyclerView
            android:id="@+id/horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:background="#454545">

        </android.support.v7.widget.RecyclerView>

        <!--垂直滑动的RecyclerView-->
        <android.support.v7.widget.RecyclerView
            android:id="@+id/vertical"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:layout_gravity="center_horizontal"
            android:layout_marginTop="10dp"
            android:background="#989898"
            android:paddingLeft="50dp"
            android:paddingRight="50dp">

        </android.support.v7.widget.RecyclerView>

    </LinearLayout>

    <!--跟随垂直滑动的RecyclerView做相反方向的按钮-->
    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_marginBottom="40dp"
        android:layout_marginRight="@dimen/fab_margin"
        android:src="@android:drawable/ic_menu_today"
        app:damping="0.5"
        app:enable_alpha="true"
        app:enable_scale="true"
        app:enable_smooth_fling="true"
        app:enable_translate="true"
        app:layout_behavior="com.oushangfeng.coordinatebehavior.ScrollBehavior"
        app:scroll_orientation="vertical_reverse"/>

    <!--跟随fab-->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        android:background="#ff345566"
        android:gravity="center"
        android:padding="12dp"
        android:text="跟随按钮"
        android:textColor="#000000"
        android:textSize="12dp"
        app:dependence_id="@+id/fab"
        app:dependence_scroll_orientation="vertical_reverse"
        app:layout_behavior="com.oushangfeng.coordinatebehavior.DependenceBehavior"
        app:relative_to_dependence="follow"/>

    <!--跟随水平滑动的RecyclerView做相反方向的按钮-->
    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="top|end"
        android:layout_margin="@dimen/fab_margin"
        android:src="@android:drawable/ic_menu_save"
        app:damping="0.5"
        app:enable_alpha="true"
        app:enable_scale="true"
        app:enable_smooth_fling="true"
        app:enable_translate="true"
        app:layout_behavior="com.oushangfeng.coordinatebehavior.ScrollBehavior"
        app:scroll_orientation="horizontal_reverse"/>

    <!--跟随水平滑动的RecyclerView做相同方向的按钮-->
    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="top|start"
        android:layout_margin="@dimen/fab_margin"
        android:src="@android:drawable/ic_menu_save"
        app:damping="0.5"
        app:enable_alpha="true"
        app:enable_scale="true"
        app:enable_smooth_fling="true"
        app:enable_translate="true"
        app:layout_behavior="com.oushangfeng.coordinatebehavior.ScrollBehavior"
        app:scroll_orientation="horizontal_same"/>

</android.support.design.widget.CoordinatorLayout>
```
### 示例展示
![](/pic/demo.gif) 

#### License
```
Copyright 2016 oubowu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




