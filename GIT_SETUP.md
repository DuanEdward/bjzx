# Git 仓库设置指南

## ✅ 已完成

1. ✅ Git 已安装（版本: 2.39.3）
2. ✅ Git 仓库已初始化
3. ✅ .gitignore 文件已创建
4. ✅ 文件已添加到暂存区
5. ✅ 初始提交已创建

## 📝 下一步操作

### 1. 配置 Git 用户信息（如果还没有配置）

```bash
git config --global user.name "你的姓名"
git config --global user.email "your.email@example.com"
```

或者只为当前项目配置：

```bash
git config user.name "你的姓名"
git config user.email "your.email@example.com"
```

### 2. 创建远程仓库

在以下平台之一创建远程仓库：
- **GitHub**: https://github.com/new
- **GitLab**: https://gitlab.com/projects/new
- **Gitee（码云）**: https://gitee.com/projects/new
- **阿里云 Code**: https://code.aliyun.com/projects/new

### 3. 添加远程仓库

创建远程仓库后，复制仓库地址，然后执行：

```bash
# GitHub 示例
git remote add origin https://github.com/your-username/bjzxjj-website.git

# 或者使用 SSH（推荐）
git remote add origin git@github.com:your-username/bjzxjj-website.git

# Gitee 示例
git remote add origin https://gitee.com/your-username/bjzxjj-website.git
```

### 4. 推送到远程仓库

```bash
# 推送主分支
git push -u origin main

# 如果默认分支是 master，使用：
git push -u origin master
```

### 5. 查看远程仓库

```bash
git remote -v
```

## 🔄 常用 Git 命令

### 查看状态
```bash
git status
```

### 添加文件
```bash
git add .                    # 添加所有文件
git add <文件名>             # 添加指定文件
```

### 提交更改
```bash
git commit -m "提交说明"
```

### 推送到远程
```bash
git push                     # 推送到默认远程分支
git push origin main         # 推送到指定分支
```

### 拉取更新
```bash
git pull                     # 拉取并合并远程更改
git fetch                    # 只拉取不合并
```

### 查看提交历史
```bash
git log                      # 查看提交历史
git log --oneline           # 简洁模式
```

### 创建分支
```bash
git branch <分支名>          # 创建分支
git checkout <分支名>       # 切换分支
git checkout -b <分支名>    # 创建并切换分支
```

## 📋 提交规范建议

建议使用清晰的提交信息：

```bash
git commit -m "feat: 添加证书批量导入功能"
git commit -m "fix: 修复MySQL GPG密钥问题"
git commit -m "docs: 更新部署文档"
git commit -m "refactor: 重构后端服务配置"
```

提交类型：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具相关

## ⚠️ 注意事项

1. **不要提交敏感信息**：如密码、密钥等
2. **定期提交**：建议经常提交，保持提交记录清晰
3. **使用有意义的提交信息**：方便后续查看历史
4. **推送前检查**：使用 `git status` 和 `git diff` 检查更改

## 🆘 遇到问题？

### 如果推送失败
```bash
# 先拉取远程更改
git pull origin main --rebase

# 然后再推送
git push origin main
```

### 如果忘记添加 .gitignore
```bash
# 删除已跟踪的文件
git rm -r --cached .
git add .
git commit -m "更新.gitignore"
```

### 撤销更改
```bash
git checkout -- <文件>      # 撤销工作区更改
git reset HEAD <文件>       # 取消暂存
git reset --hard HEAD       # 重置到最后一次提交（危险！）
```

