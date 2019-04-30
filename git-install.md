# GIT 安装及配置

## Ubuntu Linux 使用 apt-get 安装
```
$ sudo apt-get install git 
```

## Mac 上使用 xcode 安装
```
$ Xcode -> Menu -> Preferences -> Downloads -> Command Line Tools -> Install
```

## Windows 二进制安装
```
$ 下载安装文件（https://git-scm.com/downloads）。安装向导，一路默认
```

## 初始化 git
```
// 设置提交代码时的用户信息
// git 的配置文件为 .gitconfig，它可以在系统目录、用户主目录（全局配置），也可以在项目目录（项目配置）
$ git config [--global] user.name "[name]"
$ git config [--global] user.email "[email address]"
$ git config --list // 显示当前的Git配置
$ git config -e [--global]  // 编辑Git配置文件
```

## 生成 SSH 公钥/私钥
```
$ cd ~/.ssh
$ ssh-keygen [-t rsa -P '' -C 'example@email.com']

// 将 ~/.ssh/id_rsa.pub 分发到其他服务器上，并在其 shell 上执行：
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
$ chmod 600 ~/.ssh/authorized_keys

// 也可将 id_rsa.pub 提交到 github上(https://github.com/settings/ssh)
```

## Windows 下使用 TortoiseGit 生成 SSH 公钥/私钥
1. 运行 TortoiseGit 开始菜单中的 Puttygen 程序
2. 点击 Generate 按钮，鼠标在上图的空白地方来回移动直到进度条完毕，就会自动生一个随机的 key
3. 为密钥设置对应的访问密码，在 Key passphrase 和 Confirm passphrase 的后面的输入框中输入密码
4. 将多行文本框中以 ssh-rsa 开头的内容全选、复制，并粘贴到 github 的 Account Settings -> SSH Keys -> Add SSH key -> Key 字段中，这就是适用于 github 的公钥
5. 点击 Save private key 按钮，将生成的 key 保存为适用于 TortoiseGit 的私钥（扩展名为 .ppk）
6. 运行 TortoiseGit 开始菜单中的 Pageant 程序，程序启动后将自动停靠在任务栏中，双击该图标，弹出 key 管理列表
7. 点击 Add Key 按钮，将第 5 步保存的 .ppk 私钥添加进来，关闭对话框即可


## git config 的工具
* /etc/gitconfig 文件：系统中对所有用户都普遍适用的配置。若使用 git config 时用 --system 选项，读写的就是这个文件。
* ~/.gitconfig 文件：用户目录下的配置文件只适用于该用户。若使用 git config 时用 --global 选项，读写的就是这个文件。
* 当前项目的 git 目录中的配置文件（也就是工作目录中的 .git/config 文件）。这里的配置仅仅针对当前项目有效。每一个级别的配置都会覆盖上层的相同配置，所以 .git/config 里的配置会覆盖 /etc/gitconfig 中的同名变量。
* 在 Windows 系统上，Git 会找寻用户主目录下的 .gitconfig 文件。主目录即 $HOME 变量指定的目录，一般都是 C:\Documents and Settings\$USER。此外，Git 还会尝试找寻 /etc/gitconfig 文件，只不过看当初 Git 装在什么目录，就以此作为根目录来定位。

