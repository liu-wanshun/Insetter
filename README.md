# Insetter

[![](https://jitpack.io/v/com.gitee.liu_wanshun/Insetter.svg)](https://jitpack.io/#com.gitee.liu_wanshun/Insetter)

	一个完全无侵入、简单易用的处理WindowInsets的库。
	https://developer.android.com/reference/android/view/WindowInsets.html。

## Gradle

**1.** 引入jitpack仓库

```kotlin
repositories {
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}
```

**2.** 添加依赖

```kotlin
dependencies {
      implementation ("com.gitee.liu_wanshun:Insetter:0.0.1")
}
```



## 用法

**1.** 在布局文件中添加以下属性即可，无需任何代码，这些属性完全来自于[Material组件库](https://github.com/material-components/material-components-android)，所以这个Insetter库可以做到完全无侵入。

- `app:paddingLeftSystemWindowInsets`: 将[system window insets](https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()) 的 left 应用于视图的`paddingLeft`.
- `app:paddingTopSystemWindowInsets`：将[system window insets](https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()) 的 top 应用于视图的`paddingTop`.
- `app:paddingRightSystemWindowInsets`: 将[system window insets](https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()) 的 right 应用于视图的`paddingRight`.
- `app:paddingBottomSystemWindowInsets`：将[system window insets](https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()) 的 bottom 应用于视图的`paddingBottom`.

例如：

```xml
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:paddingBottomSystemWindowInsets="true">

        <Button
            android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="Button" />

    </FrameLayout>
```


**2.** 如果不生效，请在加载布局前，也就是调用inflate方法之前调用inject方法

```kotlin
Insetter.inject(context)
```



## 更新日志

[Releases](https://gitee.com/liu_wanshun/Insetter/releases)



## 感谢

- [insetter](https://github.com/chrisbanes/insetter)
- [BackgroundLibrary](https://github.com/JavaNoober/BackgroundLibrary)
- [material-components-android](https://github.com/material-components/material-components-android)


## License

```
Copyright (C) 2022. liuwanshun

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