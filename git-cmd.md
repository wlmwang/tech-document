# GIT 常用命令

## git 配置相关
```
// System: /etc/gitconfig  Global: ~/.gitconfig  Local: .git/config
$ git config --list
$ git config --global user.name "John Doe"
$ git config --global user.email johndoe@example.com

$ git config --global alias.st status
$ git config --global alias.co checkout
$ git config --global alias.ci commit
$ git config --global alias.br branch
```

## 克隆仓库
```
$ git clone git://github.com/wlmwang/tech-document.git
$ git clone git://github.com/wlmwang/tech-document.git tech-document
```

## 构建本地仓库
```
$ git init
$ git remote add origin git://github.com/wlmwang/tech-document.git
$ git remote remove origin
$ git remote -v
$ git remote show origin
```

## 日志查看
```
$ git show [commit_id] // 显示指定提交对象
$ git log -p [-2] // 展开显示每次提交对象
$ git log -p [-2] -- RELEASE-NOTE.md // 指定文件的所有修改记录
$ git log -p master..origin/dev // 指定两个分支差异
$ git log --oneline [-2] // 将每个提交放在一行显示
$ git log --graph // 简单图形
$ git log --stat  // 仅显示简要的增改行数统计
$ git log --grep keywords // 关键字搜索
```

## 分支差异
```
$ git diff [-- <path>…] // 默认比较工作区与暂存区差异
$ git diff HEAD [-- <path>…] // 比较工作区与当前本地版本库差异（当前分支最近一次提交）
$ git diff {--cached|--staged} [-- <path>…] // 比较暂存区与当前本地版本差异（当前分支最近一次提交）

$ git diff commit-id [-- <path>…] // 与某一次提交比较差异
$ git diff commit-id1 commit-id2 [-- <path>…] // 两次提交比较差异
$ git diff {--cached|--staged} commit-id [-- <path>…] // 比较暂存区与当前本地版本库中某次提交的差异

$ git diff dev [-- <path>…] // 比较当前分支与dev分支文件差异
```

## 添加到暂存区
```
$ git add . // 提交新(new)和修改(modified)文件，不包括被删除(deleted)文件
$ git add -u // 提交修改(modified)和被删除(deleted)文件，不包括新文件(new)
$ git add -A // 提交所有变化(git add --all 缩写)

// add 另一个功能是标记冲突已解决
```

## 提交仓库
```
$ git commit -m 'initial commit' // 提交暂存区到本地仓库
$ git commit -a -m 'initial commit' // 将所有已跟踪的执行修改或删除的文件都提交到本地仓库。包含 git add -u 操作
$ git commit --amend // 使用当前暂存区快照重新提交（覆盖最后一次提交记录）。若暂存区未有改动过，则有机会重新编辑提交说明
```

## 还原操作
```
$ git reset --soft HEAD^ // 用仓库中最后第二次提交，撤销最后一次提交；撤销的变更会进入暂存区，工作区的修改保留
$ git reset --hard HEAD^ // 用仓库中最后第二次提交，撤销最后一次提交以及工作区的修改；撤销的变更不会进入暂存区，工作区的修改也会丢失
$ git reset [--mixed] HEAD^ // 用仓库中最后第二次提交，撤销最后一次提交；撤销的变更不会进入暂存区，但工作区的修改保留

$ git reset [--mixed] HEAD benchmarks.rb // 用仓库最新提交，撤销暂存区变更，能达到让 git add 的文件回到未暂存状态
$ git reset commit-id // 以 commit-id 作为操作对象
```

```
$ git revert HEAD // 撤销前一次提交（产生一个新的 commit，不影响提交历史）
$ git revert HEAD^ // 撤销前前一次提交（产生一个新的commit，不影响提交历史）
$ git revert commit-id // commit-id 作为操作对象

// 解决 revert 撤销冲突
$ git revert commit-id // 遇到冲突，手动解决，进入下一步
$ git revert --continue  // 继续撤销
$ git revert --abort  // 任何时候都可以放弃 revert 操作
```

```
$ git checkout -- benchmarks.rb // 用暂存区文件，撤销工作区的修改
$ git checkout [-f] commit-id // 把暂存区与工作区全部更新为 commit-id 历史版本
```

## 删除文件
```
$ git rm readme.txt // 文件在工作区 && 暂存区被同时删除
$ git rm --cached readme.txt  // 只删除暂存区文件
```

## 重命名文件
```
$ git mv file_from file_to // 等同下面三条命令

$ mv file_from file_to // 1
$ git rm file_from  // 2
$ git add file_to  // 3
```

## 查看分支
```
$ git branch // 列出本地分支
$ git branch -r // 列出远程分支
$ git branch -vv // 查看本地分支对应的远程分支
```

## 查看标签
```
$ git tag [--list]  // 列出标签列表
$ git tag --list 'v1.0.*'  // 列出模式匹配的标签列表
$ git show v1.0.0 // 查看标签 v1.0.0 信息（连同显示打标签时的提交对象）
```

## 创建本地分支（基于本地分支、标签、远程仓库分支）
```
$ git branch dev // 基于当前分支，创建 dev 分支
$ git branch dev commit-id // 基于当前分支 commit-id 历史版本创建 dev 分支

$ git branch -d dev // 删除分支，-D 强制删除未合并的分支
$ git branch -m old-name new-name // 分支重命名

$ git fetch origin dev
$ git checkout -b dev origin/dev // 基于远程 origin/dev 分支，创建本地 dev 分支，并自动跟踪
$ git checkout -t origin/dev // 基于远程 origin/dev 分支，创建本地同名分支，并自动跟踪 --track

$ git fetch [-f] origin rbranch:lbranch // 拉取远程 origin/rbranch 分支代码，创建本地 lbranch 分支。并保存 origin/rbranch 分支的最新提交到 .git/FETCH_HEAD 文件中。若 lbranch 已存在，且不能和 origin/rbranch 分支进行 fast forward 合并，则命令执行失败。-f 则可以总是执行成功（本质上它先删除本地 lbranch 分支，再创建）

$ git fetch origin
$ git branch dev v0.0.1  // 基于标签 v0.0.1 ，创建 dev 分支（可先获取远程仓库所有标签）
```

## 创建远程仓库分支（推送）
```
$ git push origin dev // 将本地 dev 分支推送至远程 origin/dev 分支（不存在则新建）。会被扩展为 refs/heads/dev:refs/heads/dev。（注意：与当前所在分支无关）
$ git push -f origin dev // 同上。但会忽略冲突，强制推送。
$ git push -u origin dev // 同上。但会自动跟踪 --set-upstream

$ git push origin l-dev:r-dev // 将本地 l-dev 分支推送至远程 origin/r-dev 分支（不存在则新建），并自动跟踪

$ git push origin :dev // 删除远程 origin/dev 分支
$ git push origin --delete dev // 删除远程 origin/dev 分支
```

## 检出/新建本地分支
```
$ git checkout dev // 检出、切换到 dev 分支。本地分支不存在则报错
$ git checkout [-f] commit-id // 检出当前分支指定 commit-id 历史版本。-f 强制
$ git checkout --merge dev // 将当前分支的修改合并到，将要检出的分支 dev 上。当前分支修改丢失

$ git checkout -b dev // 基于当前分支，创建 dev 分支，并切换到 dev
$ git checkout -B dev // 相比 -b，不管 dev 是否存在，强制创建。本质上是先删除再创建

$ git fetch origin dev
$ git checkout -b dev origin/dev // 基于远程 origin/dev 分支，创建本地 dev 分支，并自动跟踪
$ git checkout -t origin/dev // 基于远程 origin/dev 分支，创建本地同名分支，并自动跟踪 --track
```

## 创建标签
```
$ git tag -a v1.0.0 -m "version tag 1.0.0" // 基于当前分支最新提交对象，创建含附注类型的 tag
$ git tag -a v1.0.1 -m "version tag 1.0.0" 9fceb02  // 基于当前分支某个提交对象 commit-id，创建含附注类型的 tag
```

## 推送标签
```
$ git push origin v1.0.0 // 推送本地 v1.0.0 标签到远程仓库同名标签
$ git push origin --tags // 推送本地所有标签到远程仓库
```

## 推送分支
```
$ git push // 推送当前分支到其正在跟踪的远程仓库分支上

$ git push origin dev // 将本地 dev 分支推送至远程 origin/dev 分支（不存在则新建）。会被扩展为 refs/heads/dev:refs/heads/dev。（注意：与当前所在分支无关）
$ git push -f origin dev // 同上。但会忽略冲突，强制推送。
$ git push -u origin dev // 同上。但会自动跟踪 --set-upstream

$ git push origin l-dev:r-dev // 将本地 l-dev 分支推送至远程 origin/r-dev 分支（不存在则新建），并自动跟踪

$ git push origin :dev // 删除远程 origin/dev 分支
$ git push origin --delete dev // 删除远程 origin/dev 分支

$ git push origin --tags // 连同tag一起推送远程仓库
```

## 拉取分支
```
// fetch 拉取更新
$ git fetch origin // 从远程 origin 仓库中 /refs/heads/ 命名空间复制所有分支，并将他们存储到本地 refs/remotes/origin/ 命名空间中。并将 origin/master 分支最新 commit-id 写入到 .git/FETCH_HEAD 文件中
$ git fetch origin dev // 拉取远程 origin/dev 分支代码，并将其分支最新的 commit-id 写入到 .git/FETCH_HEAD 文件中

$ git fetch [-f] origin rbranch:lbranch // 拉取远程 origin/rbranch 分支代码，创建本地 lbranch 分支。并保存 origin/rbranch 分支的最新提交到 .git/FETCH_HEAD 文件中。若 lbranch 已存在，且不能和 origin/rbranch 分支进行 fast forward 合并，则命令执行失败。-f 则可以总是执行成功（本质上它先删除本地 lbranch 分支，再创建）
```

```
// pull 拉取并合并分支
$ git pull origin master // 将远程 origin/master 分支拉取过来，合并到当前分支
$ git pull origin r-dev:l-dev // 将远程 origin/r-dev 分支拉取过来，与本地的 l-dev 分支合并
```

```
// 可用以下 3 种方式，合并远程最新的 origin/dev 分支代码到本地 master 分支
$ git fetch origin dev  // 准备工作
$ git checkout master  // 准备工作

$ git log -p master..origin/dev // 准备工作。观察差异

$ git merge FETCH_HEAD // 1
$ git merge origin/dev  // 2
$ git pull origin dev   // 3
```

## 合并分支
```
$ git merge [--no-ff] dev // 将 dev 分支合并到当前分支。--no-ff 总是创建一个新的 merge 提交记录（不使用 fast forward 方式合并）

// 解决 merge 合并冲突
$ git merge --abort
$ git merge --continue
```


## 分支变基衍合
* 黄金法则：不要通过 rebase 对任何已经提交到仓库中公共分支的 commit 进行修改
```
// 分支变基衍合（自动 rebase）
$ git checkout dev
$ git rebase master // 将当前分支变基为 master，可有效地把 master 分支上所有新的提交合并到当前分支上。本质上，它把当前分支所有新提交（自创建该分支起）都移动到 master 分支提交记录的后面。即，它为当前分支上每一个新提交（自创建该分支起）重新再创建一个等价的新提交，并将该新提交放入 master 分支提交记录的后面。它重写了当前分支的提交历史，好处是它不会带来合并提交。
```

```
// 分支变基衍合（交互式 rebase）
$ git checkout dev
$ git rebase -i master // 打开一个文本编辑器，显示所有将被移动的提交，以进行交互式的 rebase（更精细控制 rebase）

// 交互窗口会列出当前分支最新的 commit（~越靠后，提交的时间线越近~）

// 编辑提交记录命令
pick：保留该 commit
reword：保留该 commit，但我需要修改该 commit 的注释
edit：保留该 commit，但我要停下来修改该提交（rebase 时会暂停）。
squash：将该 commit 和前一个 commit 合并
fixup：将该 commit 和前一个 commit 合并，但我不要保留该提交的注释信息
exec：执行 shell 命令
drop：我要丢弃该 commit
```

```
// 本地提交记录整理（修改提交）
// git rebase -i [startpoint] [endpoint] // 编辑从 startpoint 到 endpoint 提交记录。-i 的意思是 --interactive（交互界面）。如果不指定 [endpoint]，则该区间的终点默认是当前分支 HEAD 所指向的 commit-id（该区间指定的是一个：前开后闭的区间）

$ git rebase -i commit-id // 编辑从 commit-id 到当前最新提交的记录，也可运行 git rebase -i HEAD~3 表示编辑最近三次提交记录
```

```
// 将某一个分支一段 commit-id 粘贴到另一个分支上
// git rebase [startpoint] [endpoint] --onto [branch-name] // startpoint/endpoint 仍然和上一个命令一样指定了一个编辑区间(前开后闭)，--onto 的意思是要将该指定的提交，复制到某个分支上

// 准备将 dev 上的提交复制到 master 分支上
$ git checkout dev // 准备工作
$ git rebase 90bc0045b^ 5de0da9f2 --onto master // 将 90bc0045b 到 5de0da9f2 提交记录复制到 master 分支上

$ git checkout master
$ git reset --hard 0c72e64 // git rebase … --onto … 只是将一段提交粘贴到 master 分支所指向的提交后面（即，它只修改当前 HEAD 引用的指向，但没有修改 master 引用指向），还需要将 master 所指向的提交设置为当前 HEAD 所指向的提交 commit-id
```

```
// 合并衍合冲突时，解决冲突一般步骤
$ git status // 查看冲突文件
$ vim ... // 打开冲突文件，手动解决冲突
$ git add . // 标记文件的冲突已解决
$ git rebase --continue // 继续合并衍合
$ git rebase --abort // 放弃合并衍合
```


## 暂时将未提交的变化移除，稍后再移入
```
$ git stash
$ git stash pop
```
