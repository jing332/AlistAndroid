[![Release](https://github.com/jing332/AlistAndroid/actions/workflows/release.yaml/badge.svg)](https://github.com/jing332/AlistAndroid/actions/workflows/release.yaml)
[![Test](https://github.com/jing332/AlistAndroid/actions/workflows/build.yaml/badge.svg)](https://github.com/jing332/AlistAndroid/actions/workflows/build.yaml)
[![CheckAList](https://github.com/jing332/AlistAndroid/actions/workflows/sync_alist.yaml/badge.svg)](https://github.com/jing332/AlistAndroid/actions/workflows/sync_alist.yaml)

#### 🚩　[FRP](https://github.com/fatedier/frp) 安卓版本：https://github.com/jing332/FrpAndroid

# AlistAndroid

AlistAndroid是一个基于AList的Android客户端，使用Kotlin编写，使用Jetpack Compose作为UI框架。

> [Github Action](https://github.com/jing332/AlistAndroid/actions/workflows/sync_alist.yaml)
> 会每小时检查最新的 [AList发布](https://github.com/alist-org/alist/releases)
> 并自动构建APK，发布到 [Release](https://github.com/jing332/AlistAndroid/releases)
> 中，您只需耐心等待片刻并在应用内检查更新即可。

<img src="./images/1.jpg" height="150px">

# Download

- [Github Action (DEV)](https://github.com/jing332/AlistAndroid/actions/workflows/build.yaml)

> 分为两个版本
>
> LatestCommit: AList的[最新提交版](https://github.com/alist-org/alist) Github
> Actions会自动构建最新的AList代码，但是不保证稳定性
>
> LatestRelease: AList的[最新发布版](https://github.com/alist-org/alist/releases/latest) Github
> Actions会自动构建最新的AList发布版，保证稳定性。

---

- [Github Releases (Stable)](https://github.com/jing332/AlistAndroid/releases)

# Build

```shell
cd alist-lib/scripts
chmod +x *.sh
./install_alist.sh
./install_web.sh
./install_gomobile.sh
./install_aar.sh
```
> More: https://github.com/jing332/file/blob/main/android_repo_build.md

