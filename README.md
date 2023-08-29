# dillon-boot-fx

javafx权限管理系统UI

## 简介

本项目是一套权限管理系统的用户界面(UI)实现，采用 JavaFX 技术开发。该系统的用户界面参考了若依前端框架基于 RuoYi-Vue 的设计风格，旨在提供一套免费使用的权限管理系统。

以下是本项目使用的技术栈和相关组件：

应用程序结构：采用 mvvmFX 框架，该框架基于 MVVM (Model-View-ViewModel) 架构模式，用于实现数据绑定和视图模型的管理。

主题：使用 atlantafx 主题库，该主题库提供了一套现代化、响应式的用户界面风格，帮助美化系统的外观和用户体验。

组件库：初期采用 MaterialFX 组件库，但目前暂时不使用，因为作者正在进行重构工作。组件库用于提供常见的界面组件和交互元素，以简化开发过程。

图标库：使用 ikonli 图标库，该图标库提供了丰富的矢量图标集合，可用于系统的图标显示和按钮等元素的装饰。

动画库：采用 AnimateFX 动画库，该库提供了多种动画效果，可以为系统的界面元素添加各种动态效果，增强用户体验。

HTTP库：使用 OpenFeign HTTP库，该库提供了方便的 HTTP 请求和响应处理功能，用于与后端服务器进行通信和数据交互。

本项目的目标是提供一套完整的、易于使用的权限管理系统UI，适用于个人用户和企业用户。界面设计参考了若依前端基于 RuoYi-Vue 的设计，以提供现代化、直观的用户界面。同时，使用了多种技术和组件来增强用户体验，包括数据绑定、主题化、图标和动画等方面。

请注意，本项目仅提供用户界面(UI)部分的实现，后端采用了若依/RuoYi-Cloud 框架作为后台支持。如果需要完整的权限管理系统，需要结合后端框架使用。

* 界面参考若依前端(基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue))
* 后端采用[若依/RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud)
* 前端技术栈:
    * 应用程序结构 [mvvmFX](https://github.com/sialcasa/mvvmFX) ([文档](https://github.com/sialcasa/mvvmFX/wiki))
    * 主题 [atlantafx](https://github.com/mkpaz/atlantafx) ([文档](https://mkpaz.github.io/atlantafx/))
    * 组件库 [MaterialFX](https://github.com/palexdev/MaterialFX)（组件都是用原生的，暂时不用MaterialFX，等待作者重构完成！）
    * 图标库 [ikonli](https://github.com/kordamp/ikonli) ([文档](https://kordamp.org/ikonli/))
    * 动画库 [AnimateFX](https://github.com/Typhon0/AnimateFX) ([文档](https://github.com/Typhon0/AnimateFX/wiki))
    * http库 [OpenFeign](https://github.com/OpenFeign/feign)

## 启动说明

```agsl
    1 在idea右侧栏找到Maven，展开并点击Plugins->sass-cli:run，会编译出index.css
    2 运行主类org.dillon.fx.DillonBootFxApplication即可
```

# showcase:

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/show.gif)

# 界面：

### 登录

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/login.jpg)

### 主页

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/home-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/home-light.png)


### 用户管理

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/user-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/user-light.jpg)

### 角色管理
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/role-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/role-light.jpg)

### 菜单管理
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/menu-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/menu-light.jpg)

### 部门管理
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/dept-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/dept-light.jpg)

### 岗位管理
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/post-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/post-light.jpg)

### 字典类型
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/dict-type-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/dict-type-light.jpg)

### 字典数据
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/dict-data-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/dict-data-light.jpg)

### 参数管理
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/config-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/config-light.jpg)

### 通知公告
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/notice-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/notice-light.jpg)

### 操作日志
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/operlog-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/operlog-light.jpg)

### 登录日志
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/logininfo-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/logininfo-light.jpg)

### 服务监控
![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/monit-dark.jpg)

![Image text](https://gitee.com/lwdillon/dillon-boot-fx/raw/main/readme/moint-light.jpg)


## 交流群
## QQ群：114697782 QQ2群：808309284 QQ群：518914410
