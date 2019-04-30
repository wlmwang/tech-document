# GIT 修改提交对象

## 分离提交对象
```
$ git rebase -i 25e83f7 // 基准commit hash（要修改的 commit-id 的前一个提交）
	// vim 修改某个提交
	edit aefde31
	:wq
$ git log --stat
$ git reset --soft HEAD^ // 还原暂存区为上一次提交快照（即文件状态变为：已暂存、未提交）。工作区不变
$ git status
$ git reset HEAD src/* config/* // 还原暂存区为最新提交快照（即将上一步暂存区中，有关 src、config 目录下所有文件状态变为：已跟踪、未暂存）。工作区不变
$ git commit -m "commit again(new) 1" // 提交此时暂存区剩余提交
$ git add .		// 将刚刚的 src/* config/* 加入暂存区
$ git commit -m "commit again(new) 2" // 提交
$ git status
$ git rebase --continue
```

## 合并多个提交
```
$ git fetch --all origin
$ git diff pmt-47446-THRIFT-LOUPAN origin/pmt-47446-THRIFT-LOUPAN
$ git rebase -i 25e83f7 //（前一个提交日志）
	// 全部合并到最老一次提交，并可修改提交log
	// :(s1,3/pick/f/g) 快捷替换
	r ...
	f ...
	f ...
	f ...
	:wq
$ git diff pmt-47446-THRIFT-LOUPAN origin/pmt-47446-THRIFT-LOUPAN
$ git log --stat
$ git push origin pmt-47446-THRIFT-LOUPAN -f
$ git fetch --all
```

## 衍合 rebase 分支
```
$ git co pmt-47446-THRIFT-LOUPAN
$ git fetch origin // -- upstream
$ git rebase origin/master // -- upstream/master // git pull --rebase origin  master
$ git st // 冲突列表 // 若有冲突，vim 删除、修改冲突
$ git add . // 标注冲突解决（注意：此时不要 commit，直接 rebase continue）
$ git rebase --continue
$ git push origin pmt-47446-THRIFT-LOUPAN -f
```
